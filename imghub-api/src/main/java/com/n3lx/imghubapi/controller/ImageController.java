package com.n3lx.imghubapi.controller;

import com.n3lx.imghubapi.entity.Image;
import com.n3lx.imghubapi.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
public class ImageController {

    @Autowired
    ImageService imageService;

    @RequestMapping(value = "/images", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Image uploadImage(@RequestPart("imageName") String imageName,
                             @RequestPart("imageUploader") String imageUploader,
                             @RequestPart("file") MultipartFile file) throws IOException {
        return imageService.upload(file, imageName, imageUploader);
    }

    @RequestMapping(value = "/images",
            method = RequestMethod.GET)
    public @ResponseBody List<Image> getImageMetadataPage(@RequestParam Integer pageNumber,
                                                          @RequestParam Integer itemsPerPage) {
        return imageService.getImageMetadataPage(pageNumber, itemsPerPage);
    }

}
