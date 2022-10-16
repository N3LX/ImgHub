package com.n3lx.imghubapi.service;

import com.n3lx.imghubapi.entity.Resource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @BeforeEach
    public void removeResourceDirectory() throws IOException {
        File directory = new File(FileService.RESOURCE_DIRECTORY);

        if (directory.exists()) {
            for (File f : directory.listFiles()) {
                f.delete();
            }
            directory.delete();
        }

    }

    @Test
    public void testSave() throws IOException {
        byte[] fileContent = new byte[10];

        MultipartFile mpFile = new MockMultipartFile("test_file.png", "test_file.png", "PNG", fileContent);
        String savedFileName = fileService.save(mpFile);
        assertEquals(mpFile.getOriginalFilename(), savedFileName);

        File file = new File(FileService.RESOURCE_DIRECTORY + "/test_file.png");
        assertTrue(file.exists());
        assertEquals(0,
                Arrays.compare(fileContent,
                        Files.readAllBytes(Path.of(FileService.RESOURCE_DIRECTORY + "/test_file.png"))));
    }

    @Test
    public void testGetResourcePath() {
        Resource resource = new Resource();
        resource.setResourceName("cat.jpg");
        assertEquals(FileService.RESOURCE_DIRECTORY + resource.getResourceName(),
                fileService.getResourcePath(resource));
    }

}
