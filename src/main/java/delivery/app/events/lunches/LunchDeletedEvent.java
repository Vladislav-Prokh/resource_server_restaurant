package delivery.app.events.lunches;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LunchDeletedEvent extends ApplicationEvent {
    private final Long lunchId;

    public LunchDeletedEvent(Object source, Long  lunchId) {
        super(source);
        this.lunchId = lunchId;
    }
}
