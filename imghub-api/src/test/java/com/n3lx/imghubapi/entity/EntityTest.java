package com.n3lx.imghubapi.entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class EntityTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    public void testPersistence() {

        //Create objects and upload them into database
        Image image = new Image();
        image.setImageName("Cat");
        image.setUploaderName("Cat Person");

        Resource resource = new Resource();
        resource.setResourceName("cat_picture.png");
        resource.setUploadTime(ZonedDateTime.now());
        resource.setUploadTimeZone(TimeZone.getDefault());

        image.setResource(resource);

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        int imageId = (Integer) session.save(image);
        int resourceID = (Integer) session.save(resource);

        session.getTransaction().commit();

        //Get uploaded objects from the database and compare them

        Image databaseImage = session.get(Image.class, imageId);
        assertEquals(image.getImageName(), databaseImage.getImageName());

        Resource databaseResource = databaseImage.getResource();
        assertEquals(resourceID, databaseResource.getId());
        assertEquals(resource.getResourceName(), databaseResource.getResourceName());
        assertEquals(resource.getUploadTime(), databaseResource.getUploadTime());
        assertEquals(resource.getUploadTimeZone(), databaseResource.getUploadTimeZone());

        session.close();
    }

}
