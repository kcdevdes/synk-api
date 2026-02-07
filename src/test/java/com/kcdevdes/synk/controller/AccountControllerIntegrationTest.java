package com.kcdevdes.synk.controller;

import com.kcdevdes.synk.dto.request.AccountCreateDTO;
import com.kcdevdes.synk.dto.request.AccountUpdateDTO;
import com.kcdevdes.synk.entity.AccountEntity;
import com.kcdevdes.synk.entity.UserEntity;
import com.kcdevdes.synk.entity.type.AccountType;
import com.kcdevdes.synk.repository.AccountRepository;
import com.kcdevdes.synk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("Account Integration Test")
public class AccountControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private AccountEntity testAccount;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        accountRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user first
        testUser = new UserEntity();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setMobile("+1234567890");
        testUser.setDefaultCurrency("USD");
        testUser.setLocale("en_US");
        testUser.setTimezone("America/New_York");
        testUser = userRepository.save(testUser);

        // Create test account
        testAccount = new AccountEntity();
        testAccount.setAccountName("Test Account");
        testAccount.setAccountType(AccountType.CREDIT_CARD);
        testAccount.setCurrency("USD");
        testAccount.setBalance(BigDecimal.valueOf(1000));
        testAccount.setAccountNumber("1234567890");
        testAccount.setBankName("Test Bank");
        testAccount.setDescription("Test Account Description");
        testAccount.setUser(testUser);
        testAccount = accountRepository.save(testAccount);
    }

    @Test
    @DisplayName("GET /api/accounts/{accountId} - Get account by ID")
    void getAccountById_ShouldReturnAccount() throws Exception {
        mockMvc.perform(get("/api/accounts/{accountId}", testAccount.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountName", is("Test Account")))
                .andExpect(jsonPath("$.currency", is("USD")))
                .andExpect(jsonPath("$.balance", is(1000)))
                .andExpect(jsonPath("$.accountNumber", is("1234567890")))
                .andExpect(jsonPath("$.bankName", is("Test Bank")))
                .andExpect(jsonPath("$.description", is("Test Account Description")))
                .andExpect(jsonPath("$.accountType", is("CREDIT_CARD")));
    }

    @Test
    @DisplayName("GET /api/accounts/user/{userId} - Get accounts by User ID")
    void getAccountsByUserId_ShouldReturnAccounts() throws Exception {
        mockMvc.perform(get("/api/accounts/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountName", is("Test Account")))
                .andExpect(jsonPath("$[0].currency", is("USD")))
                .andExpect(jsonPath("$[0].balance", is(1000)))
                .andExpect(jsonPath("$[0].accountNumber", is("1234567890")))
                .andExpect(jsonPath("$[0].bankName", is("Test Bank")))
                .andExpect(jsonPath("$[0].description", is("Test Account Description")))
                .andExpect(jsonPath("$[0].accountType", is("CREDIT_CARD")));
    }

    @Test
    @DisplayName("GET /api/accounts/user/{userId}/type/{accountType} - Get accounts by User ID and Account Type")
    void getAccountsByUserIdAndType_ShouldReturnAccounts() throws Exception {
        mockMvc.perform(get("/api/accounts/user/{userId}/type/{accountType}",
                        testUser.getId(), AccountType.CREDIT_CARD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountName", is("Test Account")))
                .andExpect(jsonPath("$[0].currency", is("USD")))
                .andExpect(jsonPath("$[0].balance", is(1000)))
                .andExpect(jsonPath("$[0].accountType", is("CREDIT_CARD")));
    }

    @Test
    @DisplayName("POST /api/accounts/user/{userId} - Create account successfully")
    void createAccount_ShouldReturnCreated() throws Exception {
        AccountCreateDTO createDTO = new AccountCreateDTO();
        createDTO.setAccountName("New Account");
        createDTO.setAccountType("BANK_ACCOUNT");
        createDTO.setCurrency("EUR");
        createDTO.setBalance(BigDecimal.valueOf(500));
        createDTO.setAccountNumber("9876543210");
        createDTO.setBankName("New Bank");
        createDTO.setDescription("New Account Description");

        mockMvc.perform(post("/api/accounts/user/{userId}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountName", is("New Account")))
                .andExpect(jsonPath("$.accountType", is("BANK_ACCOUNT")))
                .andExpect(jsonPath("$.currency", is("EUR")))
                .andExpect(jsonPath("$.balance", is(500)));
    }

    @Test
    @DisplayName("PUT /api/accounts/{accountId}/user/{userId} - Update account successfully")
    void updateAccount_ShouldReturnUpdated() throws Exception {
        AccountUpdateDTO updateDTO = new AccountUpdateDTO();
        updateDTO.setAccountName("Updated Account");
        updateDTO.setDescription("Updated Description");

        mockMvc.perform(put("/api/accounts/{accountId}/user/{userId}",
                        testAccount.getId(), testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountName", is("Updated Account")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.accountType", is("CREDIT_CARD")));  // unchanged
    }

    @Test
    @DisplayName("DELETE /api/accounts/{accountId}/user/{userId} - Soft delete account")
    void deleteAccount_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/accounts/{accountId}/user/{userId}",
                        testAccount.getId(), testUser.getId()))
                .andExpect(status().isNoContent());

        // Verify soft delete
        AccountEntity deletedAccount = accountRepository.findById(testAccount.getId()).orElseThrow();
        assert deletedAccount.getDeleted();
        assert !deletedAccount.getActive();
    }

    @Test
    @DisplayName("POST /api/accounts/{accountId}/user/{userId}/deposit - Deposit to account")
    void deposit_ShouldReturnUpdatedBalance() throws Exception {
        Map<String, BigDecimal> request = Map.of("amount", BigDecimal.valueOf(500));

        mockMvc.perform(post("/api/accounts/{accountId}/user/{userId}/deposit",
                        testAccount.getId(), testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(1500)));
    }

    @Test
    @DisplayName("POST /api/accounts/{accountId}/user/{userId}/withdraw - Withdraw from account")
    void withdraw_ShouldReturnUpdatedBalance() throws Exception {
        Map<String, BigDecimal> request = Map.of("amount", BigDecimal.valueOf(300));

        mockMvc.perform(post("/api/accounts/{accountId}/user/{userId}/withdraw",
                        testAccount.getId(), testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(700)));
    }

    @Test
    @DisplayName("POST /api/accounts/{accountId}/user/{userId}/withdraw - Insufficient balance should fail")
    void withdraw_InsufficientBalance_ShouldFail() throws Exception {
        Map<String, BigDecimal> request = Map.of("amount", BigDecimal.valueOf(2000));

        mockMvc.perform(post("/api/accounts/{accountId}/user/{userId}/withdraw",
                        testAccount.getId(), testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/accounts/user/{userId}/total-balance - Get total balance")
    void getTotalBalance_ShouldReturnTotalBalance() throws Exception {
        mockMvc.perform(get("/api/accounts/user/{userId}/total-balance", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBalance", is(1000)));
    }
}
