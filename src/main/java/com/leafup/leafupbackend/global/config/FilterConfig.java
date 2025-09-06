package com.leafup.leafupbackend.global.config;

import com.leafup.leafupbackend.global.filter.AdminAuthFilter;
import com.leafup.leafupbackend.global.filter.TokenCheckFilter;
import com.leafup.leafupbackend.global.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final TokenProvider tokenProvider;

    @Bean
    public FilterRegistrationBean<TokenCheckFilter> loginTokenCheckFilter() {
        FilterRegistrationBean<TokenCheckFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new TokenCheckFilter(tokenProvider));
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<AdminAuthFilter> adminAuthFilter() {
        FilterRegistrationBean<AdminAuthFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new AdminAuthFilter(tokenProvider));
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/api/v1/admin/*");
        return filterRegistrationBean;
    }

}
