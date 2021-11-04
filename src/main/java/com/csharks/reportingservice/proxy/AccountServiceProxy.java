package com.csharks.reportingservice.proxy;

import com.csharks.reportingservice.dao.Account;
import com.csharks.reportingservice.dto.receiving.AccountDTO;
import com.csharks.reportingservice.enums.Countries;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("ACCOUNT-SERVICE")
@RequestMapping("/accounts")
public interface AccountServiceProxy {
    @GetMapping
    public List<AccountDTO> findAll();

    @GetMapping("/{id}")
    public AccountDTO findById(@PathVariable(name = "id") Long id);

    @PostMapping
    public AccountDTO create(@RequestBody AccountDTO accountDTO);

    @DeleteMapping("/{id}")
    public void remove(@PathVariable(name = "id") Long id);

    @GetMapping("/mean")
    public Double findMeanEmployeeCount();

    @GetMapping("/max")
    public Long findMaxEmployeeCount();

    @GetMapping("/min")
    public Long findMinEmployeeCount();

    @GetMapping("/median")
    public Long findMedianEmployeeCount();

    @GetMapping("/industry/{industry}")
    public List<Long> listIdByIndustry(@PathVariable String industry);

    @GetMapping("/country/{country}")
    public List<Long> listIdByCountry(@PathVariable Countries country);

    @GetMapping("/city")
    public List<String> getCityList();

    @GetMapping("/city/{id}")
    public String getCityById(@PathVariable(name = "id") Long id);
}
