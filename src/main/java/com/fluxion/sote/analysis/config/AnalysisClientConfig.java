package com.fluxion.sote.analysis.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(AiClientProperties.class)
public class AnalysisClientConfig {

    /** AI 서버 호출 전용 RestTemplate (이름 고정) */
    @Bean(name = "aiRestTemplate")
    public RestTemplate aiRestTemplate(AiClientProperties props) {
        int ms = props.getTimeoutSeconds() * 1000;

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(ms))
                .setResponseTimeout(Timeout.ofMilliseconds(ms))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        factory.setConnectTimeout(ms);
        factory.setReadTimeout(ms);

        return new RestTemplate(factory);
    }
}
