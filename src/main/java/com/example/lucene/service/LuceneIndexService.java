package com.example.lucene.service;

import com.example.lucene.model.IndexCommand;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.KnnFloatVectorField;
import com.example.lucene.util.DemoWebSocketEmbeddings;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LuceneIndexService {
    private final IndexWriter indexWriter;
    private final FSDirectory dir;
    private final String shardId;

    public LuceneIndexService(String shardId) throws IOException {
        this.shardId = shardId;
        this.dir = FSDirectory.open(Paths.get("index-data/" + shardId));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        this.indexWriter = new IndexWriter(dir, config);
    }

    public String getShardId() {
        return shardId;
    }


    public void index(IndexCommand cmd) throws IOException {
        Document doc = new Document();

        doc.add(new Field("slug", cmd.getSlug(), StringField.TYPE_STORED));
        doc.add(new Field("name", cmd.getName(), TextField.TYPE_STORED));
        doc.add(new Field("category", cmd.getCategory(), TextField.TYPE_STORED));
        doc.add(new Field("description", cmd.getDescription(), TextField.TYPE_STORED));

        indexWriter.updateDocument(new Term("slug", cmd.getSlug()), doc);
        indexWriter.commit();
    }

    public void indexVector(IndexCommand cmd) throws IOException, InterruptedException, ExecutionException {
        Document doc = new Document();
        doc.add(new Field("slug", cmd.getSlug(), StringField.TYPE_STORED));
        doc.add(new Field("name", cmd.getName(), TextField.TYPE_STORED));
        doc.add(new Field("category", cmd.getCategory(), TextField.TYPE_STORED));
        doc.add(new Field("description", cmd.getDescription(), TextField.TYPE_STORED));

        CompletableFuture<float[]> future = DemoWebSocketEmbeddings.embedAsync(cmd.getName());
        float[] embedding = future.get();
        doc.add(new KnnFloatVectorField("embedding", embedding));

        indexWriter.updateDocument(new Term("slug", cmd.getSlug()), doc);
        indexWriter.commit();
    }

    public List<String> search(String queryStr) throws Exception {
        try (DirectoryReader reader = DirectoryReader.open(dir)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("name", new StandardAnalyzer());
            Query query = parser.parse(queryStr);
            List<String> results = new ArrayList<>();
            ScoreDoc[] searchResults = searcher.search(query, 10).scoreDocs;
            StoredFields storedFields = searcher.storedFields();
            for (ScoreDoc searchResult : searchResults) {
                Document hitDoc = storedFields.document(searchResult.doc);
                results.add(hitDoc.get("slug") + "; " + hitDoc.get("name") + "; " + hitDoc.get("category") + "; " + searchResult.score);
            }
            return results;
        }
    }

    public List<String> searchVector(String queryStr) throws Exception {
        try (DirectoryReader reader = DirectoryReader.open(dir)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            CompletableFuture<float[]> future = DemoWebSocketEmbeddings.embedAsync(queryStr);
            float[] embedding = future.get();
            Query query = new KnnFloatVectorQuery("embedding", embedding, 2);
            List<String> results = new ArrayList<>();
            ScoreDoc[] searchResults = searcher.search(query, 2).scoreDocs;
            StoredFields storedFields = searcher.storedFields();
            for (ScoreDoc searchResult : searchResults) {
                Document hitDoc = storedFields.document(searchResult.doc);
                float boostedScore = searchResult.score + 10.0f;
                results.add(hitDoc.get("slug") + "; " + hitDoc.get("name") + "; " + hitDoc.get("category") + "; " + boostedScore);
            }
            return results;
        }
    }
}