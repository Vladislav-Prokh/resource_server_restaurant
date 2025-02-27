package delivery.app.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
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
    private String frontendUrl;


    @PostConstruct
    private  void initStripe(){
        Stripe.apiKey = secretKey;
    }

    @PostMapping("/lunch/checkout-session")
    public ResponseEntity<String> createCheckoutSession(@RequestBody Map<String, Object> requestBody) {
        try {
            long quantity = Long.parseLong(requestBody.get("quantity").toString());

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setInvoiceCreation(
                            SessionCreateParams.InvoiceCreation.builder().setEnabled(true).build()
                    )
                    .setSuccessUrl(frontendUrl + "/payment/success")
                    .setCancelUrl(frontendUrl + "/payment/canceled")
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

    @PostMapping("/lunches/subscription/checkout-session")
    public ResponseEntity<String> createCheckoutSessionMonthSubscription() {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(frontendUrl + "/payment/success")
                    .setCancelUrl(frontendUrl + "/payment/canceled")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPrice("price_1Qx1Q5FaSQm5txzCx5Qcwu0r")
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
