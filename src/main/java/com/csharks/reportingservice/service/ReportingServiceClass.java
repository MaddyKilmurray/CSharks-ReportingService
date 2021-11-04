package com.csharks.reportingservice.service;

import com.csharks.reportingservice.dao.Account;
import com.csharks.reportingservice.dao.Opportunity;
import com.csharks.reportingservice.dao.SalesRep;
import com.csharks.reportingservice.dto.receiving.LeadCountBySalesRepDTO;
import com.csharks.reportingservice.dto.report.ReportDTO;
import com.csharks.reportingservice.enums.Countries;
import com.csharks.reportingservice.enums.Industry;
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
import java.util.Optional;

@Service
public class ReportingServiceClass {

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

        if (dataType.equalsIgnoreCase("LEAD")) {
            List<LeadCountBySalesRepDTO> leads = getCountLeadsBySalesRepId();
            for (LeadCountBySalesRepDTO leadCount : leads) {
                Optional<SalesRep> salesRepByLeadCountId = salesReps.stream()
                        .filter(salesRep -> salesRep.getId().equals(leadCount.getSalesRepId()))
                        .findFirst();
                String salesRepNameByLeadCountId = salesRepByLeadCountId.isPresent() ?
                        salesRepByLeadCountId.get().getRepName() :
                        leadCount.getSalesRepId().toString();
                salesRepReport.add(new ReportDTO(salesRepNameByLeadCountId, leadCount.getLeadCount()));
            }

        } else if (dataType.equalsIgnoreCase("OPPORTUNITY")) {
            for (SalesRep sales : salesReps) {
                int count = getBySalesRepId(sales.getId()).size();
                ReportDTO newReport = new ReportDTO(sales.getRepName(), Long.valueOf(count));
                salesRepReport.add(newReport);
            }

        } else if ((dataType.replace("_", "-").replace(" ", "-").equalsIgnoreCase("CLOSED-WON")) ||
                (dataType.replace("_", "-").replace(" ", "-").equalsIgnoreCase("CLOSED-LOST"))) {
            for (SalesRep sales : salesReps) {
                String formattedDataType = dataType.replace(" ", "_").replace("-", "_").toUpperCase();
                int count = getBySalesRepIdAndStatus(sales.getId(), formattedDataType).size();
                ReportDTO newReport = new ReportDTO(sales.getRepName(), Long.valueOf(count));
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
                salesRepReport.add(new ReportDTO(product.name(), countOppsByProduct(product.toString())));
            }
        } else if (dataType.toUpperCase().equals("CLOSED_WON") || dataType.toUpperCase().equals("CLOSED_LOST") ||
                dataType.toUpperCase().equals("OPEN")) {
            for (Truck product : products) {
                salesRepReport.add(new ReportDTO(product.name(), countOppsByProductAndStatus(product.toString(), Status.valueOf(dataType))));
            }
        }
        return salesRepReport;
    }

    public List<ReportDTO> reportByCountry(String dataType) {
        List<SalesRep> salesReps = getAllSalesReps();
        List<ReportDTO> salesRepReport = new ArrayList<>();
        List<Countries> countries = Countries.createCountryList();
        if (dataType.toUpperCase().equals("ALL")) {
            for (Countries country : countries) {
                salesRepReport.add(new ReportDTO(country.name(), countOppsByCountry(country.toString())));
            }
        } else if (dataType.toUpperCase().equals("CLOSED_WON") || dataType.toUpperCase().equals("CLOSED_LOST") ||
                dataType.toUpperCase().equals("OPEN")) {
            for (Countries country : countries) {
                salesRepReport.add(new ReportDTO(country.name(), countOppsByCountryAndStatus(country.toString(), Status.valueOf(dataType))));
            }
        }
        return salesRepReport;
    }

