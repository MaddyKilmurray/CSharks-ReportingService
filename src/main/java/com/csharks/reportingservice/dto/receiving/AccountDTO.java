package com.csharks.reportingservice.dto.receiving;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    private Long id;

    private String industry;
    private Integer employeeCount;

    private String city;
    private String country;
}
