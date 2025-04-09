package com.example.lucene.service;

import com.example.lucene.model.IndexCommand;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.*;

@Service
public class ShardManagerService {
    private final LuceneIndexService primary;
    private final LuceneIndexService replica2;
    private final LuceneIndexService replica3;
    private final List<LuceneIndexService> replicas;
    private final AtomicInteger replicaSelector = new AtomicInteger(0);

    public ShardManagerService(LuceneIndexService primary, LuceneIndexService replica2, LuceneIndexService replica3) {
        this.primary = primary;
        this.replica2 = replica2;
        this.replica3 = replica3;
        this.replicas = List.of(replica2, replica3);
    }

    public void index(IndexCommand cmd) throws IOException {
        primary.index(cmd);
        LuceneIndexService targetReplica = replicas.get(replicaSelector.getAndIncrement() % replicas.size());
        targetReplica.index(cmd);
    }

//    public List<String> search(String q) throws Exception {
//        Set<String> seen = new HashSet<>();
//        List<String> combined = new ArrayList<>();
//
//        for (LuceneIndexService shard : List.of(primary, replica2, replica3)) {
//            for (String doc : shard.search(q)) {
//                String id = doc.split(":", 2)[0];
//                if (seen.add(id)) combined.add(doc);
//            }
//        }
//        return combined;
//    }


    public List<String> search(String q) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Set<String> result = ConcurrentHashMap.newKeySet();
        Set<String> seen = ConcurrentHashMap.newKeySet();

        List<Callable<Void>> tasks = List.of(
                () -> { fetchAndAdd(primary, q, seen, result); return null; },
                () -> { fetchAndAdd(replica2, q, seen, result); return null; },
                () -> { fetchAndAdd(replica3, q, seen, result); return null; }
        );

        try {
            executor.invokeAll(tasks);
        } finally {
            executor.shutdown();
        }

        return result.stream().toList();
    }

    private void fetchAndAdd(LuceneIndexService shard, String q, Set<String> seen, Set<String> result) {
        try {
            for (String doc : shard.search(q)) {
                String id = doc.split(":", 3)[0];
                if (seen.add(id)) {
                    result.add(doc);
                }
            }
        } catch (Exception e) {
            // Optionally log or handle shard-level failure
            System.err.println("Shard search failed: " + e.getMessage());
        }
    }

}
