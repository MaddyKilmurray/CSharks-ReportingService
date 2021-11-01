package com.csharks.reportingservice.proxy;

import com.csharks.reportingservice.dao.Account;
import com.csharks.reportingservice.dto.receiving.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("ACCOUNT-SERVICE")
public interface AccountServiceProxy {
    @GetMapping("/accounts")
    public List<Account> findAll();

    @GetMapping("/accounts/{id}")
    public AccountDTO findById(@PathVariable(name = "id") Long id);

    @PostMapping("/accounts")
    public AccountDTO create(@RequestBody AccountDTO accountDTO);

    @DeleteMapping("/accounts/{id}")
    public void remove(@PathVariable(name = "id") Long id);
}
