package com.csharks.reportingservice.proxy;

import com.csharks.reportingservice.dao.Opportunity;
import com.csharks.reportingservice.dto.receiving.OpportunityDTO;
import com.csharks.reportingservice.enums.Countries;
import com.csharks.reportingservice.enums.Industry;
import com.csharks.reportingservice.enums.Status;
import com.csharks.reportingservice.enums.Truck;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@FeignClient("OPPORTUNITY-SERVICE")
@RequestMapping("/opps")
public interface OpportunityServiceProxy {

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    List<Opportunity> getAll();

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Opportunity> getById(@PathVariable Long id);

    @GetMapping("/sales-rep/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Opportunity> getBySalesRepId(@PathVariable Long id);

    @PutMapping("/{id}/{status}")
    public Opportunity changeStatus(@PathVariable Long id, @PathVariable String status);

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Opportunity createOpp(@RequestBody OpportunityDTO opportunityDTO);

    //Needs to be written into the service
    @GetMapping("/sales-repAndStatus/{id}/{status}")
    @ResponseStatus(HttpStatus.OK)
    public List<Opportunity> getBySalesRepIdAndStatus(@PathVariable Long id,@PathVariable String status);

    @GetMapping("/product/{product}")
    public Long countOppsByProduct(@PathVariable(name = "product") String product);

    @GetMapping("/product")
    public Long countOppsByProductAndStatus(@RequestParam String product, @RequestParam Status status);

    @GetMapping("/location/country/{country}")
    public Long countOppsByCountry(@PathVariable(name = "country") String country);

    @GetMapping("/location/country")
    public Long countOppsByCountryAndStatus(@RequestParam String country, @RequestParam Status status);

    @GetMapping("/industry/{industry}")
    public Long countOppsByIndustry(@PathVariable(name = "industry") String industry);

    @GetMapping("/industry")
    public Long countOppsByIndustryAndStatus(@RequestParam String industry, @RequestParam Status status);

    @GetMapping("/product/mean")
    public Double findMeanProductQuantity();

    @GetMapping("/product/max")
    public Long findMaxProductQuantity();

    @GetMapping("/product/min")
    public Long findMinProductQuantity();

    @GetMapping("/product/median")
    public Long findMedianProductQuantity();

    @GetMapping("/mean/{accountId}")
    public Double findMeanOppsByAccount(@PathVariable(name = "accountId") Long id);

    @GetMapping("/max/{accountId}")
    public Long findMaxOppsByAccount(@PathVariable(name = "accountId") Long id);

    @GetMapping("/min/{accountId}")
    public Long findMinOppsByAccount(@PathVariable(name = "accountId") Long id);

    @GetMapping("/median/{accountId}")
    public Long findMedianOppsByAccount(@PathVariable(name = "accountId") Long id);

}
