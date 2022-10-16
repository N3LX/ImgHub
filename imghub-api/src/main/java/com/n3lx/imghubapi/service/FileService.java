package com.n3lx.imghubapi.service;

import com.n3lx.imghubapi.entity.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {

    static final String RESOURCE_DIRECTORY = "./imghub-images";

    /***
     * Save a file in RESOURCE_DIRECTORY path in file system.
     * @param multipartFile File to be uploaded.
     * @return Name of uploaded file.
     * @throws IOException Thrown in case of any problems with file operations.
     */
    public String save(MultipartFile multipartFile) throws IOException {
        if (multipartFile.getOriginalFilename() == null || multipartFile.getName() == null) {
            throw new IOException("The file name cannot be empty.");
        }

        Path uploadDirectory = Paths.get(RESOURCE_DIRECTORY);

        if (!Files.exists(uploadDirectory)) {
            Files.createDirectories(uploadDirectory);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadDirectory.resolve(multipartFile.getOriginalFilename());
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return multipartFile.getOriginalFilename();
    }

    /***
     * Provides a path to the resource relative to the application's location
     */
    public String getResourcePath(Resource resource) {
        return RESOURCE_DIRECTORY + resource.getResourceName();
    }

}
