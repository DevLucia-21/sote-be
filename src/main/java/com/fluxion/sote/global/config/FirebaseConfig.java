package com.fluxion.sote.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${gcp.firebase.credentials.location}")
    private String firebaseCredentialsPath;

    @PostConstruct
    public void initialize() {
        try {

            System.out.println("Firebase credentials path = " + firebaseCredentialsPath);

            FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsPath.replace("file:", ""));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully.");
            }

        } catch (Exception e) {
            System.err.println("Firebase initialization failed: " + e.getMessage());
        }
    }
}
