package fin.dam.padel.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FirebaseInitializer {

	@PostConstruct
	public void initialize() {
	    try {
	        String firebaseConfig = System.getenv("FIREBASE_CREDENTIALS_JSON");

	        if (firebaseConfig == null || firebaseConfig.isEmpty()) {
	            System.out.println("⚠️ FIREBASE_CREDENTIALS_JSON is NOT set or is empty");
	            throw new RuntimeException("FIREBASE_CREDENTIALS_JSON environment variable not set");
	        }

	        System.out.println("✅ FIREBASE_CREDENTIALS_JSON loaded successfully. Length: " + firebaseConfig.length());

	        ByteArrayInputStream serviceAccount = new ByteArrayInputStream(
	                firebaseConfig.getBytes(StandardCharsets.UTF_8)
	        );

	        FirebaseOptions options = FirebaseOptions.builder()
	                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
	                .build();

	        if (FirebaseApp.getApps().isEmpty()) {
	            FirebaseApp.initializeApp(options);
	            System.out.println("✅ FirebaseApp initialized");
	        }

	    } catch (IOException e) {
	        System.out.println("🔥 Error initializing Firebase: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
}