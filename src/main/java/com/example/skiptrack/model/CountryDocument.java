package com.example.skiptrack.model;

import com.google.cloud.firestore.annotation.PropertyName;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps 1:1 to a document in the "skip-track" Firestore collection,
 * e.g. skip-track/canada -> { provinces: [ ... ] }
 */
public class CountryDocument {

    private List<Province> provinces = new ArrayList<>();

    public CountryDocument() {
    }

    public CountryDocument(List<Province> provinces) {
        this.provinces = provinces != null ? provinces : new ArrayList<>();
    }

    @PropertyName("provinces")
    public List<Province> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<Province> provinces) {
        this.provinces = provinces;
    }
}
