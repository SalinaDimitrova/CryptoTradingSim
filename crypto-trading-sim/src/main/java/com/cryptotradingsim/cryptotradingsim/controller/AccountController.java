package com.cryptotradingsim.cryptotradingsim.controller;

import com.cryptotradingsim.cryptotradingsim.model.Account;
import com.cryptotradingsim.cryptotradingsim.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<Account> getAccount() {
        return ResponseEntity.ok(accountService.getAccount());
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetAccount() {
        accountService.resetAccount();
        return ResponseEntity.ok("Account reset to default balance.");
    }
}
