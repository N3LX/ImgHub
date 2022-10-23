package com.n3lx.imghubapi.service;

import com.n3lx.imghubapi.entity.Image;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ImageServiceTest {

    @Autowired
    ImageService imageService;

    @Autowired
    SessionFactory sessionFactory;

    public static MultipartFile testFile;

    @BeforeEach
    private void clearImagesTableInDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            for (Image image : session.createQuery("FROM image", Image.class).getResultList()) {
                session.delete(image);
            }
            session.getTransaction().commit();
        }
    }

    @BeforeAll
    private static void prepareTestObjects() {
        testFile = new MockMultipartFile("test_image.jpg",
                "test_image.jpg",
                "JPG",
                new byte[100]);
    }

    @Test
    public void testUploadWithNullFile() {
        //Check if exception has been thrown
        Exception exception = assertThrows(NullPointerException.class, () ->
                imageService.upload(null, "Test", "Test uploader"));

        String expectedMessage = "The file name or the file itself cannot be empty.";
        assertTrue(exception.getMessage().contains(expectedMessage));

        //Make sure that an entry in database wasn't made
        try (Session session = sessionFactory.openSession()) {
            List<Image> images = session.createQuery("FROM image", Image.class).getResultList();
            assertEquals(0, images.size());
        }
    }

    @Test
    public void testUploadWithNullStringArguments() {
        //Check if exception has been thrown for empty imageName
        Exception exception = assertThrows(NullPointerException.class, () ->
                imageService.upload(testFile, null, "Test uploader"));

        String expectedMessage = "Image name and uploader name cannot be empty.";
        assertTrue(exception.getMessage().contains(expectedMessage));

        //Check if exception has been thrown for empty uploaderName
        exception = assertThrows(NullPointerException.class, () ->
                imageService.upload(testFile, "Test", null));

        expectedMessage = "Image name and uploader name cannot be empty.";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUploadWithValidData() throws IOException {
        String testImageName = "Image";
        String testUploaderName = "Uploader";

        Image uploadedImage = imageService.upload(testFile, testImageName, testUploaderName);

        Session session = sessionFactory.openSession();
        Image receivedImage = session.get(Image.class, uploadedImage.getId());
        session.close();

        assertEquals(uploadedImage.getId(), receivedImage.getId());
        assertEquals(testImageName, receivedImage.getImageName());
        assertEquals(testUploaderName, receivedImage.getUploaderName());
        assertEquals(uploadedImage.getUploadTime(), receivedImage.getUploadTime());
        assertEquals(uploadedImage.getResourceName(), receivedImage.getResourceName());
    }

    @Test
    public void testGetImageMetadata() {
        //Create a valid image object
        Image sourceImage = new Image();
        sourceImage.setImageName("Test");
        sourceImage.setUploaderName("Test uploader");
        sourceImage.setUploadTime(Timestamp.valueOf(LocalDateTime.now()));
        sourceImage.setResourceName("test_resource.jpg");

        //Put it into database
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            int id = (Integer) session.save(sourceImage);
            session.getTransaction().commit();
            sourceImage.setId(id);
        }

        //Get image from database using tested method
        Image resultImage = imageService.getImageMetadata(sourceImage.getId());

        assertEquals(sourceImage.getId(), resultImage.getId());
        assertEquals(sourceImage.getImageName(), resultImage.getImageName());
        assertEquals(sourceImage.getUploaderName(), resultImage.getUploaderName());
        assertEquals(sourceImage.getUploadTime(), resultImage.getUploadTime());
        assertEquals(sourceImage.getResourceName(), resultImage.getResourceName());
    }

    @Test
    public void testGetImageMetadataPage() throws InterruptedException {
        //Create valid image objects and add them to a database
        List<Image> generatedImages = new ArrayList<>();

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        for (int x = 0; x < 100; x++) {
            Image sourceImage = new Image();
            sourceImage.setImageName("Test " + x);
            sourceImage.setUploaderName("Test uploader " + x);
            sourceImage.setUploadTime(Timestamp.valueOf(LocalDateTime.now()));
            sourceImage.setResourceName("test_resource" + x + ".jpg");
            Thread.sleep(10);

            int id = (Integer) session.save(sourceImage);
            sourceImage.setId(id);
            generatedImages.add(sourceImage);
        }
        session.getTransaction().commit();
        session.close();

        //Test images in 2 sets of 50 items to check if division of results by page functions as intended
        List<Image> imagesFromDatabase = imageService.getImageMetadataPage(1, 50);
        for (int x = 0; x < 50; x++) {
            assertEquals(generatedImages.get(x).getId(), imagesFromDatabase.get(x).getId());
            assertEquals(generatedImages.get(x).getImageName(), imagesFromDatabase.get(x).getImageName());
            assertEquals(generatedImages.get(x).getUploaderName(), imagesFromDatabase.get(x).getUploaderName());
            assertEquals(generatedImages.get(x).getResourceName(), imagesFromDatabase.get(x).getResourceName());
            assertEquals(generatedImages.get(x).getUploadTime(), imagesFromDatabase.get(x).getUploadTime());
        }

        List<Image> imagesFromDatabase2 = imageService.getImageMetadataPage(2, 50);
        for (int x = 50; x < 100; x++) {
            assertEquals(generatedImages.get(x).getId(), imagesFromDatabase2.get(x - 50).getId());
            assertEquals(generatedImages.get(x).getImageName(), imagesFromDatabase2.get(x - 50).getImageName());
            assertEquals(generatedImages.get(x).getUploaderName(), imagesFromDatabase2.get(x - 50).getUploaderName());
            assertEquals(generatedImages.get(x).getResourceName(), imagesFromDatabase2.get(x - 50).getResourceName());
            assertEquals(generatedImages.get(x).getUploadTime(), imagesFromDatabase2.get(x - 50).getUploadTime());
        }

    }

}
