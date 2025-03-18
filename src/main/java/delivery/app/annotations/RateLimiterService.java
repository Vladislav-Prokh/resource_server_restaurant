package delivery.app.annotations;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RateLimiterService {
    private final List<LocalDateTime> requestTimestamps = new ArrayList<>();

    public  boolean allowRequest(int limitPerMinute) {

        if (!requestTimestamps.isEmpty() &&
                requestTimestamps.get(0).isBefore(LocalDateTime.now().minusMinutes(1).minusSeconds(1))) {
            requestTimestamps.clear();
        }

        requestTimestamps.add(LocalDateTime.now());
        LocalDateTime minuteBefore = LocalDateTime.now().minusMinutes(5);
        int requestsCount = 0;
        for(LocalDateTime time : requestTimestamps) {
            if(time.isAfter(minuteBefore)) {
                requestsCount++;
            }
        }
        return requestsCount < limitPerMinute;
    }

}
