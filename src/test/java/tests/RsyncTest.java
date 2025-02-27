package tests;

import framework.GCloudStorageDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RsyncTest extends GCloudStorageTests {

    /**
     * Test syncing a local folder to a GCS bucket - expected to pass.
     */
    @Test(dataProvider = "syncLocalToGCSData", dataProviderClass = GCloudStorageDataProvider.class)
    public void testSyncLocalToGCS(String localPath, String bucketPath, String... options) {
        testSyncOperation(localPath, bucketPath, true);
    }

    /**
     * Test syncing a GCS bucket to a local folder - expected to pass.
     */
    @Test(dataProvider = "syncGCSToLocalData", dataProviderClass = GCloudStorageDataProvider.class)
    public void testSyncGCSToLocal(String bucketPath, String localPath, String... options) {
        testSyncOperation(bucketPath, localPath, false);
    }
    /**
     * Test syncing a Test syncing from invalid sources (non-existent local or GCS, invalid bucket, no permissions) - expected to fail.
     */
    @Test(dataProvider = "syncFailureData", dataProviderClass = GCloudStorageDataProvider.class)
    public void testSyncFromInvalidSource(String source, String destination, boolean isLocalToGCS, String expectedError) {
        assertSyncFailure(source, destination, isLocalToGCS, expectedError);
    }

    /**
     * Test syncing file from data provider:
     * Normal case, Recursive sync, Preserve metadata, Large dataset sync, Empty folder sync, Special characters in folder name, Many small files
     * Expect to pass
     * isLocalToGCS: True for local -> GCS, False for GCS -> local
     */
    protected void testSyncOperation(String source, String destination, boolean isLocalToGCS) {
        try {
            boolean isSynced = isLocalToGCS ? manager.syncLocalToGCS(source, destination)
                    : manager.syncGCSToLocal(source, destination);
            Assert.assertTrue(isSynced, "Sync operation failed: " + source + " → " + destination);
        } catch (Exception e) {
            Assert.fail("Exception occurred during sync: " + e.getMessage());
        }
    }

    /**
     * Assertion for sync failures.
     * Determines whether the test is syncing Local -> GCS or GCS -> Local
     */
    protected void assertSyncFailure(String source, String destination, boolean isLocalToGCS, String expectedError) {
        try {
            boolean result = isLocalToGCS ? manager.syncLocalToGCS(source, destination)
                    : manager.syncGCSToLocal(source, destination);
            Assert.fail("Expected failure, but sync succeeded: " + source + " → " + destination);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains(expectedError),
                    "Unexpected error message: " + e.getMessage());
        }
    }
}
