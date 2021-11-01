package com.csharks.reportingservice.dto.receiving;

import com.csharks.reportingservice.enums.Status;
import com.csharks.reportingservice.enums.Truck;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class OpportunityDTO {
    private Status status;
    private Truck product;
    private Integer quantity;
    private Long decisionMaker;
    private Long accountId;
    private Long salesRepId;


}
