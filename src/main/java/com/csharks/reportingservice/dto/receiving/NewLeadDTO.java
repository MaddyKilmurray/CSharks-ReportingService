package com.csharks.reportingservice.dto.receiving;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NewLeadDTO {
  private String name;
  private String phoneNumber;
  private String email;
  private String companyName;
  private Long salesRep;
}
