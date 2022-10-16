package com.n3lx.imghubapi.service;

import com.n3lx.imghubapi.entity.Image;
import com.n3lx.imghubapi.entity.Resource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class ImageServiceTest {

    @Autowired
    ImageService imageService;

    @Autowired
    SessionFactory sessionFactory;


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

    @BeforeEach
    public void clearTablesInDatabase() {
        try (Session session = sessionFactory.openSession()) {
            List<Image> images = session.createQuery("FROM image", Image.class).getResultList();
            List<Resource> resources = session.createQuery("FROM resource", Resource.class).getResultList();

            session.beginTransaction();

            for (Image i : images) {
                session.delete(i);
            }

            for (Resource r : resources) {
                session.delete(r);
            }

            session.getTransaction().commit();
        }
    }

    @Test
    public void testSave() throws IOException {
        String imageName = "puppy.jpg";
        String uploaderName = "Dog Person";

        byte[] fileContents = new byte[1000];
        MultipartFile mpFile = new MockMultipartFile(imageName, imageName, "JPG", fileContents);

        Image savedImage = imageService.save(imageName, uploaderName, mpFile);

        //Compare the returned object with input data
        assertEquals(imageName, savedImage.getImageName());
        assertEquals(uploaderName, savedImage.getUploaderName());
        assertEquals(imageName, savedImage.getResource().getResourceName());

        //Get the record from the database and compare it
        try (Session session = sessionFactory.openSession()) {
            Image databaseImage = session.get(Image.class, savedImage.getId());

            assertEquals(savedImage.getImageName(), databaseImage.getImageName());
            assertEquals(savedImage.getUploaderName(), databaseImage.getUploaderName());
            assertEquals(savedImage.getResource().getId(), databaseImage.getResource().getId());
            assertEquals(savedImage.getResource().getResourceName(), databaseImage.getResource().getResourceName());
            assertEquals(savedImage.getResource().getUploadTime(), databaseImage.getResource().getUploadTime());
            assertEquals(savedImage.getResource().getUploadTimeZone(), databaseImage.getResource().getUploadTimeZone());
        }
    }

    @Test
    public void testGet() throws IOException {
        String imageName = "puppy.jpg";
        String uploaderName = "Dog Person";

        byte[] fileContents = new byte[1000];
        MultipartFile mpFile = new MockMultipartFile(imageName, imageName, "JPG", fileContents);

        Image savedImage = imageService.save(imageName, uploaderName, mpFile);
        Image retrievedImage = imageService.get(savedImage.getId());

        assertEquals(savedImage.getImageName(), retrievedImage.getImageName());
        assertEquals(savedImage.getUploaderName(), retrievedImage.getUploaderName());
        assertEquals(savedImage.getResource().getId(), retrievedImage.getResource().getId());
        assertEquals(savedImage.getResource().getResourceName(), retrievedImage.getResource().getResourceName());
        assertEquals(savedImage.getResource().getUploadTime(), retrievedImage.getResource().getUploadTime());
        assertEquals(savedImage.getResource().getUploadTimeZone(), retrievedImage.getResource().getUploadTimeZone());
    }

    @Test
    public void testGetAll() throws IOException {
        byte[] fileContents = new byte[1000];

        //Create a couple of images and insert them into database
        String imageName1 = "puppy.jpg";
        String uploaderName1 = "Dog Person";
        MultipartFile mpFile1 = new MockMultipartFile(imageName1, imageName1, "JPG", fileContents);
        Image image1 = imageService.save(imageName1, uploaderName1, mpFile1);

        String imageName2 = "cat.jpg";
        String uploaderName2 = "Cat Person";
        MultipartFile mpFile2 = new MockMultipartFile(imageName2, imageName2, "JPG", fileContents);
        Image image2 = imageService.save(imageName2, uploaderName2, mpFile2);

        String imageName3 = "turtle.jpg";
        String uploaderName3 = "Turtle Person";
        MultipartFile mpFile3 = new MockMultipartFile(imageName3, imageName3, "JPG", fileContents);
        Image image3 = imageService.save(imageName3, uploaderName3, mpFile3);

        //Pull images from database and compare them
        List<Image> dbImages = imageService.getAll();
        assertEquals(3, dbImages.size());

        List<Image> originalImages = new LinkedList<>();
        originalImages.add(image1);
        originalImages.add(image2);
        originalImages.add(image3);

        for (int x = 0; x < originalImages.size(); x++) {
            assertEquals(originalImages.get(x).getImageName(), dbImages.get(x).getImageName());
            assertEquals(originalImages.get(x).getUploaderName(), dbImages.get(x).getUploaderName());
            assertEquals(originalImages.get(x).getResource().getId(), dbImages.get(x).getResource().getId());
            assertEquals(originalImages.get(x).getResource().getResourceName(), dbImages.get(x).getResource().getResourceName());
            assertEquals(originalImages.get(x).getResource().getUploadTime(), dbImages.get(x).getResource().getUploadTime());
            assertEquals(originalImages.get(x).getResource().getUploadTimeZone(), dbImages.get(x).getResource().getUploadTimeZone());
        }

    }

}
