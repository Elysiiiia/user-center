package com.example.demo.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="spring.data.redis")
public class RedissonConfig {
    private String host;
    private int port;
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String redissonAddress = String.format("redis://%s:%s", host, port);
        System.out.println("Redis Address: " + redissonAddress);
        config.useSingleServer().setAddress(redissonAddress).setDatabase(1);
        RedissonClient redisson= Redisson.create(config);
        return redisson;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }
}
