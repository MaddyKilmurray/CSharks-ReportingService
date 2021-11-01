package com.csharks.reportingservice.dto.receiving;

import com.csharks.reportingservice.dao.Lead;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LeadDTO {
  private Long id;
  private String name;
  private String phoneNumber;
  private String email;
  private String companyName;
  private Long salesRep;

  // ---------- LeadDTO constructor from Lead (dao) ----------
  public LeadDTO(Lead lead) {
    this.id = lead.getId();
    this.name = lead.getName();
    this.phoneNumber = lead.getPhoneNumber();
    this.email = lead.getEmail();
    this.companyName = lead.getCompanyName();
    this.salesRep = lead.getSalesRepId();
  }

}
