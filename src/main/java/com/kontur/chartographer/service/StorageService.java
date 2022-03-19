package com.kontur.chartographer.service;

import com.kontur.chartographer.exceptions.NotFoundByIdException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();
    void store(MultipartFile file);
    Stream<Path> loadAll();
    Path load(String fileName);
    char[] loadAsBase64(File file);
    void deleteAll();
}
