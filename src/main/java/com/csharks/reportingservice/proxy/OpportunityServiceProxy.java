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
    Optional<Opportunity> getById(@PathVariable Long id);

    @GetMapping("/sales-rep/{id}")
    @ResponseStatus(HttpStatus.OK)
    List<Opportunity> getBySalesRepId(@PathVariable Long id);

    @PutMapping("/{id}/{status}")
    Opportunity changeStatus(@PathVariable Long id, @PathVariable String status);

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    Opportunity createOpp(@RequestBody OpportunityDTO opportunityDTO);

    //Needs to be written into the service
    @GetMapping("/sales-repAndStatus/{id}/{status}")
    @ResponseStatus(HttpStatus.OK)
    List<Opportunity> getBySalesRepIdAndStatus(@PathVariable Long id,@PathVariable String status);

    @GetMapping("/product/{product}")
    Long countOppsByProduct(@PathVariable(name = "product") String product);

    @GetMapping("/product")
    Long countOppsByProductAndStatus(@RequestParam String product, @RequestParam Status status);

    @GetMapping("/location/country/{country}")
    Long countOppsByCountry(@PathVariable(name = "country") String country);

    @GetMapping("/location/country")
    Long countOppsByCountryAndStatus(@RequestParam String country, @RequestParam Status status);

    @GetMapping("/location/city/{city}")
    Long countOppsByCity(@PathVariable(name = "city") String city);

    @GetMapping("/location/city")
    Long countOppsByCityAndStatus(@RequestParam String city, @RequestParam Status status);

    @GetMapping("/industry/{industry}")
    Long countOppsByIndustry(@PathVariable(name = "industry") String industry);

    @GetMapping("/industry")
    Long countOppsByIndustryAndStatus(@RequestParam String industry, @RequestParam Status status);

    @GetMapping("/product/mean")
    Double findMeanProductQuantity();

    @GetMapping("/product/max")
    Long findMaxProductQuantity();

    @GetMapping("/product/min")
    Long findMinProductQuantity();

    @GetMapping("/product/median")
    Long findMedianProductQuantity();

    @GetMapping("/mean/{accountId}")
    Double findMeanOppsByAccount(@PathVariable(name = "accountId") Long id);

    @GetMapping("/max/{accountId}")
    Long findMaxOppsByAccount(@PathVariable(name = "accountId") Long id);

    @GetMapping("/min/{accountId}")
    Long findMinOppsByAccount(@PathVariable(name = "accountId") Long id);

    @GetMapping("/median/{accountId}")
    Long findMedianOppsByAccount(@PathVariable(name = "accountId") Long id);

}
