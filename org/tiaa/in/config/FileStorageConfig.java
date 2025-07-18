package org.tiaa.in.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Value("${split.data.folder}")
    private String splitDataFolder;

    @PostConstruct
    public void init() {
        try {
            Path directory = Paths.get(splitDataFolder);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/split-data/**")
                .addResourceLocations("file:" + splitDataFolder + "/");
    }
}
