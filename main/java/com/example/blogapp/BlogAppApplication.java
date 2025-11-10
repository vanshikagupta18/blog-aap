package com.example.blogapp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class BlogAppApplication {

    public static void main(String[] args) {
        initializeFirebase();
        SpringApplication.run(BlogAppApplication.class, args);
    }

    // 🔥 Initialize Firebase Admin SDK when the application starts
    private static void initializeFirebase() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/firebase-service-account.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase Admin SDK initialized successfully!");
            } else {
                System.out.println("⚠️ Firebase already initialized.");
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to initialize Firebase: " + e.getMessage());
        }
    }
}
