package com.csharks.reportingservice.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportDTO {

    public String name;
    public Long count;
    public Double doubleCount;

    public ReportDTO(String name, Long count) {
        this.name = name;
        this.count = count;
    }

    public ReportDTO(String name, Double doubleCount) {
        this.name = name;
        this.doubleCount = doubleCount;
    }
}
