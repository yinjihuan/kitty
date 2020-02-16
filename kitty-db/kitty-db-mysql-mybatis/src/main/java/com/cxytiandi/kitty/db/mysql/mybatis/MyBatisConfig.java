package com.cxytiandi.kitty.db.mysql.mybatis;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * MyBatis配置
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-02-16 19:31
 */
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
