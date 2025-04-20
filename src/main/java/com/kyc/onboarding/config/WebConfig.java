package com.kyc.onboarding.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploadeddoc";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(UPLOAD_DIR).toUri().toString();
        registry.addResourceHandler("/uploadeddoc/**")
                .addResourceLocations(uploadPath);
    }

}
