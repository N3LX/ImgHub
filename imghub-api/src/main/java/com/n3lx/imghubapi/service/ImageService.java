package com.n3lx.imghubapi.service;

import com.n3lx.imghubapi.entity.Image;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImageService {

    @Autowired
    private FileService fileService;

    @Autowired
    SessionFactory sessionFactory;

    public Image upload(MultipartFile multipartFile, String imageName, String uploaderName) throws IOException {
        //Data validation
        if (imageName == null || uploaderName == null) {
            throw new NullPointerException("Image name and uploader name cannot be empty.");
        }

        //Prepare new Image object
        Image image = new Image();
        image.setImageName(imageName);
        image.setUploaderName(uploaderName);
        image.setUploadTime(Timestamp.valueOf(LocalDateTime.now()));

        //Save the file via FileService
        String resourceName = fileService.save(multipartFile, image);
        image.setResourceName(resourceName);

        //Save the image into database
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Integer imageId = (Integer) session.save(image);

            session.getTransaction().commit();
            image.setId(imageId);
        }

        //Return complete Image object
        return image;
    }

    public Image getImageMetadata(int imageId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Image.class, imageId);
        }
    }

    public List<Image> getImageMetadataPage(int pageNo, int itemsPerPage) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM image ORDER BY upload_time ASC", Image.class)
                    .setFirstResult((pageNo - 1) * itemsPerPage)
                    .setMaxResults(itemsPerPage)
                    .getResultList();
        }
    }

}
