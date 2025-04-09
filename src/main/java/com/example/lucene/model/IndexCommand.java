package com.example.lucene.model;

//public class IndexCommand {
//    private String id;
//    private String content;
//
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//    public String getContent() { return content; }
//    public void setContent(String content) { this.content = content; }
//}

public class IndexCommand {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String slug;
    private String category;
    private String description;
}