package com.example.skiptrack.service;

import com.example.skiptrack.exception.NotFoundException;
import com.example.skiptrack.model.CountryDocument;
import com.example.skiptrack.model.Province;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CountryService {

    /** Firestore collection holding one document per country, e.g. skip-track/canada */
    private static final String COLLECTION = "skip-trap-test";

    private final Firestore firestore;

    public CountryService(Firestore firestore) {
        this.firestore = firestore;
    }

    // ---------- GET ----------

    /** Fetch the full country document (all provinces) by document id, e.g. "canada". */
    public CountryDocument getCountry(String countryId) {
        try {
            DocumentSnapshot snapshot = firestore.collection(COLLECTION)
                    .document(countryId)
                    .get()
                    .get();

            if (!snapshot.exists()) {
                throw new NotFoundException("Country document '" + countryId + "' not found in '" + COLLECTION + "'");
            }

            CountryDocument doc = snapshot.toObject(CountryDocument.class);
            return doc != null ? doc : new CountryDocument();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while reading Firestore", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to read country '" + countryId + "'", e);
        }
    }

    /** Fetch a single province by id within a country document. */
    public Province getProvince(String countryId, String provinceId) {
        return getCountry(countryId).getProvinces().stream()
                .filter(p -> provinceId.equals(p.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Province '" + provinceId + "' not found under country '" + countryId + "'"));
    }

    // ---------- CREATE / REPLACE WHOLE DOCUMENT ----------

    /** Create (or overwrite) the entire country document. */
    public CountryDocument createCountry(String countryId, CountryDocument document) {
        try {
            firestore.collection(COLLECTION).document(countryId).set(document).get();
            return document;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while writing Firestore", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to create country '" + countryId + "'", e);
        }
    }

    // ---------- ADD SINGLE PROVINCE ----------

    /**
     * Adds a single province to a country document. If a province with the same id
     * already exists it is replaced (upsert); otherwise it's appended.
     * Creates the country document if it doesn't exist yet.
     */
    public Province addProvince(String countryId, Province province) {
        if (province.getId() == null || province.getId().isBlank()) {
            throw new IllegalArgumentException("Province id must not be blank");
        }
        runUpsertTransaction(countryId, List.of(province));
        return province;
    }

    // ---------- BULK ADD PROVINCES ----------

    /**
     * Adds/replaces many provinces in a single atomic transaction. Existing
     * provinces sharing an id with an incoming one are replaced; the rest are kept.
     */
    public List<Province> addProvincesBulk(String countryId, List<Province> provinces) {
        if (provinces == null || provinces.isEmpty()) {
            throw new IllegalArgumentException("Province list must not be empty");
        }
        for (Province p : provinces) {
            if (p.getId() == null || p.getId().isBlank()) {
                throw new IllegalArgumentException("Every province must have a non-blank id");
            }
        }
        runUpsertTransaction(countryId, provinces);
        return provinces;
    }

    // ---------- shared upsert logic ----------

    private void runUpsertTransaction(String countryId, List<Province> incoming) {
        DocumentReference docRef = firestore.collection(COLLECTION).document(countryId);
        try {
            firestore.runTransaction((Transaction.Function<Void>) transaction -> {
                DocumentSnapshot snapshot = transaction.get(docRef).get();

                CountryDocument current;
                if (snapshot.exists()) {
                    CountryDocument existing = snapshot.toObject(CountryDocument.class);
                    current = existing != null ? existing : new CountryDocument();
                } else {
                    current = new CountryDocument();
                }

                List<Province> merged = new ArrayList<>(current.getProvinces());
                for (Province newProvince : incoming) {
                    merged.removeIf(existingProvince -> newProvince.getId().equals(existingProvince.getId()));
                    merged.add(newProvince);
                }
                current.setProvinces(merged);

                transaction.set(docRef, current);
                return null;
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while writing Firestore", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to write provinces for country '" + countryId + "'", e);
        }
    }
}
