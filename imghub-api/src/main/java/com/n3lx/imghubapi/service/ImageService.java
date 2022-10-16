package com.n3lx.imghubapi.service;

import com.n3lx.imghubapi.entity.Image;
import com.n3lx.imghubapi.entity.Resource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

@Service
public class ImageService {

    @Autowired
    private FileService fileService;

    @Autowired
    SessionFactory sessionFactory;

    public Image save(String imageName, String uploaderName, MultipartFile mpFile) throws IOException {

        if (imageName == null || uploaderName == null) {
            throw new IllegalArgumentException("Image name and uploader fields cannot be null");
        }

        Image image = new Image();
        image.setImageName(imageName);
        image.setUploaderName(uploaderName);

        String resourceName = fileService.save(mpFile);

        Resource resource = new Resource();
        resource.setResourceName(resourceName);
        resource.setUploadTime(ZonedDateTime.now());
        resource.setUploadTimeZone(TimeZone.getDefault());

        image.setResource(resource);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            int imageId = (Integer) session.save(image);
            session.save(resource);

            session.getTransaction().commit();

            return session.get(Image.class, imageId);
        }
    }

    public Image get(int imageId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Image.class, imageId);
        }
    }

    public List<Image> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM image", Image.class).getResultList();
        }
    }

}
