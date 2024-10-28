package com.simbirsoft.account.controller;

import com.simbirsoft.account.model.Account;
import com.simbirsoft.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<Account> signUp(@RequestBody Account account) {
        return ResponseEntity.ok(accountService.createAccount(account));
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody Account account) {
        Account existingAccount = accountService.findByUsername(account.getUsername());
        if (existingAccount != null && accountService.passwordMatches(account.getPassword(), existingAccount.getPassword())) {
            return ResponseEntity.ok("Token");
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        return ResponseEntity.ok(accountService.updateAccount(id, account));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}