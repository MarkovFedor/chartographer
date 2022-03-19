package com.kontur.chartographer;

import com.kontur.chartographer.service.StorageService;
import com.kontur.chartographer.storage.StorageProperties;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;
import java.net.InetSocketAddress;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties(StorageProperties.class)
public class Application {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args -> {

        });
    }
}
