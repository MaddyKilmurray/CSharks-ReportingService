package com.csharks.reportingservice.service;

import com.csharks.reportingservice.dao.Account;
import com.csharks.reportingservice.dao.Opportunity;
import com.csharks.reportingservice.dao.SalesRep;
import com.csharks.reportingservice.dto.receiving.LeadCountBySalesRepDTO;
import com.csharks.reportingservice.dto.receiving.LeadDTO;
import com.csharks.reportingservice.dto.report.ReportDTO;
import com.csharks.reportingservice.enums.Status;
import com.csharks.reportingservice.enums.Truck;
import com.csharks.reportingservice.proxy.AccountServiceProxy;
import com.csharks.reportingservice.proxy.LeadServiceProxy;
import com.csharks.reportingservice.proxy.OpportunityServiceProxy;
import com.csharks.reportingservice.proxy.SalesRepServiceProxy;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportingServiceClass{

    final LeadServiceProxy leadServiceProxy;
    final AccountServiceProxy accountServiceProxy;
    final OpportunityServiceProxy opportunityServiceProxy;
    final SalesRepServiceProxy salesRepServiceProxy;

    final CircuitBreakerFactory circuitBreakerFactory;

    public ReportingServiceClass(LeadServiceProxy leadServiceProxy, AccountServiceProxy accountServiceProxy, OpportunityServiceProxy opportunityServiceProxy, SalesRepServiceProxy salesRepServiceProxy, CircuitBreakerFactory circuitBreakerFactory) {
        this.leadServiceProxy = leadServiceProxy;
        this.accountServiceProxy = accountServiceProxy;
        this.opportunityServiceProxy = opportunityServiceProxy;
        this.salesRepServiceProxy = salesRepServiceProxy;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public List<ReportDTO> reportBySalesRep(String dataType) {
        List<SalesRep> salesReps = getAllSalesReps();
        List<ReportDTO> salesRepReport = new ArrayList<>();
        if (dataType.toUpperCase().equals("LEAD")) {
            List<LeadCountBySalesRepDTO> leads = getCountLeadsBySalesRepId();
            for (LeadCountBySalesRepDTO leadCount : leads) {
                salesRepReport.add(new ReportDTO(leadCount.getSalesRepId().toString(),leadCount.getLeadCount()));
            }
        }
        else if (dataType.toUpperCase().equals("OPPORTUNITY")) {
            for (SalesRep sales : salesReps) {
                int count = getBySalesRepId(sales.getId()).size();
                ReportDTO newReport = new ReportDTO(sales.getRepName(),Long.valueOf(count));
                salesRepReport.add(newReport);
            }
        }
        else if ((dataType.toUpperCase().equals("CLOSED-WON")) || (dataType.toUpperCase().equals("CLOSED-LOST"))) {
            for (SalesRep sales : salesReps) {
                int count = getBySalesRepIdAndStatus(sales.getId(),dataType).size();
                ReportDTO newReport = new ReportDTO(sales.getRepName(),Long.valueOf(count));
                salesRepReport.add(newReport);
            }
        }
        return salesRepReport;
    }

    public List<ReportDTO> reportByProduct(String dataType) {
        List<SalesRep> salesReps = getAllSalesReps();
        List<ReportDTO> salesRepReport = new ArrayList<>();
        List<Truck> products = Truck.createProductList();
        if (dataType.toUpperCase().equals("ALL")) {
            for (Truck product : products) {
                salesRepReport.add(new ReportDTO(product.name(),countOppsByProduct(product)));
            }
        }
        else if (dataType.toUpperCase().equals("CLOSED_WON") || dataType.toUpperCase().equals("CLOSED_LOST") ||
        dataType.toUpperCase().equals("OPEN")) {
            for (Truck product : products) {
                salesRepReport.add(new ReportDTO(product.name(), countOppsByProductAndStatus(product,Status.valueOf(dataType))));
            }
        }
        return salesRepReport;
    }



    public List<Account> getAllAccounts() {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> accountServiceProxy.findAll(),
                throwable -> getAccountListFallback());
    }

    public Long countOppsByProduct(Truck product) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.countOppsByProduct(product),
                throwable -> null);
    }

    public Long countOppsByProductAndStatus(Truck product, Status status) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.countOppsByProductAndStatus(product,status),
                throwable -> null);
    }

    public List<LeadCountBySalesRepDTO> getCountLeadsBySalesRepId() {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> leadServiceProxy.getCountLeadsBySalesRepId(),
                throwable -> getLeadCountFallback());
    }

    public List<Opportunity> getBySalesRepId(Long id) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.getBySalesRepId(id),
                throwable -> getOppListFallback());
    }

    public List<Opportunity> getBySalesRepIdAndStatus(Long id,String status) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.getBySalesRepIdAndStatus(id,status),
                throwable -> getOppListFallback());
    }

    public List<SalesRep> getAllSalesReps() {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> salesRepServiceProxy.findAll(),
                throwable -> getSalesRepListFallback());
    }

    public CircuitBreaker createCircuitBreaker() {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        return circuitBreaker;
    }

    public List<Account> getAccountListFallback() {
        return null;
    }
    public List<Opportunity> getOppListFallback() {
        return null;
    }
    public List<SalesRep> getSalesRepListFallback() {
        return null;
    }

    public List<LeadCountBySalesRepDTO> getLeadCountFallback() {
        return null;
    }
}
