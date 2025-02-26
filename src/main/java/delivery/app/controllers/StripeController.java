package delivery.app.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/stripe")
public class StripeController {

    @Value("${payment.stripe.secret}")
    private String secretKey;
    @Value("${urls.paths.frontend}")
    private String frontendIp;


    @PostMapping("/checkout-session")
    public ResponseEntity<String> createCheckoutSession(@RequestBody Map<String, Object> requestBody) {
        Stripe.apiKey = secretKey;
        try {
            long quantity = Long.parseLong(requestBody.get("quantity").toString());
            String redirectDomain = "http://" + frontendIp + ":4200";

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(redirectDomain + "/payment/success")
                    .setCancelUrl(redirectDomain + "/payment/canceled")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(quantity)
                                    .setPrice("price_1QwfC2FaSQm5txzCi8Ff4VBj")
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            return ResponseEntity.ok(session.getUrl());
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating checkout session: " + e.getMessage());
        }
    }


}
