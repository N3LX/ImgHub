package com.n3lx.imghubapi.service;

import com.n3lx.imghubapi.entity.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FileServiceTest {

    @Autowired
    FileService fileService;

    @Value("${imghub.resourceDirectory}")
    private String RESOURCE_DIRECTORY;

    public static Image testImage;
    public static MultipartFile testFile;

    @BeforeAll
    private static void prepareTestObjects() {
        testImage = new Image();
        testImage.setImageName("Test");
        testImage.setUploaderName("Test uploader");
        testImage.setUploadTime(Timestamp.valueOf(LocalDateTime.now()));

        testFile = new MockMultipartFile("test_image.jpg",
                "test_image.jpg",
                "JPG",
                new byte[100]);
    }

    @BeforeEach
    private void removeResourceDirectoryIfExists() {
        Path resourceDirectory = Paths.get(RESOURCE_DIRECTORY);

        if (resourceDirectory.toFile().exists()) {

            for (File resource : resourceDirectory.toFile().listFiles()) {
                resource.delete();
            }

            resourceDirectory.toFile().delete();

        }
    }

    @Test
    public void testSaveWithNullFile() {
        Exception exception = assertThrows(NullPointerException.class, () -> fileService.save(null, testImage));

        String expectedMessage = "The file name or the file itself cannot be empty.";

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSaveWithNullImage() {
        Exception exception = assertThrows(NullPointerException.class, () -> fileService.save(testFile, null));

        String expectedMessage = "The image object or its contents are null.";

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSaveWithValidData() throws IOException {
        String resourceName = fileService.save(testFile, testImage);

        //Check if file exists
        assertTrue(Paths.get(RESOURCE_DIRECTORY, resourceName).toFile().exists());

        //Check if file contents match what should have been saved into it
        byte[] fileContents = Files.readAllBytes(Paths.get(RESOURCE_DIRECTORY, resourceName));
        assertEquals(0, Arrays.compare(new byte[100], fileContents));
    }

    @Test
    public void testLoad() throws IOException {
        String resourceName = "test.jpg";

        //Create a test file and write data into it
        Paths.get(RESOURCE_DIRECTORY).toFile().mkdir();
        Path resourcePath = Paths.get(RESOURCE_DIRECTORY, resourceName);
        Files.write(resourcePath, new byte[1000]);

        //Check output of load() method
        assertEquals(0, Arrays.compare(new byte[1000], fileService.load(resourceName)));
    }

}
