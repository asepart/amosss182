package de.fau.cs.osr.amos.asepart;

import java.io.File;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.LinkedList;

import io.minio.ErrorCode;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.messages.Item;

import net.coobird.thumbnailator.Thumbnails;

class FileStorageClient
{
    private MinioClient client;

    private final String fileBucket;
    private final String thumbnailBucket;

    FileStorageClient() throws Exception
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

    String download(int ticketId, String fileName) throws Exception
    {
        return download(ticketId, fileName, fileBucket);
    }

    void upload(int ticketId, String fileName, InputStream fileStream) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);

        if (!exists(ticketId, fileName))
        {
            String contentType;

            if (isImageFile(fileName))
            {
                contentType = "image/" + getExtension(fileName);
            }

            else contentType = "application/octet-stream";

            client.putObject(fileBucket, fileId, fileStream, contentType);
        }

        else throw new FileAlreadyExistsException("File with same name already exists for this ticket.");

        if (isImageFile(fileName))
            generateThumbnail(fileId);
    }

    boolean exists(int ticketId, String fileName) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);
        return exists(fileId, fileBucket);
    }

    private static boolean isImageFile(String fileName)
    {
        return fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".bmp");
    }

    private static String getExtension(String fileName)
    {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private void generateThumbnail(String fileId) throws Exception
    {
        final String extension = getExtension(fileId);

        File cacheFile = File.createTempFile("asepart-", "-cache." + extension);
        File thumbFile = File.createTempFile("asepart-", "-thumbnail." + extension);

        cacheFile.deleteOnExit();
        thumbFile.deleteOnExit();

        client.getObject(fileBucket, fileId, cacheFile.getAbsolutePath());
        Thumbnails.of(cacheFile).size(256, 256).toFile(thumbFile.getAbsoluteFile());
        client.putObject(thumbnailBucket, fileId, thumbFile.getAbsolutePath());
    }

    boolean hasThumbnail(int ticketId, String fileName) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);
        return exists(fileId, thumbnailBucket);
    }

    String getThumbnail(int ticketId, String fileName) throws Exception
    {
        return download(ticketId, fileName, thumbnailBucket);
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

    private String download(int ticketId, String fileName, String bucket) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);
        return client.presignedGetObject(bucket, fileId, 86400);
    }

    void remove(int ticketId, String fileName) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);
        client.removeObject(fileBucket, fileId);

        if (exists(fileId, thumbnailBucket))
            client.removeObject(thumbnailBucket, fileId);
    }

    void cascade(int ticketId) throws Exception
    {
        cascade(ticketId, fileBucket);
        cascade(ticketId, thumbnailBucket);
    }

    private void cascade(int ticketId, String bucket) throws Exception
    {
        Iterable<Result<Item>> results = client.listObjects(bucket, "ticket:" + String.valueOf(ticketId) + ":");
        LinkedList<String> trash = new LinkedList<>();

        for (Result<Item> result : results)
        {
            Item item = result.get();
            trash.add(item.objectName());
        }

        client.removeObject(bucket, trash);
    }
}
