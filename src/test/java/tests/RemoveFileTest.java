package tests;

import framework.GCloudStorageDataProvider;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.List;

public class RemoveFileTest extends GCloudStorageTests{

    public final String bucketName = GCloudStorageDataProvider.getBucketName();

    @BeforeClass
    public void uploadTestFiles() {
        Object[][] filePaths = new GCloudStorageDataProvider().uploadRemoveFiles(); // Call data provider manually
        // Upload a test file for removal tests
        try {
            for (int i = 0; i < filePaths.length; i ++) {
                manager.copyFileToGCS((String)filePaths[i][0], (String)filePaths[i][1]);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Test removing file from data provider:
     * Normal case, Nested files, large files, Single empty file, Bulk deletion (1000+ files), Bulk deletion (5000+ files), Mixed file types deletion, Wildcard pattern deletion, Cross-bucket deletion.
     * Expect to pass
     * */
    @Test(dataProvider = "removeFilesTestData", dataProviderClass = GCloudStorageDataProvider.class)
    public void testRemoveFiles(List<String> filePaths) {
        try {
            boolean isRemoved = manager.removeFilesFromGCS(filePaths);
            Assert.assertTrue(isRemoved, "File removal failed for: " + filePaths);
        } catch (Exception e) {
            Assert.fail("Exception occurred while removing files: " + e.getMessage());
        }
    }

    /**
     * Test removing non-existent files - expected to fail.
     */
    @Test
    public void testRemoveNonExistentFiles() {
        List<String> filePaths = List.of(
                "gs://" + bucketName + "/nonexistent1.txt",
                "gs://" + bucketName + "/nonexistent2.txt"
        );
        try {
            boolean isRemoved = manager.removeFilesFromGCS(filePaths);
            Assert.fail("Removing non-existent files: expected an exception, but the method executed successfully.");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("The following URLs matched no objects or files"),
                    "Unexpected error message: " + e.getMessage());
        }
    }

    /**
     * Test removing files from a non-existent bucket - expected to fail.
     */
    @Test
    public void testRemoveFilesFromNonExistentBucket() {
        List<String> filePaths = List.of(
                "gs://invalid-bucket/file1.txt",
                "gs://invalid-bucket/file2.txt"
        );
        try {
            boolean isRemoved = manager.removeFilesFromGCS(filePaths);
            Assert.fail("Removing files from a non-existent bucket: expected an exception, but the method executed successfully.");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("The billing account for the owning project is disabled in state closed"),
                    "Unexpected error message: " + e.getMessage());
        }
    }
    /** Tests i would like to add:
     * Test Removing Folder Instead Of File
     * Test removing many files at once
     * Test copying many files and deleting simultaneously */

}
