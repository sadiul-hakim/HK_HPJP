package xyz.sadiulhakim.inheritance;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CreditCard")
public class CreditCard extends BillingDetails {

    @Column(name = "card_name", length = 55)
    private String cardName;

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}