//    public List<ReportDTO> reportByCity(String dataType) {
//        List<String> cities = accountServiceProxy.getCityList();
//        List<ReportDTO> salesRepReport = new ArrayList<>();
//
//    }

    public List<ReportDTO> reportByIndustry(String dataType) {
        List<SalesRep> salesReps = getAllSalesReps();
        List<ReportDTO> salesRepReport = new ArrayList<>();
        List<Industry> industries = Industry.createIndustryList();
        if (dataType.toUpperCase().equals("ALL")) {
            for (Industry industry : industries) {
                salesRepReport.add(new ReportDTO(industry.name(), countOppsByIndustry(industry.toString())));
            }
        } else if (dataType.toUpperCase().equals("CLOSED_WON") || dataType.toUpperCase().equals("CLOSED_LOST") ||
                dataType.toUpperCase().equals("OPEN")) {
            for (Industry industry : industries) {
                salesRepReport.add(new ReportDTO(industry.name(), countOppsByIndustryAndStatus(industry.toString(), Status.valueOf(dataType))));
            }
        }
        return salesRepReport;
    }

    public ReportDTO reportByEmployeeCount(String reportType) {
        ReportDTO report = null;
        if (reportType.equals("MEDIAN")) {
            report = new ReportDTO("Median Employee Count", findMedianEmployeeCount());
        } else if (reportType.equals("MAX")) {
            report = new ReportDTO("Maximum Employee Count", findMaxEmployeeCount());
        } else if (reportType.equals("MIN")) {
            report = new ReportDTO("Minimum Employee Count", findMinEmployeeCount());
        } else if (reportType.equals("MEAN")) {
            report = new ReportDTO("Mean Employee Count", findMeanEmployeeCount());
        }
        return report;
    }

    public ReportDTO reportByProductQuantity(String reportType) {
        ReportDTO report = null;
        if (reportType.equals("MEDIAN")) {
            report = new ReportDTO("Median Product Count", findMedianProductQuantity());
        } else if (reportType.equals("MAX")) {
            report = new ReportDTO("Maximum Product Count", findMaxProductQuantity());
        } else if (reportType.equals("MIN")) {
            report = new ReportDTO("Minimum Product Count", findMinProductQuantity());
        } else if (reportType.equals("MEAN")) {
            report = new ReportDTO("Mean Product Count", findMeanProductQuantity());
        }
        return report;
    }

    public List<ReportDTO> reportOppsNumbersByAccount(String dataType) {
        List<Account> accounts = getAllAccounts();
        List<ReportDTO> report = new ArrayList<>();
        if (dataType.equals("MEDIAN")) {
            for (Account account : accounts) {
                report.add(new ReportDTO("Median Opportunities By Account", findMedianOppsByAccount(account.getId())));
            }
        } else if (dataType.equals("MAX")) {
            for (Account account : accounts) {
                report.add(new ReportDTO("Maximum Opportunities By Account", findMaxOppsByAccount(account.getId())));
            }
        } else if (dataType.equals("MIN")) {
            for (Account account : accounts) {
                report.add(new ReportDTO("Minimum Opportunities By Account", findMinOppsByAccount(account.getId())));
            }
        } else if (dataType.equals("MEAN")) {
            for (Account account : accounts) {
                report.add(new ReportDTO("Mean Opportunities By Account", findMeanOppsByAccount(account.getId())));
            }
        }
        return report;
    }


    public List<Account> getAllAccounts() {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> accountServiceProxy.findAll(),
                throwable -> getAccountListFallback());
    }

    public Long countOppsByProduct(String product) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.countOppsByProduct(product),
                throwable -> null);
    }

    public Long countOppsByProductAndStatus(String product, Status status) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.countOppsByProductAndStatus(product, status),
                throwable -> null);
    }

    public Long countOppsByCountry(String country) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.countOppsByCountry(country),
                throwable -> null);
    }

    public Long countOppsByCountryAndStatus(String country, Status status) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.countOppsByCountryAndStatus(country, status),
                throwable -> null);
    }

    public Long countOppsByIndustry(String industry) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.countOppsByIndustry(industry),
                throwable -> null);
    }

    public Long countOppsByIndustryAndStatus(String industry, Status status) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.countOppsByIndustryAndStatus(industry, status),
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

    public List<Opportunity> getBySalesRepIdAndStatus(Long id, String status) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.getBySalesRepIdAndStatus(id, status),
                throwable -> getOppListFallback());
    }

    public List<SalesRep> getAllSalesReps() {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> salesRepServiceProxy.findAll(),
                throwable -> getSalesRepListFallback());
    }

    public Double findMeanEmployeeCount() {
        return accountServiceProxy.findMeanEmployeeCount();
    }

    public Long findMaxEmployeeCount() {
        return accountServiceProxy.findMaxEmployeeCount();
    }

    public Long findMinEmployeeCount() {
        return accountServiceProxy.findMinEmployeeCount();
    }

    public Long findMedianEmployeeCount() {
        return accountServiceProxy.findMedianEmployeeCount();
    }

    public Double findMeanProductQuantity() {
        return opportunityServiceProxy.findMeanProductQuantity();
    }

    public Long findMaxProductQuantity() {
        return opportunityServiceProxy.findMaxProductQuantity();
    }

    public Long findMinProductQuantity() {
        return opportunityServiceProxy.findMinProductQuantity();
    }

    public Long findMedianProductQuantity() {
        return opportunityServiceProxy.findMedianProductQuantity();
    }

    public Double findMeanOppsByAccount(Long id) {
        return opportunityServiceProxy.findMeanOppsByAccount(id);
    }

    public Long findMaxOppsByAccount(Long id) {
        return opportunityServiceProxy.findMaxOppsByAccount(id);
    }

    public Long findMinOppsByAccount(Long id) {
        return opportunityServiceProxy.findMinOppsByAccount(id);
    }

    public Long findMedianOppsByAccount(Long id) {
        return opportunityServiceProxy.findMedianOppsByAccount(id);
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
