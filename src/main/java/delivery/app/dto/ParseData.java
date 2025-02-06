package delivery.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParseData {
    private String urlWebsite;
    private String userAgent;
    private String referrer;
    private String itemType;
    private String listClass;
    private String nameClass;
    private String descriptionClass;
    private String priceClass;
}

