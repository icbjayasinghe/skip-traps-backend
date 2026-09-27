package com.example.skiptrack.model;

import com.google.cloud.firestore.annotation.PropertyName;

/**
 * A single checklist item inside a ProvinceActivity.
 * Requires a public no-arg constructor + public getters/setters for Firestore's
 * automatic POJO <-> document mapping.
 */
public class Task {

    private String id = "";
    private String title = "";
    private String description = "";

    public Task() {
    }

    public Task(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    @PropertyName("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @PropertyName("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
