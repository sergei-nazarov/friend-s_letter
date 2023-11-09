package com.example.friendsletter.configs;

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

/**
 * Config for localization
 */
@Configuration
public class LocalizationConfig implements WebMvcConfigurer {

    /**
     * Bean for locale resolving
     * Locale is held in session.
     * First, the locale is determined by the http Accept-Language header.
     * If there is no Accept-Language header in the request or there are no
     * supported languages in the application, the default is ENGLISH.
     * Header example - "ru,en-US;q=0.9,en;q=0.8,ru-RU;q=0.7"
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocaleFunction(req -> {
            String acceptLanguage = req.getHeader("Accept-Language");
            if (acceptLanguage == null) {
                return Locale.ENGLISH;
            }
            List<Locale.LanguageRange> list = Locale.LanguageRange.parse(acceptLanguage);
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


    /**
     * Bean for message source.
     * Files starting with "messages..." are used.
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setCacheSeconds(1800);
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
}
