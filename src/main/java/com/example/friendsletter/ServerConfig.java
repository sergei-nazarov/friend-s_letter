package com.example.friendsletter;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class ServerConfig implements WebMvcConfigurer {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setCacheSeconds(1800); //reload messages every 1800 seconds
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    //SessionLocaleResolver
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocaleFunction(req -> {
            List<Locale.LanguageRange> list = Locale.LanguageRange.parse(req.getHeader("Accept-Language"));
            List<Locale> availableLocales = List.of(Locale.ENGLISH, Locale.US,
                    Locale.FRANCE, new Locale("fr"), Locale.forLanguageTag("ru"), Locale.forLanguageTag("ru-ru"));
            Locale result = Locale.lookup(list, availableLocales);
            if (result == null) {
                return Locale.ENGLISH;
            }
            return result;
        });

        return slr;
    }

}
