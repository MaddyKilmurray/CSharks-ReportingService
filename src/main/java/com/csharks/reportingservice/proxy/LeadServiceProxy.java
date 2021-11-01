package com.csharks.reportingservice.proxy;

import com.csharks.reportingservice.dto.receiving.LeadCountBySalesRepDTO;
import com.csharks.reportingservice.dto.receiving.LeadDTO;
import com.csharks.reportingservice.dto.receiving.NewLeadDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@FeignClient("LEAD-SERVICE")
public interface LeadServiceProxy {

    @GetMapping("/leads")
    @ResponseStatus(OK)
    public List<LeadDTO> getAllLeads();

    @GetMapping("/leads/{id}")
    @ResponseStatus(OK)
    public LeadDTO getLeadById(@PathVariable long id);

    @GetMapping("/leads/by-sales-rep/{id}")
    @ResponseStatus(OK)
    public List<LeadDTO> getLeadBySalesRepId(@PathVariable long id);

    @PostMapping("/leads")
    @ResponseStatus(CREATED)
    public LeadDTO createLead(@RequestBody NewLeadDTO newLead);

    @DeleteMapping("/leads/{id}")
    @ResponseStatus(OK)
    public LeadDTO deleteLeadById(@PathVariable long id);

    @GetMapping("/leads/report/count-by-sales-rep")
    @ResponseStatus(OK)
    public List<LeadCountBySalesRepDTO> getCountLeadsBySalesRepId();
}
