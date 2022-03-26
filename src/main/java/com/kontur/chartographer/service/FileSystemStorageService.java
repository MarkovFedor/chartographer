package com.kontur.chartographer.service;

import com.kontur.chartographer.exceptions.IncorrectChartParamsException;
import com.kontur.chartographer.exceptions.NotFoundByIdException;
import com.kontur.chartographer.imageUtils.ConcatImages;
import com.kontur.chartographer.storage.StorageProperties;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService{
    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void init() {

    }

    public void store(MultipartFile file) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        File savingFile = new File(rootLocation.resolve(UUID.randomUUID().toString() + ".bmp").toString());
        try {
            ImageIO.write(image, "BMP", savingFile);
            System.out.println(savingFile.getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UUID create(int width, int height) throws IncorrectChartParamsException, IOException {
        if(width > 20000 || width < 0 || height > 50000 || height < 0) {
            throw new IncorrectChartParamsException("Неверные параметры изображенния");
        }
        UUID id = UUID.randomUUID();
        File file = new File(rootLocation.resolve(id.toString() + ".bmp").toString());
        File templateFile = new File(rootLocation.resolve("template.bmp").toString());
        BufferedImage image = new BufferedImage(width, 1, BufferedImage.TYPE_3BYTE_BGR);
        for(int i = 0; i < width; i++) {
            image.setRGB(i, 0, Color.BLACK.getRGB());
        }
        ImageIO.write(image, "BMP", templateFile);
        ImageIO.write(image, "BMP", file);
        String[] st = new String[2];
        st[0] = "D:/TestFolder/template.bmp";
        st[1] = file.getPath();
        try {
            for(int i = 0; i < height - 1; i++) {
                ConcatImages.concatBMPFiles(st,file.getPath());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        templateFile.delete();
        return id;
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public char[] loadAsBase64(File file) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            byte[] image = IOUtils.toByteArray(in);
            char[] imageBase = Base64Coder.encode(image);
            return imageBase;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public char[] loadFullImage(String id) throws NotFoundByIdException {
        String path = getPath(id);
        File file = new File(path);
        if(!file.exists()) {
            throw new NotFoundByIdException("Такого файла не существует");
        }
        return loadAsBase64(file);
    }

    public char[] loadPartOfImage(String id, int x, int y, int width, int height) throws NotFoundByIdException, IncorrectChartParamsException {
        if(x < 0 || y<0|| x>20000||y>50000||width<0||width>5000||height<0||height>5000) {
            throw new IncorrectChartParamsException("Неверные параметры изображения");
        }
        String path = getPath(id);
        File file = new File(path);
        if(!file.exists()) {
            throw new NotFoundByIdException("Такого файла не существует");
        }
        try {
            BufferedImage image = ImageIO.read(file);
            BufferedImage partImage = image.getSubimage(x,y,width,height);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(partImage, "BMP", baos);
            byte[] bytes = baos.toByteArray();
            char[] base64Image = Base64Coder.encode(bytes);
            return base64Image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void restoreImage(String id, int x, int y, int width, int height, MultipartFile file) throws NotFoundByIdException {
        BufferedImage image = null;
        try {
            image = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        File restoringImageFile = new File(getPath(id));
        if(!restoringImageFile.exists()) {
            throw new NotFoundByIdException("Не найдено");
        }
        BufferedImage restoringImage = null;
        try {
            restoringImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Graphics g = restoringImage.getGraphics();
        g.drawImage(image,x,y,null);
        g.dispose();
        try {
            ImageIO.write(restoringImage, "BMP", restoringImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPath(String id) {
        String[] parts = id.split(".");
        if(parts.length > 1) {
            if(parts[1] != "bmp") {
                id = id + ".bmp";
            }
        }
        return rootLocation.resolve(id).toString();
    }

    public void delete(String id) {
        String path = getPath(id);
        File file = new File(path);
        file.delete();
    }
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
