package com.example.skiptrack.model;

import com.google.cloud.firestore.annotation.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class ProvinceActivity {

    private String id = "";
    private String title = "";
    private String summary = "";
    private String icon = "";
    private List<Task> tasks = new ArrayList<>();

    public ProvinceActivity() {
    }

    public ProvinceActivity(String id, String title, String summary, String icon, List<Task> tasks) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.icon = icon;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
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

    @PropertyName("summary")
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @PropertyName("icon")
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @PropertyName("tasks")
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
