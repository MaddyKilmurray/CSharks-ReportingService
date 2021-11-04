package com.csharks.reportingservice.service;

import com.csharks.reportingservice.dao.Opportunity;
import com.csharks.reportingservice.dao.SalesRep;
import com.csharks.reportingservice.dto.receiving.AccountDTO;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        } else if ((dataType.replace("-", "_").replace(" ", "-").equalsIgnoreCase("CLOSED_WON")) ||
                (dataType.replace("-", "_").replace(" ", "-").equalsIgnoreCase("CLOSED_LOST"))) {
            for (SalesRep sales : salesReps) {
                String formattedDataType = dataType.replace(" ", "_").replace("-", "_").toUpperCase();
                int count = getBySalesRepIdAndStatus(sales.getId(), formattedDataType).size();
                salesRepReport.add(new ReportDTO(sales.getRepName(), Long.valueOf(count)));
            }
        }
        return salesRepReport;
    }

    public List<ReportDTO> reportByProduct(String dataType) {
        List<ReportDTO> salesRepReport = new ArrayList<>();
        List<Truck> products = Truck.createProductList();
        if (dataType.equalsIgnoreCase("ALL")) {
            for (Truck product : products) {
                salesRepReport.add(new ReportDTO(product.name(), countOppsByProduct(product.toString())));
            }
        } else if ((dataType.replace("-", "_").replace(" ", "-").equalsIgnoreCase("CLOSED_WON")) ||
                (dataType.replace("-", "_").replace(" ", "-").equalsIgnoreCase("CLOSED_LOST")) ||
                dataType.equalsIgnoreCase("OPEN")) {
            String formattedDataType = dataType.replace(" ", "_").replace("-", "_").toUpperCase();
            for (Truck product : products) {
                salesRepReport.add(new ReportDTO(product.name(), countOppsByProductAndStatus(product.toString(), Status.valueOf(formattedDataType))));
            }
        }
        return salesRepReport;
    }

    public List<ReportDTO> reportByCountry(String dataType) {
        List<ReportDTO> salesRepReport = new ArrayList<>();
        List<Countries> countries = Countries.createCountryList();
        if (dataType.equalsIgnoreCase("ALL")) {
            for (Countries country : countries) {
                salesRepReport.add(new ReportDTO(country.name(), countOppsByCountry(country.toString())));
            }
        } else if ((dataType.replace("-", "_").replace(" ", "-").equalsIgnoreCase("CLOSED_WON")) ||
                (dataType.replace("-", "_").replace(" ", "-").equalsIgnoreCase("CLOSED_LOST")) ||
                dataType.equalsIgnoreCase("OPEN")) {
            String formattedDataType = dataType.replace(" ", "_").replace("-", "_").toUpperCase();
            for (Countries country : countries) {
                salesRepReport.add(new ReportDTO(country.name(), countOppsByCountryAndStatus(country.toString(), Status.valueOf(formattedDataType))));
            }
        }
        return salesRepReport.stream()
                .sorted(Comparator.comparing(reportDTO -> reportDTO.getCount(), Comparator.reverseOrder()))
                .collect(Collectors.toList());
//        return salesRepReport;
    }

    public List<ReportDTO> reportByCity(String dataType) {
        List<String> cities = accountServiceProxy.getCityList();
        List<ReportDTO> salesRepReport = new ArrayList<>();
        if (dataType.equalsIgnoreCase("ALL")) {
            for (String city : cities) {
                salesRepReport.add(new ReportDTO(city, countOppsByCity(city)));
            }
        } else if ((dataType.replace("-", "_").replace(" ", "-").equalsIgnoreCase("CLOSED_WON")) ||
                (dataType.replace("-", "_").replace(" ", "-").equalsIgnoreCase("CLOSED_LOST")) ||
                dataType.equalsIgnoreCase("OPEN")) {
            String formattedDataType = dataType.replace(" ", "_").replace("-", "_").toUpperCase();
            for (String city : cities) {
                salesRepReport.add(new ReportDTO(city, countOppsByCityAndStatus(city, Status.valueOf(formattedDataType))));
            }
        }
        return salesRepReport.stream()
                .sorted(Comparator.comparing(reportDTO -> reportDTO.getCount(), Comparator.reverseOrder()))
                .collect(Collectors.toList());
//        return salesRepReport;
    }

    public List<ReportDTO> reportByIndustry(String dataType) {
        List<ReportDTO> salesRepReport = new ArrayList<>();
        List<Industry> industries = Industry.createIndustryList();
        if (dataType.equalsIgnoreCase("ALL")) {
            for (Industry industry : industries) {
                salesRepReport.add(new ReportDTO(industry.toString(), countOppsByIndustry(industry.toString())));
            }
        } else if ((dataType.replace("-", "_").replace(" ", "-").equalsIgnoreCase("CLOSED_WON")) ||
                (dataType.replace("-", "_").replace(" ", "-").equalsIgnoreCase("CLOSED_LOST")) ||
                dataType.equalsIgnoreCase("OPEN")) {
            String formattedDataType = dataType.replace(" ", "_").replace("-", "_").toUpperCase();
            for (Industry industry : industries) {
                salesRepReport.add(new ReportDTO(industry.name(), countOppsByIndustryAndStatus(industry.toString(), Status.valueOf(formattedDataType))));
            }
        }
        return salesRepReport;
    }

    public ReportDTO reportByEmployeeCount(String reportType) {
        ReportDTO report = null;
        if (reportType.equalsIgnoreCase("MEDIAN")) {
            report = new ReportDTO("Median Employee Count", findMedianEmployeeCount());
        } else if (reportType.equalsIgnoreCase("MAX")) {
            report = new ReportDTO("Maximum Employee Count", findMaxEmployeeCount());
        } else if (reportType.equalsIgnoreCase("MIN")) {
            report = new ReportDTO("Minimum Employee Count", findMinEmployeeCount());
        } else if (reportType.equalsIgnoreCase("MEAN")) {
            report = new ReportDTO("Mean Employee Count", findMeanEmployeeCount());
        }
        return report;
    }

    public ReportDTO reportByProductQuantity(String reportType) {
        ReportDTO report = null;
        if (reportType.equalsIgnoreCase("MEDIAN")) {
            report = new ReportDTO("Median Product Count", findMedianProductQuantity());
        } else if (reportType.equalsIgnoreCase("MAX")) {
            report = new ReportDTO("Maximum Product Count", findMaxProductQuantity());
        } else if (reportType.equalsIgnoreCase("MIN")) {
            report = new ReportDTO("Minimum Product Count", findMinProductQuantity());
        } else if (reportType.equalsIgnoreCase("MEAN")) {
            report = new ReportDTO("Mean Product Count", findMeanProductQuantity());
        }
        return report;
    }

    // TODO - Fix this method. It is returning the ** product quantity by account instead of ** opportunities.
    public ReportDTO reportOppsNumbersByAccount(String dataType) {
        List<AccountDTO> accounts = getAllAccounts();
        ReportDTO report = null;
        List<Long> nrOfOpportunities = new ArrayList<>();
        List<Opportunity> opportunities = getAllOpportunities();

        for (AccountDTO account : accounts) {
            nrOfOpportunities.add(
                    opportunities.stream()
                            .filter(opportunity -> opportunity.getAccountId().equals(account.getId()))
                            .count());
        }


        if (dataType.equalsIgnoreCase("MEDIAN")) {
            report = new ReportDTO("Median Opportunities By Account", median(nrOfOpportunities));
        } else if (dataType.equalsIgnoreCase("MAX")) {
            report = new ReportDTO("Maximum Opportunities By Account", max(nrOfOpportunities));
        } else if (dataType.equalsIgnoreCase("MIN")) {
            report = new ReportDTO("Minimum Opportunities By Account", min(nrOfOpportunities));
        } else if (dataType.equalsIgnoreCase("MEAN")) {
            report = new ReportDTO("Mean Opportunities By Account", mean(nrOfOpportunities));
        }
        return report;
    }

    public double mean(List<Long> listOfNumbers) {
        double sum = 0;
        for (Long number : listOfNumbers) {
            sum += number;
        }
        return sum / listOfNumbers.size();
    }

    public long max(List<Long> listOfNumbers) {
        long max = 0;
        for (Long number : listOfNumbers) {
            if (number > max) {
                max = number;
            }
        }
        return max;
    }


    public long min(List<Long> listOfNumbers) {
        long min = Long.MAX_VALUE;
        for (Long number : listOfNumbers) {
            if (number < min) {
                min = number;
            }
        }
        return min;
    }

    public long median(List<Long> listOfNumbers) {
        long median = 0;
        long size = listOfNumbers.size();
        if (size % 2 == 0) {
            median = (listOfNumbers.get((int) (size / 2)) + listOfNumbers.get((int) (size / 2 - 1))) / 2;
        } else {
            median = listOfNumbers.get((int) (size / 2));
        }
        return median;
    }


    public List<Opportunity> getAllOpportunities() {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.getAll(),
                throwable -> getOppListFallback());
    }

    public List<AccountDTO> getAllAccounts() {
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

    public Long countOppsByCity(String city) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.countOppsByCity(city),
                throwable -> null);
    }

    public Long countOppsByCityAndStatus(String city, Status status) {
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        return circuitBreaker.run(() -> opportunityServiceProxy.countOppsByCityAndStatus(city, status),
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

    public List<AccountDTO> getAccountListFallback() {
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
