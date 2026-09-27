package com.example.skiptrack.controller;

import com.example.skiptrack.model.CountryDocument;
import com.example.skiptrack.model.Province;
import com.example.skiptrack.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@Tag(name = "Countries", description = "Country/province/activity/task settlement checklists")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    // ---------- GET ----------

    @Operation(summary = "Get full country document", description = "Returns all provinces for a country.")
    @GetMapping("/{countryId}")
    public ResponseEntity<CountryDocument> getCountry(@PathVariable String countryId) {
        return ResponseEntity.ok(countryService.getCountry(countryId));
    }

    @Operation(summary = "Get a single province")
    @GetMapping("/{countryId}/provinces/{provinceId}")
    public ResponseEntity<Province> getProvince(@PathVariable String countryId,
                                                 @PathVariable String provinceId) {
        return ResponseEntity.ok(countryService.getProvince(countryId, provinceId));
    }

    // ---------- CREATE / REPLACE WHOLE DOCUMENT ----------

    @Operation(summary = "Create or overwrite a country document", description = "Replaces the entire provinces array for this country.")
    @PostMapping("/{countryId}")
    public ResponseEntity<CountryDocument> createCountry(@PathVariable String countryId,
                                                           @Valid @RequestBody CountryDocument document) {
        CountryDocument saved = countryService.createCountry(countryId, document);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ---------- ADD SINGLE PROVINCE ----------

    @Operation(summary = "Add or upsert a single province")
    @PostMapping("/{countryId}/provinces")
    public ResponseEntity<Province> addProvince(@PathVariable String countryId,
                                                 @Valid @RequestBody Province province) {
        Province saved = countryService.addProvince(countryId, province);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ---------- BULK ADD PROVINCES ----------

    @Operation(summary = "Bulk add or upsert provinces", description = "Adds/replaces many provinces in one atomic transaction.")
    @PostMapping("/{countryId}/provinces/bulk")
    public ResponseEntity<List<Province>> addProvincesBulk(@PathVariable String countryId,
                                                             @Valid @RequestBody List<Province> provinces) {
        List<Province> saved = countryService.addProvincesBulk(countryId, provinces);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
