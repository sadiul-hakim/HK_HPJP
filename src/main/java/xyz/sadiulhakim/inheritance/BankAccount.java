package xyz.sadiulhakim.inheritance;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BankAccount")
public class BankAccount extends BillingDetails {

    @Column(name = "account_name", length = 75)
    private String accountName;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
