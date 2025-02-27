package framework;

import utils.CommandExecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.Map;

/*
This class will be responsible for interacting with Google Cloud Storage (GCS) CLI commands.
cp (Copy files to/from GCS)
rm (Remove files from GCS)
sign-url (Generate signed URLs)
rsync (Synchronize files between local and GCS)
*/
public class GCloudStorageManager {

    private final CommandExecutor commandExecutor = new CommandExecutor(); // Initialize CommandExecutor
    private final String gcloudPath = "C:\\Users\\Puffy\\google-storage-test\\";
    private final String serviceAccountKey = "src\\test\\java\\resources\\service-account.json";

    public boolean copyFileToGCS(String sourcePath, String destinationPath) throws IOException, InterruptedException {

        String command = "gcloud storage cp \"" + gcloudPath + sourcePath + "\" " + destinationPath;
        return commandExecutor.executeCommand(command);

    }
    public boolean removeFilesFromGCS(List<String> filePaths) throws IOException, InterruptedException {
        if (filePaths.isEmpty()) return false;
        String command = "gcloud storage rm " + String.join(" ", filePaths);
        return commandExecutor.executeCommand(command);
    }

    public String generateSignedUrl(String filePath, Map<String, String> flags) throws IOException, InterruptedException {
        String command = String.format("gcloud storage sign-url %s --private-key-file=%s", filePath, serviceAccountKey);
        for (Map.Entry<String, String> entry : flags.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                command += " --" + entry.getKey() + "=" + entry.getValue();
            }
        }
        command += " --region=us-central1";
        return commandExecutor.executeCommandString(command);
    }
    public boolean syncLocalToGCS(String localPath, String bucketPath) throws IOException, InterruptedException {
        String command = "gcloud storage rsync -r" + " \"" + localPath + "\" " + bucketPath;
        return commandExecutor.executeCommand(command);
    }

    public boolean syncGCSToLocal(String bucketPath, String localPath) throws IOException, InterruptedException {
        String command = "gcloud storage rsync -r"  + " " + bucketPath + " \"" + localPath + "\"";
        return commandExecutor.executeCommand(command);
    }

    // Verify Google Cloud Installation
    public boolean checkGCloudInstallation() throws IOException, InterruptedException{
        String command = "gcloud --version";
        return commandExecutor.executeCommand(command);
    }

    // Verify Google Cloud Authentication
    public boolean verifyAuthentication() throws IOException, InterruptedException{
        String command = "gcloud auth list";
        return commandExecutor.executeCommand(command);
    }

    //  Ensure the Test Bucket Exists
    public boolean ensureTestBucketExists(String testBucketName) throws IOException, InterruptedException{
        String command = "gcloud storage buckets describe gs://" + testBucketName;
        return commandExecutor.executeCommand(command);
    }

     //Removes failed partial uploads from the bucket.
    public boolean removePartialUploads(String bucketName) throws IOException, InterruptedException{
        return commandExecutor.executeCommand("gcloud storage objects list --prefix=temp/ --format=json | jq -r '.[].name' | xargs -I {} gcloud storage objects delete " + bucketName + "/{}");
    }

    // Cleans up orphaned resources in the test bucket.
    public boolean cleanupOrphanedResources(String bucketName) throws IOException, InterruptedException{
        return commandExecutor.executeCommand("gcloud storage objects list " + bucketName + " --format=json | jq -r '.[].name' | xargs -I {} gcloud storage objects delete " + bucketName + "/{}");
    }

    // Revokes authentication (optional step).
    public boolean revokeAuthentication() throws IOException, InterruptedException{
        return commandExecutor.executeCommand("gcloud auth revoke");
    }

    // Create Test Files for Multiple Objects
    public List<String> createTestFiles(int numFiles) throws IOException {
        String bucketPath = "gs://my-gcloud-testing-bucket/";
        List<String> filePaths = new java.util.ArrayList<>();
        try {
            for (int i = 0; i < numFiles; i++) {
                String fileName = "test-file-" + i + ".txt";
                File file = new File("/tmp/" + fileName);
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("This is test file " + i);
                }
                filePaths.add(bucketPath + fileName);
                copyFileToGCS("/tmp/" + fileName, bucketPath);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return filePaths;
    }

    public void createLargeFile(String filePath, long sizeInBytes) throws IOException, InterruptedException {
        byte[] buffer = new byte[1024 * 1024]; // 1MB buffer
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            for (long written = 0; written < sizeInBytes; written += buffer.length) {
                fos.write(buffer);
            }
            copyFileToGCS(filePath, "gs://my-gcould-testing-bucket/filePath");
        }
        System.out.println("Large file created successfully: " + filePath);
    }

}
