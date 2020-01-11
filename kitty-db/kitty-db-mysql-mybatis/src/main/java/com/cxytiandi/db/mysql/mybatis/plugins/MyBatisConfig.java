package com.cxytiandi.db.mysql.mybatis.plugins;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return new ConfigurationCustomizer() {
            @Override public void customize(MybatisConfiguration configuration) {
                configuration.addInterceptor(new CatMybatisInterceptor(datasourceUrl));
            }
        };
    }

}
