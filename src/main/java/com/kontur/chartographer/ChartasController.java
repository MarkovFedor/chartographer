package com.kontur.chartographer;

import com.kontur.chartographer.exceptions.IncorrectChartParamsException;
import com.kontur.chartographer.exceptions.NotFoundByIdException;
import com.kontur.chartographer.service.FileSystemStorageService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.annotation.Resource;
import javax.imageio.plugins.bmp.BMPImageWriteParam;
import javax.servlet.ServletContext;
import javax.websocket.server.PathParam;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Controller
public class ChartasController {
    private final FileSystemStorageService storageService;

    @Autowired
    public ChartasController(FileSystemStorageService storageService, ServletContext context) {
        this.storageService = storageService;
    }

    @PostMapping("/")
    public ResponseEntity createNewChart(
            @RequestParam("width") int width,
            @RequestParam("height") int height) {
        UUID id = null;
        try {
            id = storageService.create(width, height);
            return new ResponseEntity(id, HttpStatus.CREATED);
        } catch (IncorrectChartParamsException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getFullChart(
            @PathVariable String id) throws IOException {
        char[] image = new char[0];
        try {
            image = storageService.loadFullImage(id);
            ResponseEntity<char[]> entity = new ResponseEntity(image, HttpStatus.OK);
            return entity;
        } catch (NotFoundByIdException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/{id}/")
    public ResponseEntity getPartOfChart(
            @PathVariable String id,
            @RequestParam("x") int x,
            @RequestParam("y") int y,
            @RequestParam("width") int width,
            @RequestParam("height") int height) {
        try {
            byte[] image = storageService.loadPartOfImage(id, x, y, width, height);
            return new ResponseEntity(image, HttpStatus.OK);
        } catch (NotFoundByIdException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } catch(IncorrectChartParamsException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteChart(@PathVariable String id) {
        storageService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
