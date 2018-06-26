package de.fau.cs.osr.amos.asepart.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.LinkedList;
import javax.imageio.ImageIO;

import io.minio.ErrorCode;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.messages.Item;

import net.coobird.thumbnailator.Thumbnails;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

/**
 * This class is a wrapper for accessing
 * an minio file server, which is an
 * open source implementation of the
 * Amazon S3 protocol.
 */

public class FileStorageClient
{
    private MinioClient client;

    private final String fileBucket;
    private final String thumbnailBucket;

    public FileStorageClient() throws Exception
    {
        final String minioUrl = System.getenv("ASEPART_MINIO_URL");
        final String minioAccessKey = System.getenv("MINIO_ACCESS_KEY");
        final String minioSecretKey = System.getenv("MINIO_SECRET_KEY");

        fileBucket = System.getenv("ASEPART_MINIO_BUCKET");

        if (minioUrl == null || minioAccessKey == null || minioSecretKey == null || fileBucket == null)
            throw new UnsupportedOperationException("Environment variables for Minio were not configured!");

        thumbnailBucket = fileBucket + "-thumbs";

        try
        {
            client = new MinioClient(minioUrl, minioAccessKey, minioSecretKey);
        }

        catch (InvalidEndpointException e)
        {
            throw new IllegalArgumentException("Minio endpoint is invalid.");
        }

        catch (InvalidPortException e)
        {
            throw new IllegalArgumentException("Minio port is invalid.");
        }

        if (!client.bucketExists(fileBucket))
            client.makeBucket(fileBucket);

        if (!client.bucketExists(thumbnailBucket))
            client.makeBucket(thumbnailBucket);
    }

    private static String internalName(int ticketId, String fileName)
    {
        return "ticket:" + String.valueOf(ticketId) + ":" + fileName;
    }

    private static boolean isImageFile(String fileName)
    {
        return fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".bmp");
    }

    private static boolean isVideoFile(String fileName)
    {
        return fileName.endsWith(".mp4") || fileName.endsWith(".mkv");
    }

    private static String getExtension(String fileName)
    {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private static String getVideoThumbnailName(String fileName)
    {
        return fileName + ".png";
    }

    private String download(int ticketId, String fileName, String bucket) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);
        return client.presignedGetObject(bucket, fileId, 86400);
    }

    /**
     * Returns an url where the file can be found.
     *
     * @param ticketId Unique id of ticket.
     * @param fileName The name of the file.
     * @return URL which can be used to GET the file.
     */

    public String download(int ticketId, String fileName) throws Exception
    {
        return download(ticketId, fileName, fileBucket);
    }

    /**
     * Uploads a file to the server.
     *
     * @param ticketId Unique id of ticket.
     * @param fileName The name of the file.
     * @param fileStream Stream containing the file's contents.
     */

    public void upload(int ticketId, String fileName, InputStream fileStream) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);

        if (!exists(ticketId, fileName))
        {
            String contentType;

            if (isImageFile(fileName))
            {
                contentType = "image/" + getExtension(fileName);
            }

            else if (isVideoFile(fileName))
            {
                contentType = "video/" + getExtension(fileName);
            }

            else contentType = "application/octet-stream";

            client.putObject(fileBucket, fileId, fileStream, contentType);
        }

        else throw new FileAlreadyExistsException("File with same name already exists for this ticket.");

        if (isImageFile(fileName) || isVideoFile(fileName))
            generateThumbnail(fileId);
    }

    private void generateThumbnail(String fileId) throws Exception
    {
        final String extension = getExtension(fileId);

        File cacheFile = File.createTempFile("asepart-", "-cache." + extension);
        cacheFile.deleteOnExit();

        client.getObject(fileBucket, fileId, cacheFile.getAbsolutePath());

        if (isImageFile(fileId))
        {
            File thumbFile = File.createTempFile("asepart-", "-thumbnail." + extension);
            thumbFile.deleteOnExit();

            Thumbnails.of(cacheFile).size(256, 256).toFile(thumbFile.getAbsoluteFile());
            client.putObject(thumbnailBucket, fileId, thumbFile.getAbsolutePath());
        }

        else if (isVideoFile(fileId))
        {
            File thumbFile = File.createTempFile("asepart-", "-thumbnail.png");
            thumbFile.deleteOnExit();

            Picture picture = FrameGrab.getFrameFromFile(cacheFile, 0);
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            ImageIO.write(bufferedImage, "png", thumbFile);

            String thumbnailId = getVideoThumbnailName(fileId);
            client.putObject(thumbnailBucket, thumbnailId, thumbFile.getAbsolutePath());
        }

        else throw new IllegalArgumentException("File is neither an image nor a video!");
    }

    /**
     * Checks if a file has a thumbnail available.
     *
     * @param ticketId Unique id of ticket.
     * @param fileName The name of the file.
     * @return true if thumbnail exists, false if not.
     */

    public boolean hasThumbnail(int ticketId, String fileName) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);

        if (isImageFile(fileId))
            return exists(fileId, thumbnailBucket);
        else if (isVideoFile(fileId))
            return exists(getVideoThumbnailName(fileId), thumbnailBucket);
        else return false;
    }

    /**
     * Returns an url where the thumbnail of the file can be found.
     *
     * @param ticketId Unique id of ticket.
     * @param fileName The name of the file.
     * @return URL which can be used to GET the thumbnail.
     */

    public String getThumbnail(int ticketId, String fileName) throws Exception
    {
        if (isImageFile(fileName))
            return download(ticketId, fileName, thumbnailBucket);
        else if (isVideoFile(fileName))
            return download(ticketId, getVideoThumbnailName(fileName), thumbnailBucket);
        else throw new IllegalArgumentException("File is neither an image nor a video!");
    }

    /**
     * Checks if a file exists.
     *
     * @param ticketId Unique id of ticket.
     * @param fileName The name of the file.
     * @return true if file exists, false if not.
     */

    public boolean exists(int ticketId, String fileName) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);
        return exists(fileId, fileBucket);
    }

    private boolean exists(String fileId, String bucket) throws Exception
    {
        boolean fileExists = true;

        try
        {
            client.statObject(bucket, fileId);
        }

        catch (ErrorResponseException e)
        {
            if (e.errorResponse().errorCode().code().equals(ErrorCode.NO_SUCH_KEY.code()))
                fileExists = false;
        }

        return fileExists;
    }

    /**
     * Delete a file.
     *
     * @param ticketId Unique id of ticket.
     * @param fileName The name of the file.
     */

    public void remove(int ticketId, String fileName) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);
        client.removeObject(fileBucket, fileId);

        if (hasThumbnail(ticketId, fileName))
        {
            if (isImageFile(fileName))
                client.removeObject(thumbnailBucket, fileId);
            if (isVideoFile(fileName))
                client.removeObject(thumbnailBucket, getVideoThumbnailName(fileId));
        }
    }

    /**
     * Delete all files related to a ticket.
     *
     * @param ticketId Unique id of ticket.
     */

    public void cascade(int ticketId) throws Exception
    {
        Iterable<Result<Item>> results = client.listObjects(fileBucket, "ticket:" + String.valueOf(ticketId) + ":");
        LinkedList<String> trash = new LinkedList<>();

        for (Result<Item> result : results)
        {
            Item item = result.get();
            trash.add(item.objectName());
        }

        client.removeObject(fileBucket, trash);
        client.removeObject(thumbnailBucket, trash);

        for (String fileId : trash)
        {
            if (isVideoFile(fileId))
                client.removeObject(thumbnailBucket, getVideoThumbnailName(fileId));
        }
    }
}
