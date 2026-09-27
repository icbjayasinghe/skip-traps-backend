package com.example.skiptrack.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;

/**
 * Bootstraps the Firebase Admin SDK and exposes a {@link Firestore} bean.
 *
 * Credentials resolution order:
 *  1. firebase.credentials.path property (a service-account JSON file, classpath or filesystem)
 *  2. GOOGLE_APPLICATION_CREDENTIALS environment variable (Application Default Credentials)
 */
@Configuration
public class FirestoreConfig {

    @Value("${firebase.credentials.path:}")
    private String credentialsPath;

    @Value("${firebase.project-id:}")
    private String projectId;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        GoogleCredentials credentials = resolveCredentials();

        FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                .setCredentials(credentials);

        if (projectId != null && !projectId.isBlank()) {
            optionsBuilder.setProjectId(projectId);
        }

        return FirebaseApp.initializeApp(optionsBuilder.build());
    }

    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }

    private GoogleCredentials resolveCredentials() throws IOException {
        if (credentialsPath != null && !credentialsPath.isBlank()) {
            try (InputStream serviceAccount = openCredentialsStream(credentialsPath)) {
                return GoogleCredentials.fromStream(serviceAccount);
            }
        }
        // Falls back to GOOGLE_APPLICATION_CREDENTIALS env var, gcloud CLI login,
        // or the metadata server when running on GCP/Cloud Run.
        return GoogleCredentials.getApplicationDefault();
    }

    private InputStream openCredentialsStream(String path) throws IOException {
        if (path.startsWith("classpath:")) {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource(path);
            return resource.getInputStream();
        }
        return new java.io.FileInputStream(path);
    }
}
