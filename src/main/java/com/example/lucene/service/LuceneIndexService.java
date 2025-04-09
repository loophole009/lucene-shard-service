package com.example.lucene.service;

import com.example.lucene.model.IndexCommand;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class LuceneIndexService {
    private IndexWriter indexWriter;
    private FSDirectory dir;
    private final String shardId;

    public LuceneIndexService(String shardId) throws IOException {
        this.shardId = shardId;
        dir = FSDirectory.open(Paths.get("index-data/" + shardId));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        this.indexWriter = new IndexWriter(dir, config);
    }

    public void index(IndexCommand cmd) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("id", cmd.getId(), Field.Store.YES));
        doc.add(new TextField("content", cmd.getContent(), Field.Store.YES));
        indexWriter.updateDocument(new Term("id", cmd.getId()), doc);
        indexWriter.commit();
    }

    public List<String> search(String queryStr) throws Exception {
        try (DirectoryReader reader = DirectoryReader.open(dir)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("content", new StandardAnalyzer());
            Query query = parser.parse(queryStr);
            TopDocs topDocs = searcher.search(query, 10);
            List<String> results = new ArrayList<>();
            for (ScoreDoc sd : topDocs.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                results.add(doc.get("id") + ": " + doc.get("content"));
            }
            return results;
        }
    }
}