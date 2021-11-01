package com.csharks.reportingservice.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Lead {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "contact_name")
  private String name;

  @Column(name = "phone_number")
  private String phoneNumber;

  private String email;

  @Column(name = "company_name")
  private String companyName;

  //  @ManyToOne
  //  @JoinColumn(name = "sales_rep_id", referencedColumnName = "id")
  @Column(name = "sales_rep_id")
  private Long salesRepId;


  // -------------------- Constructors --------------------
  public Lead(String name, String phoneNumber, String email, String companyName) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.companyName = companyName;
  }

  public Lead(String name, String phoneNumber, String email, String companyName, Long salesRepId) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.companyName = companyName;
    this.salesRepId = salesRepId;
  }
}
