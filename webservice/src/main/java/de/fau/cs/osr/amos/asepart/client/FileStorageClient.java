package de.fau.cs.osr.amos.asepart.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;

import io.minio.ErrorCode;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;

import jdk.internal.util.xml.impl.Input;
import net.coobird.thumbnailator.Thumbnails;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

/**
 * This class is a wrapper for accessing an minio file server, which is an
 * open source implementation of the Amazon S3 protocol.
 */

public class FileStorageClient implements AutoCloseable
{
    private MinioClient minioClient;
    private DatabaseClient dbClient;

    private final String fileBucket;
    private final String thumbnailBucket;

    public FileStorageClient() throws Exception
    {
        final String minioAccessKey = System.getenv("MINIO_ACCESS_KEY");
        final String minioSecretKey = System.getenv("MINIO_SECRET_KEY");

        final String minioUrl = System.getenv("ASEPART_MINIO_URL");
        fileBucket = System.getenv("ASEPART_MINIO_BUCKET");

        if (minioUrl == null || minioAccessKey == null || minioSecretKey == null || fileBucket == null)
            throw new UnsupportedOperationException("Environment variables for Minio were not configured!");

        thumbnailBucket = fileBucket + "-thumbs";

        try
        {
            minioClient = new MinioClient(minioUrl, minioAccessKey, minioSecretKey);
        }

        catch (InvalidEndpointException e)
        {
            throw new IllegalArgumentException("Minio endpoint is invalid.");
        }

        catch (InvalidPortException e)
        {
            throw new IllegalArgumentException("Minio port is invalid.");
        }

        if (!minioClient.bucketExists(fileBucket))
            minioClient.makeBucket(fileBucket);

        if (!minioClient.bucketExists(thumbnailBucket))
            minioClient.makeBucket(thumbnailBucket);

        dbClient = new DatabaseClient();
    }

    @Override
    public void close() throws Exception
    {
        dbClient.close();
    }

    private static String internalName(String extension)
    {
        final UUID uuid = UUID.randomUUID();
        return uuid + "." + extension;
    }

    private static boolean isImageFile(String originalName)
    {
        originalName = originalName.toLowerCase();
        return originalName.endsWith(".jpg") || originalName.endsWith(".png") || originalName.endsWith(".bmp");
    }

    private static boolean isVideoFile(String originalName)
    {
        originalName = originalName.toLowerCase();
        return originalName.endsWith(".mp4") || originalName.endsWith(".mov");
    }

    private static String getContentType(String originalName)
    {
        switch (getExtension(originalName))
        {
            case "jpg": return "image/jpg";
            case "png": return "image/png";
            case "bmp": return "image/bmp";
            case "mp4": return "video/mp4";
            case "mov": return "video/quicktime";
            default: throw new UnsupportedOperationException("File extension not supported.");
        }
    }

