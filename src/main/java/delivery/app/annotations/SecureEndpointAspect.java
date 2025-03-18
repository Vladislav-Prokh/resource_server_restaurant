package delivery.app.annotations;

import delivery.app.exceptions.RequestLimitException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@Aspect
@Component
public class SecureEndpointAspect {


    private final RateLimiterService rateLimiterService;
    private final Logger logger = LoggerFactory.getLogger(SecureEndpointAspect.class);

    @Autowired
    public SecureEndpointAspect(RateLimiterService rateLimiterService) {

        this.rateLimiterService = rateLimiterService;
    }

    @Around("@annotation(requestLimitedEndpoint)")
    public Object validateSecurity(ProceedingJoinPoint joinPoint, RequestLimitedEndpoint requestLimitedEndpoint) throws Throwable {

        if(!this.rateLimiterService.allowRequest(requestLimitedEndpoint.rateLimit())){
            throw new RequestLimitException("Too many request");
        }

        return joinPoint.proceed();
    }


}
