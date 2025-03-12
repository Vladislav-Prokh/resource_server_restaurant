package delivery.app.events.lunches;

import com.google.firebase.messaging.FirebaseMessagingException;
import delivery.app.entities.Lunch;
import delivery.app.notification.dto.TopicNotificationRequest;
import delivery.app.services.ElasticService;
import delivery.app.services.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class LunchCreatedEventListener {

    private final ElasticService elasticService;
    private final NotificationService notificationService;

    public LunchCreatedEventListener(ElasticService elasticService, NotificationService notificationService) {
        this.elasticService = elasticService;
        this.notificationService = notificationService;
    }

    @EventListener
    public void onLunchCreated(LunchCreatedEvent event) throws IOException, ExecutionException, FirebaseMessagingException, InterruptedException {
        Lunch lunch = event.getLunch();
        this.elasticService.saveLunch(lunch);
        //notification
        TopicNotificationRequest request = new TopicNotificationRequest("newLunch");
        request.setBody("body");
        Map<String, String> data = new HashMap<>();
        data.put("date:", LocalDateTime.now().toString());
        request.setData(data);
        request.setImageUrl("https://assets.bonappetit.com/photos/601de207bc23c5cd3769e5eb/16:9/w_2580,c_limit/Lunch-Tuna-Salad-Chickpeas.jpg?mbid=social_retweet");
        request.setTitle("New Lunch: "+lunch.getMainCourse().getMealName()+" + "+lunch.getDessert().getDessertName());
        notificationService.sendPushNotificationToTopic(request);

    }
    @EventListener
    public void onLunchDeleted(LunchDeletedEvent event) throws IOException {
        String lunchId = event.getLunchId().toString();
        this.elasticService.deleteLunch(lunchId);
    }
}
