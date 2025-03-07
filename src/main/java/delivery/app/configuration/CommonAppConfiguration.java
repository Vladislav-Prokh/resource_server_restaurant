package delivery.app.configuration;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


@Configuration
public class CommonAppConfiguration {


    public CommonAppConfiguration() {
    }
    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("restaurant-4e9e0-firebase-adminsdk-fbsvc-cd2bbd9e3e.json");

        if (serviceAccount == null) {
            throw new FileNotFoundException("Firebase service account key not found in classpath.");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    public void sendPushNotification(String token) {
        Notification notification = Notification.builder().setTitle("trere").setBody("sfsd").build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(token)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Сообщение отправлено с ID: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
