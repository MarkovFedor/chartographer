package com.kontur.chartographer.service;

import com.kontur.chartographer.repository.ImageWindowsRepository;
import com.kontur.chartographer.exceptions.IncorrectChartParamsException;
import com.kontur.chartographer.exceptions.NotFoundByIdException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class ChartasService {
    private ImageWindowsRepository repository;
    private String rootPath;
    public ChartasService(String rootPath, ImageWindowsRepository repository) {
        this.rootPath = rootPath;
    }

    public UUID create(int width, int height) throws IncorrectChartParamsException {
        if(width > 20000 || height > 50000 || width < 0 || height < 0) {
            throw new IncorrectChartParamsException("Заданные параметры не верны");
        }
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for(int x = 0; x < width; x++) {
            for(int y = 0; y< height; y++) {
                result.setRGB(x,y, Color.WHITE.getRGB());
            }
        }
        UUID id = UUID.randomUUID();
        String path = generatePath(id);
        try {
            repository.saveImage(result, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void update(BufferedImage img, int x, int y, int width, int height) {

    }

    public BufferedImage getPartOfImage(UUID id,int x, int y, int width, int height) throws IncorrectChartParamsException, IOException, NotFoundByIdException {
        if(width>5000 || width <0 || height > 5000 || height < 0) {
            throw new IncorrectChartParamsException("Некорректные параметры изображения");
        }
        String path = generatePath(id);
        BufferedImage image = repository.getImage(path);

        image.getData(new Rectangle());
        return null;
    }

    public void removeChart(UUID id) throws NotFoundByIdException {
        String path = generatePath(id);
        repository.deleteImage(path);
    }

    public BufferedImage findImage(UUID id) throws NotFoundByIdException {
        String path = generatePath(id);
        BufferedImage image = null;
        try {
            image = repository.getImage(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(image != null) {
            return image;
        } else {
            throw new NotFoundByIdException("Изображение с id: "+id+" не найдено");
        }
    }

    private String generatePath(UUID id) {
        return rootPath + "/" + id;
    }
}
