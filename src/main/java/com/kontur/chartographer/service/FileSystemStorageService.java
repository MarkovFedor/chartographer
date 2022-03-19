package com.kontur.chartographer.service;

import com.kontur.chartographer.exceptions.IncorrectChartParamsException;
import com.kontur.chartographer.exceptions.NotFoundByIdException;
import com.kontur.chartographer.exceptions.StorageFileNotFoundException;
import com.kontur.chartographer.storage.StorageProperties;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public UUID create(int width, int height) throws IncorrectChartParamsException {
        if(width > 20000 || width < 0 || height > 50000 || height < 0) {
            throw new IncorrectChartParamsException("Неверные параметры изображенния");
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        UUID id = UUID.randomUUID();
        File file = new File(rootLocation.resolve(id.toString() + ".bmp").toString());
        try {
            ImageIO.write(image, "BMP", file);
            return id;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
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

    public byte[] loadPartOfImage(String id, int x, int y, int width, int height) throws NotFoundByIdException, IncorrectChartParamsException {
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
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
