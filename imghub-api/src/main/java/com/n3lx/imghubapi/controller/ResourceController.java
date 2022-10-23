package com.n3lx.imghubapi.controller;

import com.n3lx.imghubapi.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin
public class ResourceController {

    @Autowired
    FileService fileService;

    @RequestMapping(value = "/resources/{resourceName}",
            method = RequestMethod.GET,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getResource(@PathVariable("resourceName") String resourceName) throws IOException {
        return fileService.load(resourceName);
    }

}
