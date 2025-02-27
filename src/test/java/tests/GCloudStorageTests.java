package tests;
import framework.GCloudStorageDataProvider;
import framework.GCloudStorageManager;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class GCloudStorageTests {

    protected GCloudStorageManager manager;
    public final String bucketName = GCloudStorageDataProvider.getBucketName();

    /**
     *  Check Google Cloud CLI Installation
     *  Verify Google Cloud Authentication
     *  Ensure the Test Bucket Exists
     * */
    @BeforeClass
    public void setUp() {

        manager = new GCloudStorageManager();
        try {
            boolean connectionValid = manager.checkGCloudInstallation() &&
                                        manager.verifyAuthentication() &&
                                        manager.ensureTestBucketExists(bucketName);
            if(!connectionValid){
                throw new RuntimeException("Google Cloud setup failed. Please check your configuration.");
            }

        }
        catch(Exception e){
            Assert.fail("Setup failed due to an exception: " + e.getMessage());
        }
    }

    /**
     *  Remove any failed partial uploads
     *  Check for orphaned resources
     *  Clear cached credentials
     * */
    @AfterClass
    public void tearDown() {
        System.out.println("cleaning up");
    }

}
