package com.atguigu.gulimall.gulimallsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Hanlong
 * @description: some desc
 * @date: 2020/10/10 3:30 下午
 */
@Configuration
public class GulimallElasticSearchConfig {

    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClient(){
        RestClientBuilder builder = null;
        builder = RestClient.builder(new HttpHost("182.254.186.94",9200,"http"));
        RestHighLevelClient client = new RestHighLevelClient(builder);
       /* RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("39.108.94.54",9200,"http")
                )
        );*/
        return client;
    }
}
