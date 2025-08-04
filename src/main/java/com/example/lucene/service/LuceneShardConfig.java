package com.example.lucene.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@Configuration
public class LuceneShardConfig {

    @Bean("primary")
    public LuceneIndexService primaryShard() throws IOException {
        return new LuceneIndexService("primary");
    }

    @Bean("replica2")
    public LuceneIndexService replica2Shard() throws IOException {
        return new LuceneIndexService("replica2");
    }

    @Bean("replica3")
    public LuceneIndexService replica3Shard() throws IOException {
        return new LuceneIndexService("replica3");
    }

    @Bean("vector")
    public LuceneIndexService vectorShard() throws IOException {
        return new LuceneIndexService("vector");
    }
}