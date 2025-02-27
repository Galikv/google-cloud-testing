package framework;

import org.testng.annotations.DataProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class GCloudStorageDataProvider {

    public static final String BUCKET_NAME = "my-gcould-testing-bucket";

    public static String getBucketName() {
        return BUCKET_NAME;
    }

    @DataProvider(name = "uploadSignUrlFiles")
    public Object[][] uploadSignUrlFiles(){
        return new Object [][]{
                {"file1.txt", "gs://" + BUCKET_NAME + "/signUrl-file1.txt"},
                {"file2.txt", "gs://" + BUCKET_NAME + "/signUrl-file2.txt"},
                {"file3.txt", "gs://" + BUCKET_NAME + "/signUrl-file3.txt"}
        };
    }
    @DataProvider(name = "validSignUrlData")
    public Object[][] validSignUrlData() {
        return new Object[][] {
                {"gs://" + BUCKET_NAME + "/signUrl-file1.txt", Map.of("http-verb", "GET", "duration", "10m")},
                {"gs://" + BUCKET_NAME + "/signUrl-file2.txt", Map.of("http-verb", "PUT", "duration", "1h")},
                {"gs://" + BUCKET_NAME + "/signUrl-file3.txt", Map.of("http-verb", "DELETE", "duration", "12h")}
        };
    }

    @DataProvider(name = "invalidExpirationData")
    public Object[][] invalidExpirationData() {
        return new Object[][]{
                {"gs://" + BUCKET_NAME + "/signUrl-file1.txt", Map.of("duration", "1s")},
                {"gs://" + BUCKET_NAME + "/signUrl-file1.txt", Map.of("duration", "99999999h")},
                {"gs://" + BUCKET_NAME + "/signUrl-file1.txt", Map.of("duration", "-10m")},
                {"gs://" + BUCKET_NAME + "/signUrl-file1.txt", Map.of("duration", "abc")},
                {"gs://" + BUCKET_NAME + "/signUrl-file1.txt", Map.of("duration", "0h")},
                {"gs://" + BUCKET_NAME + "/signUrl-file1.txt", Map.of("duration", "0.0000000000000000000000000001s")}
        };
    }
    @DataProvider(name = "invalidMethodData")
    public Object[][] invalidMethodData() {
        return new Object[][]{
                {"gs://" + BUCKET_NAME + "/signUrl-file1.txt", Map.of("http-verb", "pot")},
                {"gs://" + BUCKET_NAME + "/signUrl-file1.txt", Map.of("http-verb", "G")},
                {"gs://" + BUCKET_NAME + "/signUrl-file1.txt", Map.of("http-verb", "")}
        };
    }
    @DataProvider(name = "ShortLivedSignedUrl")
    public Object[][] ShortLivedSignedUrl() {
        return new Object[][]{
                {"gs://" + BUCKET_NAME + "/signUrl-file1.txt", Map.of("duration", "1s")},
                {"gs://" + BUCKET_NAME + "/signUrl-file2.txt", Map.of("duration", "10s")},
                {"gs://" + BUCKET_NAME + "/signUrl-file3.txt", Map.of("duration", "1m")},
                {"gs://" + BUCKET_NAME + "/signUrl-file1.txt", Map.of("duration", "10m")}
        };
    }

    @DataProvider(name = "invalidPath")
    public Object[][] invalidPath() {
        return new Object[][]{
                {"gs://" + BUCKET_NAME + "/non-existent-file.txt", Map.of("duration", "10s")}, //invalid object
                {"gs://" + BUCKET_NAME + "/special!@#$%^-file.txt", Map.of("duration", "10s")}, // special characters
                {"gs://" + BUCKET_NAME + "/signUrl - file .txt", Map.of("duration", "10s")},// spaces
                {"gs://" + BUCKET_NAME + "/signUrllllllllllllllllllllllllllllllllllllllllllllllllllllll" +
                        "lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll" +
                        "lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll" +
                        "lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll - file .txt", Map.of("duration", "10s")} // name too long

        };
    }

    @DataProvider(name = "copyFileTestData")
    public static Object[][] copyFileTestData() {
        return new Object[][]{
                {"local-file.txt", "gs://" + BUCKET_NAME + "/copied-file1.txt"},  // Normal case
                {"large-file.zip", "gs://" + BUCKET_NAME + "/copied-file2.zip"},  // Large file
                {"*.txt", "gs://" + BUCKET_NAME + "/"},  // Wildcard copy
                {"special@file(name).txt", "gs://" + BUCKET_NAME + "/copied-file3.txt"}, // Special character
                {"empty-file.txt", "gs://" + BUCKET_NAME + "/copied-file4.txt"} // Empty file case
        };
    }
    @DataProvider(name = "uploadRemoveFiles")
    public Object[][] uploadRemoveFiles(){
        return new Object [][]{
                {"file1.txt", "gs://" + BUCKET_NAME + "/removed-file1.txt"},
                {"file2.txt", "gs://" + BUCKET_NAME + "/removed-file2.txt"},
                {"file3.txt", "gs://" + BUCKET_NAME + "/removed-file3.txt"},
                {"nested1.txt", "gs://" + BUCKET_NAME + "/folder/removed-nested1.txt"},
                {"nested2.txt", "gs://" + BUCKET_NAME + "/folder/removed-nested2.txt"},
                {"large-file.zip", "gs://" + BUCKET_NAME + "/removed-large-file.zip"},
                {"large-file1.zip", "gs://" + BUCKET_NAME + "/removed-large-file1.zip"},
                {"empty-file.txt", "gs://" + BUCKET_NAME + "/removed-empty-file.txt"},
                {"cross-file1.txt", "gs://cross-bucket/removed-cross-file1.txt"}
        };
    }
    @DataProvider(name = "removeFilesTestData")
    public static Object[][] removeFilesTestData() {
        return new Object[][]{
                {Arrays.asList("gs://" + BUCKET_NAME + "/removed-file1.txt", "gs://" + BUCKET_NAME + "/removed-file2.txt")},  // Normal case
                {Arrays.asList("gs://" + BUCKET_NAME + "/folder/removed-nested1.txt", "gs://" + BUCKET_NAME + "/folder/removed-nested2.txt")}, // Nested files
                {Arrays.asList("gs://" + BUCKET_NAME + "/removed-large-file.zip", "gs://" + BUCKET_NAME + "/removed-large-file1.zip")}, // Large files
                {Arrays.asList("gs://" + BUCKET_NAME + "/removed-empty-file.txt")}, // Single empty file
                {Arrays.asList("gs://" + BUCKET_NAME + "/removed-file3.txt", "gs://cross-bucket/removed-cross-file1.txt")} // Cross-bucket deletion
        };
    }

    //Local -> GCS
    @DataProvider(name = "syncLocalToGCSData")
    public static Object[][] syncLocalToGCSData() {
        return new Object[][]{
                {"local-folder/", "gs://" + BUCKET_NAME + "/", (String) null }, // Normal sync
                {"local-folder/", "gs://" + BUCKET_NAME + "/", "-r"}, // Recursive sync
                {"local-folder/", "gs://" + BUCKET_NAME + "/", "--preserve-metadata"} // Preserve metadata
        };
    }
    //GCS -> Local
    @DataProvider(name = "syncGCSToLocalData")
    public static Object[][] syncGCSToLocalData() {
        return new Object[][]{
                {"gs://" + BUCKET_NAME + "/", "local-folder-dest/", (String) null}, // Normal sync
                {"gs://" + BUCKET_NAME + "/", "local-folder-dest/", "-r"}, // Recursive sync
                {"gs://" + BUCKET_NAME + "/", "local-folder-dest/", "--preserve-metadata"}, // Preserve metadata
                {"gs://" + BUCKET_NAME + "/", "special-chars-#&@!-folder/", (String) null} // Special characters in folder name
        };
    }

    @DataProvider(name = "syncFailureData")
    public static Object[][] syncFailureData() {
        return new Object[][]{
                {"non-existent-folder/", "gs://" + BUCKET_NAME + "/", true, "Did not find existing container at:"},
                {"gs://non-existent-bucket/", "local-folder/", false, "not exist"},
                {"local-folder/", "gs://invalid-bucket/", true, "not exist"}
        };
    }
    private static List<String> generateLargeFileList(int count) {
        List<String> files = new ArrayList<>();
        IntStream.range(1, count + 1).forEach(i ->
                files.add("gs://" + BUCKET_NAME + "/large-file-" + i + ".txt"));
        return files;
    }

    private static List<String> generateMixedFileList() {
        return Arrays.asList(
                "gs://" + BUCKET_NAME + "/image.jpg",
                "gs://" + BUCKET_NAME + "/document.pdf",
                "gs://" + BUCKET_NAME + "/audio.mp3",
                "gs://" + BUCKET_NAME + "/archive.zip"
        );
    }

}
