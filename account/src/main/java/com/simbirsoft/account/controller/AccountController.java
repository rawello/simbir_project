package com.simbirsoft.account.controller;

import com.simbirsoft.account.model.Account;
import com.simbirsoft.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PutMapping("/signout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> signOut() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestParam String accessToken) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody Map<String, String> refreshToken) {
        return ResponseEntity.ok("New Token");
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Account> getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Account account = accountService.findByUsername(username);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Account> updateAccount(@RequestBody Account account) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Account existingAccount = accountService.findByUsername(username);
        existingAccount.setLastName(account.getLastName());
        existingAccount.setFirstName(account.getFirstName());
        existingAccount.setPassword(account.getPassword());
        return ResponseEntity.ok(accountService.updateAccount(existingAccount.getId(), existingAccount));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Account>> getAllAccounts(@RequestParam int from, @RequestParam int count) {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return ResponseEntity.ok(accountService.createAccount(account));
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

    @GetMapping("/doctors")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Account>> getDoctors(@RequestParam String nameFilter, @RequestParam int from, @RequestParam int count) {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/doctors/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Account> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }
}