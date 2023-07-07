package com.zkd.demo.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.http.HttpHeaders;

@Configuration
public class ElasticsearchConfig {

    /**
     * localhost:9300 写在配置文件中就可以了
     */
    //@Bean
    RestHighLevelClient elasticsearchClient() {
        //HttpHeaders defaultHeaders = new HttpHeaders();
        //defaultHeaders.setBasicAuth(USER_NAME,USER_PASS);
        ClientConfiguration configuration = ClientConfiguration.builder()
                .connectedTo("localhost:9300")
                //.withConnectTimeout(Duration.ofSeconds(5))
                //.withSocketTimeout(Duration.ofSeconds(3))
                //.useSsl()
                //.withDefaultHeaders(defaultHeaders)
                //.withBasicAuth(username, password)
                // ... other options
                .build();
        RestHighLevelClient client = RestClients.create(configuration).rest();
        return client;
    }
}