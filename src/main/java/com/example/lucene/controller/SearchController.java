package com.example.lucene.controller;

import com.example.lucene.service.ShardManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private ShardManagerService shardManagerService;

    @GetMapping
    public List<String> search(@RequestParam String q) throws Exception {
        return shardManagerService.search(q);
    }
}