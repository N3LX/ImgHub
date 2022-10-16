package com.n3lx.imghubapi;

import com.n3lx.imghubapi.entity.Image;
import com.n3lx.imghubapi.service.FileService;
import com.n3lx.imghubapi.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@CrossOrigin
public class ImageController {

    @Autowired
    ImageService imageService;

    @Autowired
    FileService fileService;

    @RequestMapping(value = "/images/{id}",
            method = RequestMethod.GET,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@PathVariable Integer id) throws IOException {
        Image image = imageService.get(id);
        String filePath = fileService.getResourcePath(image.getResource());
        return Files.readAllBytes(Path.of(filePath));
    }

    @RequestMapping(value = "/images", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Image saveImage(@RequestPart("imageName") String imageName,
                           @RequestPart("imageUploader") String imageUploader,
                           @RequestPart("file") MultipartFile file) throws IOException {
        return imageService.save(imageName, imageUploader, file);
    }

}
