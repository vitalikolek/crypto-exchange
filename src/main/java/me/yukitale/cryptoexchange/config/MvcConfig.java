package me.yukitale.cryptoexchange.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        if (dirName.startsWith("../")) {
            dirName = dirName.replace("../", "");
        }

        registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:" + uploadPath + "/").setCacheControl(cacheControl());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory(Resources.USER_PROFILES_PHOTO_DIR, registry);
        exposeDirectory(Resources.USER_KYC_PHOTO_DIR, registry);
        exposeDirectory(Resources.ADMIN_ICON_DIR, registry);
        exposeDirectory(Resources.ADMIN_COIN_ICONS_DIR, registry);
        exposeDirectory(Resources.DOMAIN_ICONS_DIR, registry);
        exposeDirectory(Resources.SUPPORT_IMAGES, registry);
        exposeDirectory(Resources.P2P_AVATARS, registry);

        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/").setCacheControl(cacheControl());
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/static/assets/").setCacheControl(cacheControl());
        registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/static/fonts/").setCacheControl(cacheControl());
        registry.addResourceHandler("/external-embedding/**").addResourceLocations("classpath:/static/external-embedding/").setCacheControl(cacheControl());
        registry.addResourceHandler("/landings/**").addResourceLocations("classpath:/static/landings/").setCacheControl(cacheControl());
        registry.addResourceHandler("/npm/**").addResourceLocations("classpath:/static/npm/").setCacheControl(cacheControl());
        registry.addResourceHandler("/trading_core/**").addResourceLocations("classpath:/static/trading_core/").setCacheControl(cacheControl());
    }

    private CacheControl cacheControl() {
        return CacheControl.maxAge(Duration.ofSeconds(3600));
    }
}
