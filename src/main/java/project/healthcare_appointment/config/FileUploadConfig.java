package project.healthcare_appointment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {
    @Value("${file.upload.base-path:uploads}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + basePath + "/avatars/");

        registry.addResourceHandler("/documents/**")
                .addResourceLocations("file:" + basePath + "/documents/");

        registry.addResourceHandler("/medical-records/**")
                .addResourceLocations("file:" + basePath + "/medical-records/");
    }
}
