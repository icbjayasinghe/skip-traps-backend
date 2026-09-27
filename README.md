# skip-track-firestore

Spring Boot (Java 17) service that reads/writes the `skip-track` Firestore
collection using the Firebase Admin SDK. Each document in the collection is a
country (e.g. `canada`) with a single `provinces` array field, matching:

```
CountryDocument -> provinces: [ Province ]
Province        -> activities: [ ProvinceActivity ]
ProvinceActivity -> tasks: [ Task ]
```

## 1. Set up credentials

Firestore access needs a service-account key from your Firebase project:

1. Firebase Console → Project settings → **Service accounts** → **Generate new private key**.
2. Save the JSON somewhere on disk, e.g. `~/secrets/firebase-service-account.json`.
3. Point the app at it, either:
   - env var: `export GOOGLE_APPLICATION_CREDENTIALS=~/secrets/firebase-service-account.json`, or
   - `application.yml` / env var: `FIREBASE_CREDENTIALS_PATH=/absolute/path/to/key.json`

If `firebase.project-id` isn't set, it's inferred from the key file.

## 2. Run it

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`.

## 3. Endpoints

| Method | Path                                                | Purpose                                   |
|--------|------------------------------------------------------|--------------------------------------------|
| GET    | `/api/countries/{countryId}`                          | Get the full country document              |
| GET    | `/api/countries/{countryId}/provinces/{provinceId}`   | Get one province                           |
| POST   | `/api/countries/{countryId}`                           | Create/overwrite the whole country document|
| POST   | `/api/countries/{countryId}/provinces`                | Add (or upsert) **one** province            |
| POST   | `/api/countries/{countryId}/provinces/bulk`           | Add (or upsert) **many** provinces at once  |

Adding a province whose `id` already exists **replaces** that province
(upsert), so re-running a request is safe. All writes run inside a Firestore
transaction so concurrent requests can't clobber each other.

### Get a country document

```bash
curl http://localhost:8080/api/countries/canada
```

### Get a single province

```bash
curl http://localhost:8080/api/countries/canada/provinces/ns
```

### Add a single province

Uses `sample-requests/single-province-ns.json`, built from the Nova Scotia
data you supplied:

```bash
curl -X POST http://localhost:8080/api/countries/canada/provinces \
  -H "Content-Type: application/json" \
  -d @sample-requests/single-province-ns.json
```

### Bulk-add provinces

Uses `sample-requests/bulk-provinces.json` (the empty-activity provinces:
NL, AB, BC, MB, NB):

```bash
curl -X POST http://localhost:8080/api/countries/canada/provinces/bulk \
  -H "Content-Type: application/json" \
  -d @sample-requests/bulk-provinces.json
```

### Create/overwrite the entire document in one shot

If you want to seed the whole `canada` document at once (all provinces
including Ontario), POST a `CountryDocument` shaped body — `{ "provinces": [...] }`
— to `/api/countries/canada`.

## Notes

- `firestore.runTransaction` is used for both the single-add and bulk-add
  endpoints so partial writes never happen and duplicate-id provinces are
  replaced rather than duplicated.
- Swap `firebase-admin` for `google-cloud-firestore` directly if you'd
  rather manage credentials purely via ADC/Workload Identity — the `Firestore`
  bean shape stays the same either way.
