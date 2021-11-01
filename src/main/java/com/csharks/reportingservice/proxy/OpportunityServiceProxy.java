package com.csharks.reportingservice.proxy;

import com.csharks.reportingservice.dao.Opportunity;
import com.csharks.reportingservice.dto.receiving.OpportunityDTO;
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
    public List<Opportunity> getAll();

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
    @GetMapping("/sales-repAndStatus/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Opportunity> getBySalesRepIdAndStatus(@PathVariable Long id,@RequestBody String status);

    @GetMapping("/product/{product}")
    public Long countOppsByProduct(@PathVariable(name = "product") Truck product);

    @GetMapping("/product")
    public Long countOppsByProductAndStatus(@RequestParam Truck product, @RequestParam Status status);

    @GetMapping("/location/country/{country}")
    public Long countOppsByCountry(@PathVariable(name = "country") String country);

    @GetMapping("/location/country")
    public Long countOppsByCountryAndStatus(@RequestParam String country, @RequestParam Status status);


}
