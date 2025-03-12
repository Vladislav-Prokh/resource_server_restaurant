package delivery.app.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationSubscriptionRequest  extends NotificationRequest {
    @NotBlank
    private String deviceToken;
    @NotBlank
    private String topicName;
}
