package com.example.skiptrack.model;

import com.google.cloud.firestore.annotation.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class Province {

    private String id = "";
    private String name = "";
    private String abbreviation = "";
    private List<ProvinceActivity> activities = new ArrayList<>();

    public Province() {
    }

    public Province(String id, String name, String abbreviation, List<ProvinceActivity> activities) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.activities = activities != null ? activities : new ArrayList<>();
    }

    @PropertyName("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("abbreviation")
    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @PropertyName("activities")
    public List<ProvinceActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<ProvinceActivity> activities) {
        this.activities = activities;
    }
}
