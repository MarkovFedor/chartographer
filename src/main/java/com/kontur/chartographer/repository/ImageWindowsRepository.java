package com.kontur.chartographer.repository;

import com.kontur.chartographer.exceptions.NotFoundByIdException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageWindowsRepository {

    public boolean saveImage(BufferedImage image, String path) throws IOException {
        return ImageIO.write(image, "bmp", new File(path));
    }

    public void deleteImage(String path) throws NotFoundByIdException {
        File file = new File(path);
        if(!file.exists()) {
            throw new NotFoundByIdException("Файл не найден");
        } else {
            file.delete();
        }
    }

    public BufferedImage getImage(String path) throws IOException, NotFoundByIdException {
        File file = new File(path);
        BufferedImage image = ImageIO.read(file);
        if(image == null) {
            throw new NotFoundByIdException("Изображение не найдено");
        }
        return image;
    }
}
