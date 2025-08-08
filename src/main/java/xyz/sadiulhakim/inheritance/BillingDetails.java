package xyz.sadiulhakim.inheritance;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "billing_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("BillingDetails")
public abstract class BillingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public BillingDetails() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
