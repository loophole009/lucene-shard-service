package com.example.lucene.kafka;

import com.example.lucene.model.IndexCommand;
import com.example.lucene.service.ShardManagerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class IndexCommandConsumer {

    @Autowired
    private ShardManagerService shardManagerService;

    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "shard-1-index", groupId = "lucene-shard-group")
    public void consume(String message) throws Exception {
        IndexCommand cmd = mapper.readValue(message, IndexCommand.class);
        shardManagerService.index(cmd);
    }
}
