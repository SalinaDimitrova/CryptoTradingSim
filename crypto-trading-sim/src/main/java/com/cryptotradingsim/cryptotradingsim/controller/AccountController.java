package com.cryptotradingsim.cryptotradingsim.controller;

import com.cryptotradingsim.cryptotradingsim.model.Account;
import com.cryptotradingsim.cryptotradingsim.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts/{accountId}")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<Account> getAccount(@PathVariable long accountId) {
        return ResponseEntity.ok(accountService.getAccount(accountId));
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetAccount(@PathVariable long accountId) {
        accountService.resetAccount(accountId);
        return ResponseEntity.ok("Account reset to default balance.");
    }
}
