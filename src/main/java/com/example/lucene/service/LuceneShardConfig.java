package com.example.lucene.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@Configuration
public class LuceneShardConfig {

    @Bean
    @Primary
    public LuceneIndexService primaryShard() throws IOException {
        return new LuceneIndexService("primary");
    }

    @Bean
    public LuceneIndexService replica2Shard() throws IOException {
        return new LuceneIndexService("replica2");
    }

    @Bean
    public LuceneIndexService replica3Shard() throws IOException {
        return new LuceneIndexService("replica3");
    }
}