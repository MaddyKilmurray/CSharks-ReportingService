package com.csharks.reportingservice.dao;

import com.csharks.reportingservice.enums.Status;
import com.csharks.reportingservice.enums.Truck;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // This sets the status to Enum Open whenever an opportunity object is created
    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Truck product;
    private Integer quantity;

    private Long decisionMaker;

    private Long accountId;

    private Long salesRepId;



    public Opportunity(Truck product, int quantity, Long decisionMaker, Long salesRepId) {
        this.status = Status.OPEN;
        setTruck(product);
        setQuantity(quantity);
        setDecisionMaker(decisionMaker);
        setSalesRepId(salesRepId);
    }

    public Opportunity(Status status, Truck product, int quantity, Long decisionMaker, Long salesRepId)  {
        setStatus(status);
        setTruck(product);
        setQuantity(quantity);
        setDecisionMaker(decisionMaker);
        setSalesRepId(salesRepId);
    }


    public Truck getProduct() {
        return product;
    }

    public void setTruck(Truck product) {
        this.product = product;
    }

    public void setQuantity(int quantity)  {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive. Please try again.");
        }
        this.quantity = quantity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
