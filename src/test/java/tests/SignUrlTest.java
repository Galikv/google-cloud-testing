package tests;

import framework.GCloudStorageDataProvider;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SignUrlTest extends GCloudStorageTests {

    public final String bucketName = GCloudStorageDataProvider.getBucketName();

    @BeforeClass
    public void uploadTestFiles() {
        Object[][] filePaths = new GCloudStorageDataProvider().uploadSignUrlFiles();
        // Upload a test file for sign URL tests
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
    @AfterClass
    public void removeTestFiles() {
        Object[][] filePaths = new GCloudStorageDataProvider().uploadSignUrlFiles();
        // Upload a test file for sign URL tests
        try {
            for (int i = 0; i < filePaths.length; i ++) {
                manager.removeFilesFromGCS(Arrays.asList((String)filePaths[i][1]));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Basic Signed URL Generation - expected to pass.
     */
    @Test(dataProvider = "validSignUrlData", dataProviderClass = GCloudStorageDataProvider.class)
    public void testSignedUrlGeneration(String filePath, Map<String, String> flags) {
        try {
            String signedUrl = manager.generateSignedUrl(filePath, flags);
            Assert.assertNotNull(signedUrl, "Signed URL failed for: " + filePath);
        } catch (Exception e) {
            Assert.fail("Exception occurred while generating signed URL: " + e.getMessage());
        }
    }

    /**
     * Short-Lived Links - expected to pass.
     */
    @Test(dataProvider = "ShortLivedSignedUrl", dataProviderClass = GCloudStorageDataProvider.class)
    public void testShortLivedSignedUrl(String filePath, Map<String, String> flags) throws InterruptedException {
        try {
            String signedUrl = manager.generateSignedUrl(filePath, flags);

            Assert.assertNotNull(signedUrl, "Signed URL should be generated.");
            Assert.assertFalse(signedUrl.isEmpty(), "Signed URL should not be empty.");

            // Convert duration from string to seconds
            String durationStr = flags.get("duration");
            int duration = Integer.parseInt(flags.get("duration").substring(0, flags.get("duration").length() - 1));

            Thread.sleep(duration * 1000L - 10);

            // Verify that the signed URL is expired by making an HTTP request
            boolean isAccessible = verifySignedUrlSecurity(signedUrl);
            Assert.assertFalse(isAccessible, "Short-lived signed URL should expire and be inaccessible.");
        } catch (Exception e) {
            Assert.fail("Unexpected error message: " + e.getMessage());
        }
    }

    /** Try signing a URL for an object/bucket that does not exist - expected to fail.*/
    @Test(dataProvider = "invalidPath", dataProviderClass = GCloudStorageDataProvider.class)
    public void testSignUrlWithInvalidObject(String filePath, Map<String, String> flags) {
        try {
            String signedUrl = manager.generateSignedUrl(filePath, flags);
            Assert.fail("Signing a URL for a non-existent object/bucket should fail.");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("does not exist") || e.getMessage().contains("'FileUrl' object has no attribute 'is_provider'"),
                    "Unexpected error message: " + e.getMessage());
        }
    }


    /** Signed URL Should Be Accessible Without Phishing Warnings */
   /* @Test(dataProvider = "validSignUrlData", dataProviderClass = GCloudStorageDataProvider.class)
    public void testSignedUrlDoesNotTriggerPhishingWarnings(String filePath, Map<String, String> flags) {
        String signedUrl = null;
        try {
            signedUrl = manager.generateSignedUrl(filePath, flags);
            Assert.assertNotNull(signedUrl, "Signed URL should be generated successfully.");
        } catch (Exception e) {
            Assert.fail("Exception occurred while generating signed URL: " + e.getMessage());
        }

        boolean isSafe = verifySignedUrlSecurity(signedUrl);
        Assert.assertTrue(isSafe, "Signed URL should be accessible without phishing warnings.");
    }

    /** Verifies if Signed URL is Safe (No Phishing Warnings) */
    private boolean verifySignedUrlSecurity(String signedUrl) {
        try {
            URL url = new URL(signedUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.connect();

            int responseCode = connection.getResponseCode();
            return responseCode == 200; // URL is accessible if it returns 200 OK
        } catch (Exception e) {
            System.err.println("Security check failed: " + e.getMessage());
            return false; // Treat failures as potential phishing issues
        }
    }
    /** Tests i would like to add:
     * Test singUrl while internet is disconnected
     * Test Sign a URL for a very large object (e.g., >5GB)
     * Test Sign URLs for 100+ objects in a single command
    */

}
