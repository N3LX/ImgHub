package com.n3lx.imghubapi.service;

import com.n3lx.imghubapi.entity.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class FileService {

    @Value("${imghub.resourceDirectory}")
    private String RESOURCE_DIRECTORY;

    /***
     * Save a file in RESOURCE_DIRECTORY path in file system.
     * @param multipartFile File to be uploaded.
     * @return Name of uploaded file.
     * @throws IOException Thrown in case of any problems with file operations.
     */
    public String save(MultipartFile multipartFile, Image image) throws IOException {

        //Data validation
        if (multipartFile == null || multipartFile.getOriginalFilename() == null) {
            throw new NullPointerException("The file name or the file itself cannot be empty.");
        }

        if (image == null || image.getImageName() == null || image.getUploaderName() == null || image.getUploadTime() == null) {
            throw new NullPointerException("The image object or its contents are null.");
        }

        //Generate resourceName
        String resourceName = generateResourceName(multipartFile, image);
        Path resourcePath = Paths.get(RESOURCE_DIRECTORY, resourceName);

        //Ensure that resource directory exists
        Paths.get(RESOURCE_DIRECTORY).toFile().mkdir();

        //Save the resource
        Files.write(resourcePath, multipartFile.getInputStream().readAllBytes());

        return resourceName;
    }

    public byte[] load(String resourceName) throws IOException {
        Path resourcePath = Paths.get(RESOURCE_DIRECTORY, resourceName);
        return Files.readAllBytes(resourcePath);
    }

    private String generateResourceName(MultipartFile multipartFile, Image image) {
        StringBuilder resourceNameBuilder = new StringBuilder();

        //Generate a hashValue based on Image object and original filename
        int hashValue = Objects.hash(image.getImageName(),
                image.getUploaderName(),
                image.getUploadTime(),
                multipartFile.getOriginalFilename());

        //Obtain original file extension
        String fileExtension = multipartFile.getOriginalFilename().split("\\.")[1];

        //Create a filename based on the hash value and append original file extension
        resourceNameBuilder.append("res_")
                .append(hashValue)
                .append(".")
                .append(fileExtension);

        return resourceNameBuilder.toString();
    }

}
