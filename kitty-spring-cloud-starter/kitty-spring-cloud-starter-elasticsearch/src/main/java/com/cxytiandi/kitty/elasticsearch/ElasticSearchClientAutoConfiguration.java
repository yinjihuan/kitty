package com.cxytiandi.kitty.elasticsearch;

import com.cxytiandi.kitty.db.elasticsearch.client.KittyRestHighLevelClient;
import com.cxytiandi.kitty.db.elasticsearch.config.ElasticsearchProperties;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * ES 自动配置
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-03-09 23:05
 */
@ImportAutoConfiguration(ElasticsearchProperties.class)
@Configuration
public class ElasticSearchClientAutoConfiguration {

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient(ElasticsearchProperties elasticsearchProperties) {
        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost(elasticsearchProperties.getHostname(), elasticsearchProperties.getPort())
        );

        if (StringUtils.hasText(elasticsearchProperties.getUsername()) && StringUtils.hasText(elasticsearchProperties.getPassword())) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword()));
            restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }

        return new RestHighLevelClient(restClientBuilder);
    }

    @Bean
    public KittyRestHighLevelClient kittyRestHighLevelClient() {
        return new KittyRestHighLevelClient();
    }
}