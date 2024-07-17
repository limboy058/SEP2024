package alex.UTFProject.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

import javax.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfig {
    //设置上传文件大小限制
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory config = new MultipartConfigFactory();
        config.setMaxFileSize(DataSize.of(10, DataUnit.MEGABYTES));
        config.setMaxRequestSize(DataSize.of(10, DataUnit.MEGABYTES));
        return config.createMultipartConfig();
    }

}
