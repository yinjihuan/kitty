package com.cxytiandi.kitty.elasticsearch;

import com.cxytiandi.kitty.db.elasticsearch.client.KittyRestHighLevelClient;
import com.cxytiandi.kitty.db.elasticsearch.config.ElasticsearchProperties;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-03-09 23:05
 */
@Configuration
public class ElasticSearchClientAutoConfiguration {

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient(ElasticsearchProperties elasticsearchProperties) {
        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost(elasticsearchProperties.getHostname(), elasticsearchProperties.getPort())
        );
        return new RestHighLevelClient(restClientBuilder);
    }

    @Bean
    public KittyRestHighLevelClient kittyRestHighLevelClient() {
        return new KittyRestHighLevelClient();
    }
}