    private static String getExtension(String fileName)
    {
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    private String download(String bucket, String file) throws Exception
    {
        return minioClient.presignedGetObject(bucket, file, 86400);
    }

    /**
     * Returns an url where the file can be found.
     *
     * @param metadataId Metadata id of file.
     * @return URL which can be used to GET the file.
     */

    public String download(int metadataId) throws Exception
    {
        Map<String, String> fileInfo = dbClient.getFile(metadataId);
        return download(fileBucket, fileInfo.get("internalName"));
    }

    /**
     * Uploads a file to the server.
     *
     * @param ticketId Unique id of ticket.
     * @param fileName The name of the file.
     * @param fileStream Stream containing the file's contents.
     *
     * @return Metadata id of file.
     */

    public int upload(int ticketId, String fileName, InputStream fileStream) throws Exception
    {
        final String fileId = internalName(getExtension(fileName));
        String contentType;

        try
        {
            contentType = getContentType(fileName);
        }

        catch (UnsupportedOperationException e)
        {
            contentType = "application/octet-stream";
        }

        if (isImageFile(fileName))
            minioClient.putObject(fileBucket, fileId, resizeImage(fileStream), contentType);

        else minioClient.putObject(fileBucket, fileId, fileStream, contentType);

        String thumbnailName = null;

        if (isImageFile(fileName) || isVideoFile(fileName))
            thumbnailName = generateThumbnail(fileId);

        try
        {
            return dbClient.registerFile(fileId, thumbnailName, fileName, ticketId);
        }

        catch (SQLException sqlex)
        {
            minioClient.removeObject(fileBucket, fileId);
            if (thumbnailName != null) minioClient.removeObject(thumbnailName, thumbnailName);

            throw sqlex;
        }
    }

    private InputStream resizeImage(InputStream img) throws Exception
    {
        PipedInputStream istream = new PipedInputStream();
        PipedOutputStream ostream = new PipedOutputStream(istream);

        Thumbnails.of(img).size(1000, 1000).toOutputStream(ostream);

        return istream;
    }

    private String generateThumbnail(String fileId) throws Exception
    {
        final String extension = getExtension(fileId);

        File cacheFile = File.createTempFile("asepart-", "-cache." + extension);
        cacheFile.deleteOnExit();

        minioClient.getObject(fileBucket, fileId, cacheFile.getAbsolutePath());

        if (isImageFile(fileId))
        {
            File thumbFile = File.createTempFile("asepart-", "-thumbnail." + extension);
            thumbFile.deleteOnExit();

            Thumbnails.of(cacheFile).size(256, 256).toFile(thumbFile.getAbsoluteFile());
            minioClient.putObject(thumbnailBucket, fileId, thumbFile.getAbsolutePath());

            return fileId;
        }

        else if (isVideoFile(fileId))
        {
            File thumbFile = File.createTempFile("asepart-", "-thumbnail.png");
            thumbFile.deleteOnExit();

            Picture picture = FrameGrab.getFrameFromFile(cacheFile, 0);
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            ImageIO.write(bufferedImage, "png", thumbFile);

            String thumbnailId = internalName("png");
            minioClient.putObject(thumbnailBucket, thumbnailId, thumbFile.getAbsolutePath());

            return thumbnailId;
        }

        else throw new IllegalArgumentException("File is neither an image nor a video!");
    }

    /**
     * Checks if a file has a thumbnail available.
     *
     * @param metadataId Metadata id of file.
     * @return true if thumbnail exists, false if not.
     */

    public boolean hasThumbnail(int metadataId) throws Exception
    {
        Map<String, String> fileInfo = dbClient.getFile(metadataId);
        return fileInfo.get("thumbnailName") != null;
    }

    /**
     * Returns an url where the thumbnail of the file can be found.
     *
     * @param metadataId Metadata id of file.
     * @return URL which can be used to GET the thumbnail.
     */

    public String getThumbnail(int metadataId) throws Exception
    {
        if (!hasThumbnail(metadataId))
            throw new IllegalArgumentException("File is neither an image nor a video!");

        Map<String, String> fileInfo = dbClient.getFile(metadataId);
        return download(thumbnailBucket, fileInfo.get("thumbnailName"));
    }

    /**
     * Checks if a file exists.
     *
     * @param bucket The bucket to check.
     * @param file The internal name of the file.
     * @return true if file exists, false if not.
     */

    @Deprecated
    public boolean exists(String bucket, String file) throws Exception
    {
        boolean fileExists = true;

        try
        {
            minioClient.statObject(bucket, file);
        }

        catch (ErrorResponseException e)
        {
            if (e.errorResponse().errorCode().code().equals(ErrorCode.NO_SUCH_KEY.code()))
                fileExists = false;
        }

        return fileExists;
    }

    public boolean exists(int metadataId) throws Exception
    {
        return dbClient.isFile(metadataId);
    }

    /**
     * Delete a file.
     *
     * @param metadataId Metadata id of file.
     */

    public void remove(int metadataId) throws Exception
    {
        Map<String, String> fileInfo = dbClient.getFile(metadataId);
        minioClient.removeObject(fileBucket, fileInfo.get("internalName"));

        final String thumbnailName = fileInfo.get("thumbnailName");

        if (thumbnailName != null)
            minioClient.removeObject(thumbnailBucket, thumbnailName);

        dbClient.unregisterFile(metadataId);
    }

    /**
     * Delete all files that are not related to a ticket.
     */

    public void killOrphans() throws Exception
    {
        List<Map<String, String>> fileList = dbClient.listOrphans();

        for (Map<String, String> fileInfo : fileList)
        {
            int metadataId = Integer.parseInt(fileInfo.get("id"));
            remove(metadataId);
        }
    }
}
