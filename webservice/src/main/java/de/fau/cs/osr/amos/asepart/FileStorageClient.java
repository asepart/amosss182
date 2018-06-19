package de.fau.cs.osr.amos.asepart;

import java.io.InputStream;
import java.util.LinkedList;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.messages.Item;

class FileStorageClient
{
    private MinioClient client;
    private final String bucketName;

    FileStorageClient() throws Exception
    {
        final String minioUrl = System.getenv("ASEPART_MINIO_URL");
        final String minioAccessKey = System.getenv("MINIO_ACCESS_KEY");
        final String minioSecretKey = System.getenv("MINIO_SECRET_KEY");

        bucketName = System.getenv("ASEPART_MINIO_BUCKET");

        if (minioUrl == null || minioAccessKey == null || minioSecretKey == null || bucketName == null)
            throw new UnsupportedOperationException("Environment variables for Minio were not configured!");

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

        if (!client.bucketExists(bucketName))
            client.makeBucket(bucketName);
    }

    private static String internalName(int ticketId, String fileName)
    {
        return "ticket:" + String.valueOf(ticketId) + ":" + fileName;
    }

    public void upload(int ticketId, String fileName, InputStream fileStream) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);
        client.statObject(bucketName, fileId); // throws exception if file does not exist
        client.putObject(bucketName, fileId, fileStream, "application/octet-stream");
    }

    public InputStream download(int ticketId, String fileName) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);
        return client.getObject(bucketName, fileId);
    }

    public void remove(int ticketId, String fileName) throws Exception
    {
        final String fileId = internalName(ticketId, fileName);
        client.removeObject(bucketName, fileId);
    }

    public void cascade(int ticketId) throws Exception
    {
        Iterable<Result<Item>> results = client.listObjects(bucketName, "ticket:" + String.valueOf(ticketId) + ":");
        LinkedList<String> trash = new LinkedList<>();

        for (Result<Item> result : results)
        {
            Item item = result.get();
            trash.add(item.objectName());
        }

        client.removeObject(bucketName, trash);
    }
}
