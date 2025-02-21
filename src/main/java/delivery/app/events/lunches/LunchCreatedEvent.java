package delivery.app.events.lunches;

import delivery.app.entities.Lunch;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LunchCreatedEvent extends ApplicationEvent {
    private final Lunch lunch;

    public LunchCreatedEvent(Object source, Lunch lunch) {
        super(source);
        this.lunch = lunch;
    }
}
