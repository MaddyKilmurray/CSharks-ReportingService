package com.csharks.reportingservice.dto.receiving;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LeadCountBySalesRepDTO {
  private Long salesRepId;
  private Long LeadCount;
}
