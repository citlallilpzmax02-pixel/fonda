package itch.tecnm.proyecto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "drhtzmxqc",
            "api_key", "219132286996962",
            "api_secret", "aiHDT5-TbIb3OD0lvpAjHJ8RH2w",
            "secure", true
        ));
    }
    
}
