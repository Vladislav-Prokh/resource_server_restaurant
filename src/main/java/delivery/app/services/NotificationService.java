package delivery.app.services;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import delivery.app.notification.dto.DeviceNotificationRequest;
import delivery.app.notification.dto.NotificationSubscriptionRequest;
import delivery.app.notification.dto.TopicNotificationRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationService {

    private final FirebaseApp firebaseApp;


    public void sendPushNotificationToTopic(@Valid TopicNotificationRequest request) throws FirebaseMessagingException, ExecutionException, InterruptedException {
        Message fcmMessage = Message.builder()
                .setTopic(request.getTopicName())
                .setNotification(
                        Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .setImage(request.getImageUrl())
                                .build()
                )
                .setApnsConfig(getApnsConfig(request.getTopicName()))
                .putAllData(request.getData())
                .build();

        String response = FirebaseMessaging.getInstance(firebaseApp).sendAsync(fcmMessage).get();
        log.info("sendNotificationToDevice response: {}", response);
    }



    public void sendNotificationToDevice(DeviceNotificationRequest request) throws FirebaseMessagingException, ExecutionException, InterruptedException {
        Message fcmMessage = Message.builder()
                .setToken(request.getDeviceToken())
                .setNotification(
                        Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .setImage(request.getImageUrl())
                                .build()
                )
                .putAllData(request.getData())
                .build();

        String response = FirebaseMessaging.getInstance(firebaseApp).sendAsync(fcmMessage).get();
        log.info("sendNotificationToDevice response: {}", response);
    }

    public void subscribeDeviceToTopic(@Valid NotificationSubscriptionRequest request) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().subscribeToTopic(
                Collections.singletonList(request.getDeviceToken()),
                request.getTopicName()
        );
    }

    public void unsubscribeDeviceFromTopic(NotificationSubscriptionRequest request) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(
                Collections.singletonList(request.getDeviceToken()),
                request.getTopicName()
        );
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }
}
