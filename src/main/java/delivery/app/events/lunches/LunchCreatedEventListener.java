package delivery.app.events.lunches;

import delivery.app.entities.Lunch;
import delivery.app.services.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LunchCreatedEventListener {

    private final ElasticService elasticService;

    public LunchCreatedEventListener(ElasticService elasticService) {
        this.elasticService = elasticService;
    }

    @EventListener
    public void onLunchCreated(LunchCreatedEvent event) throws IOException {
        Lunch lunch = event.getLunch();
        this.elasticService.saveLunch(lunch);

    }
    @EventListener
    public void onLunchDeleted(LunchDeletedEvent event) throws IOException {
        String lunchId = event.getLunchId().toString();
        this.elasticService.deleteLunch(lunchId);
    }
}
