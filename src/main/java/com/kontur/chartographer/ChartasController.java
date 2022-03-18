package com.kontur.chartographer;

import com.kontur.chartographer.exceptions.IncorrectChartParamsException;
import com.kontur.chartographer.exceptions.NotFoundByIdException;
import com.kontur.chartographer.service.ChartasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/chartas")
public class ChartasController {
    private final ChartasService service;

    @Autowired
    public ChartasController(ChartasService service) {
        this.service = service;
    }

    @PostMapping("/?width={width}&height={height")
    public ResponseEntity createChartas(
            @PathVariable("width") int width,
            @PathVariable("height") int height
    ) {
        try {
            UUID id = service.create(width, height);
            return new ResponseEntity(id, HttpStatus.CREATED);
        } catch(IncorrectChartParamsException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteChart(@PathVariable("id") UUID id) {
        try {
            service.removeChart(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (NotFoundByIdException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/?x={x}&y={y}&width={width}&height={height}")
    public ResponseEntity getPartOfChart(
            @PathVariable("id") UUID id,
            @PathVariable("x") int x,
            @PathVariable("y") int y,
            @PathVariable("width") int width,
            @PathVariable("height") int height
    )  {
        try {
            BufferedImage image = service.getPartOfImage(id, x,y,width,height);
            return new ResponseEntity(image, HttpStatus.CREATED);
        } catch (IncorrectChartParamsException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch(NotFoundByIdException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.OK);
        }
    }


}
