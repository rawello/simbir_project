package com.simbirsoft.account.service;

import com.simbirsoft.account.exception.ResourceNotFoundException;
import com.simbirsoft.account.model.Account;
import com.simbirsoft.account.repository.AccountRepository;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    public Account createAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Account savedAccount = accountRepository.save(account);
        saveToElasticsearch(savedAccount);
        return savedAccount;
    }

    private void saveToElasticsearch(Account account) {
        IndexRequest request = new IndexRequest("accounts")
                .id(account.getId().toString())
                .source(Map.of(
                        "username", account.getUsername(),
                        "firstName", account.getFirstName(),
                        "lastName", account.getLastName(),
                        "roles", account.getRoles()
                ), XContentType.JSON);
        try {
            IndexResponse response = elasticsearchClient.index(request, RequestOptions.DEFAULT);
            System.out.println("Indexed with ID: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account updateAccount(Long id, Account account) {
        Account existingAccount = accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        existingAccount.setLastName(account.getLastName());
        existingAccount.setFirstName(account.getFirstName());
        existingAccount.setUsername(account.getUsername());
        existingAccount.setPassword(passwordEncoder.encode(account.getPassword()));
        existingAccount.setRoles(account.getRoles());
        Account updatedAccount = accountRepository.save(existingAccount);
        updateInElasticsearch(updatedAccount);
        return updatedAccount;
    }

    private void updateInElasticsearch(Account account) {
        UpdateRequest request = new UpdateRequest("accounts", account.getId().toString())
                .doc(Map.of(
                        "username", account.getUsername(),
                        "firstName", account.getFirstName(),
                        "lastName", account.getLastName(),
                        "roles", account.getRoles()
                ), XContentType.JSON);
        try {
            UpdateResponse response = elasticsearchClient.update(request, RequestOptions.DEFAULT);
            System.out.println("Updated with ID: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
        deleteFromElasticsearch(id);
    }

    private void deleteFromElasticsearch(Long id) {
        DeleteRequest request = new DeleteRequest("accounts", id.toString());
        try {
            DeleteResponse response = elasticsearchClient.delete(request, RequestOptions.DEFAULT);
            System.out.println("Deleted with ID: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}