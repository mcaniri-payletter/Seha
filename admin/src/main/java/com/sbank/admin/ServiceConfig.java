package com.sbank.admin;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;
import com.sbank.admin.common.resolver.ArgumentResolver;
import com.sbank.admin.common.security.BaseInterceptor;
import com.sbank.admin.common.security.LoginCheckInterceptor;
import com.sbank.admin.common.security.SessionListener;
import com.sbank.admin.common.security.XSSFilter;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.http.HttpSessionListener;
import java.util.List;
import java.util.Locale;

/**--------------------------------------------------------------------
 * ■ServiceConfig ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@SuppressWarnings("unchecked")
@Configuration
public class ServiceConfig implements WebMvcConfigurer {

    @Value("${static.url}")
    private String staticUrl;

    @Value("${static.resources.location}")
    private String staticResourcesLocation;

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource resourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("classpath:locale/Messages");
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setCacheSeconds(0);
        return resourceBundleMessageSource;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor(@Qualifier("messageSource") MessageSource messageSource) {
        return new MessageSourceAccessor(messageSource);
    }

    @Bean
    public SessionLocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(new Locale("ko"));
        return sessionLocaleResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(staticUrl + "/**")
                .addResourceLocations(staticResourcesLocation)
                .setCachePeriod(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry objRegistry) {
        objRegistry.addInterceptor(loginCheckInterceptor())
                   .addPathPatterns("/**")
                   .excludePathPatterns("/")
                   .excludePathPatterns(staticUrl + "/**")
                   .excludePathPatterns("/error")
                   .excludePathPatterns("/login")
                   .excludePathPatterns("/login/**")
                   .excludePathPatterns("/administrator/administrator/resetPwd")
                   .excludePathPatterns("/administrator/administrator/changePwd")
                   .excludePathPatterns("/common/common/reload");

        objRegistry.addInterceptor(baseInterceptor())
                   .addPathPatterns("/**")
                   .excludePathPatterns("/")
                   .excludePathPatterns(staticUrl + "/**")
                   .excludePathPatterns("/error")
                   .excludePathPatterns("/login")
                   .excludePathPatterns("/login/**")
                   .excludePathPatterns("/administrator/administrator/resetPwd")
                   .excludePathPatterns("/administrator/administrator/changePwd")
                   .excludePathPatterns("/common/common/reload");
    }

    @Bean
    public LoginCheckInterceptor loginCheckInterceptor() {
        return new LoginCheckInterceptor();
    }

    @Bean
    public BaseInterceptor baseInterceptor() {
        return new BaseInterceptor();
    }

    @Bean
    public FilterRegistrationBean setXSSFilter() {
        FilterRegistrationBean objRegistrationBean = new FilterRegistrationBean();

        objRegistrationBean.setFilter(new XSSFilter());
        objRegistrationBean.addUrlPatterns("/*");
        objRegistrationBean.setOrder(0);

        return objRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean setXSSEscapeServletFilter() {
        FilterRegistrationBean objRegistrationBean = new FilterRegistrationBean();

        objRegistrationBean.setFilter(new XssEscapeServletFilter());
        objRegistrationBean.addUrlPatterns("/*");
        objRegistrationBean.setOrder(1);

        return objRegistrationBean;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> objArgumentResolvers) {
        objArgumentResolvers.add(new ArgumentResolver());
    }

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }

    @Bean
    public TaskScheduler taskScheduler(){
        return new ConcurrentTaskScheduler();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public HttpSessionListener httpSessionListener(){
    	return new SessionListener();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
