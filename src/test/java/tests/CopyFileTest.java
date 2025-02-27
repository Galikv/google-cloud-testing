package tests;
import framework.GCloudStorageDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileOutputStream;

public class CopyFileTest extends GCloudStorageTests{

    public final String bucketName = GCloudStorageDataProvider.getBucketName();
    /**
    * Test copying file from data provider:
    * Normal case, Large file, Wildcard copy, Special character, Empty file case
    * Expect to pass.
    * */
    @Test(dataProvider = "copyFileTestData", dataProviderClass = GCloudStorageDataProvider.class)
    public void testCopyFile(String source, String destination){
        try {
            boolean isCopied = manager.copyFileToGCS(source, destination);
            Assert.assertTrue(isCopied, "File copy operation failed");
        }
        catch (Exception e){
            Assert.fail("Exception occurred while copying file: " + e.getMessage());
        }
    }

    @Test
    public void testSignUrlForLargeObject() {
        try {
            long sizeInBytes = 5L * 1024 * 1024 * 1024;
            byte[] buffer = new byte[1024 * 1024]; // 1MB buffer
            try (FileOutputStream fos = new FileOutputStream("copy-large-file.txt")) {
                for (long written = 0; written < sizeInBytes; written += buffer.length) {
                    fos.write(buffer);
                }
                boolean uploadSuccess = manager.copyFileToGCS("copy-large-file.txt", "gs://my-gcloud-testing-bucket/copy-large-file1.txt");
                Assert.assertTrue(uploadSuccess, "File upload to GCS failed.");

            } catch (Exception e) {
                Assert.fail("Exception occurred while copying a large object: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  Test copying a file that does not exist - expected to fail.
     */
    @Test
    public void testCopyNonExistentFile() {
        String localFile = "nonexistent.txt";
        String gcsPath = "gs://" + bucketName + "/missing-file.txt";

        try{
            boolean isCopied = manager.copyFileToGCS(localFile, gcsPath);
            Assert.fail("Copying a file that does not exist: expected an exception, but the method executed successfully.");
        }
        catch (Exception e){
            // Verify that the error message is related to a missing file
            Assert.assertTrue(e.getMessage().contains("The following URLs matched no objects or files"),
                    "Unexpected error message: " + e.getMessage());

        }
    }
    /**
     * Test copying with an invalid GCS path - expected to fail.
     */
    @Test
    public void testCopyToInvalidGCSPath() {
        String localFile = "local-file.txt";
        String gcsPath = "invalid-path";

        try{
            boolean isCopied = manager.copyFileToGCS(localFile, gcsPath);
            Assert.fail("Copying with an invalid GCS path: expected an exception, but the method executed successfully.");
        }
        catch (Exception e){
            // Verify that the error message is related to a missing file
            Assert.assertTrue(e.getMessage().contains("Local copies not supported"), "Unexpected error message: " + e.getMessage());
        }
    }
    /**
     * Test copying with an invalid Local path - expected to fail.
     */
    @Test
    public void testCopyFromInvalidLocalPath() {
        String localFile = "invalid-path";
        String gcsPath = "gs://" + bucketName + "//file1.txt";

        try{
            boolean isCopied = manager.copyFileToGCS(localFile, gcsPath);
            Assert.fail("Copying with an invalid Local path: expected an exception, but the method executed successfully.");
        }
        catch (Exception e){
            // Verify that the error message is related to a missing file
            Assert.assertTrue(e.getMessage().contains("The following URLs matched no objects or files"), "Unexpected error message: " + e.getMessage());
        }
    }
    /** Tests i would like to add:
     * Test copying while internet is disconnected
     * Test copying hidden files
     * Test copying many files at once
     * Test copying many files and deleting simultaneously*/
}
