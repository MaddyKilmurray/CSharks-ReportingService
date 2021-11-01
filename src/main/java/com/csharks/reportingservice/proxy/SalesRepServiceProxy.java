package com.csharks.reportingservice.proxy;

import com.csharks.reportingservice.dao.SalesRep;
import com.csharks.reportingservice.dto.receiving.SalesRepDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("SALESREP-SERVICE")
public interface SalesRepServiceProxy {

    @GetMapping("/salesrep")
    @ResponseStatus(HttpStatus.OK)
    public List<SalesRep> findAll();

    @GetMapping("/salesrep/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SalesRepDTO findById(@PathVariable(name = "id") Long id);

    @PostMapping("/salesrep")
    @ResponseStatus(HttpStatus.CREATED)
    public SalesRepDTO createSalesRep(@RequestBody SalesRepDTO salesRepDTO);

    @DeleteMapping("/salesrep/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable(name = "id") Long id);
}
