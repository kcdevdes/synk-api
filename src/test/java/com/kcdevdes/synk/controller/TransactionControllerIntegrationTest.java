package com.kcdevdes.synk.controller;

import com.kcdevdes.synk.dto.request.TransactionCreateDTO;
import com.kcdevdes.synk.dto.request.TransactionUpdateDTO;
import com.kcdevdes.synk.entity.AccountEntity;
import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.entity.UserEntity;
import com.kcdevdes.synk.entity.type.AccountType;
import com.kcdevdes.synk.entity.type.TransactionType;
import com.kcdevdes.synk.repository.AccountRepository;
import com.kcdevdes.synk.repository.TransactionRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("Transaction Integration Test")
public class TransactionControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private TransactionEntity testTransaction;
    private AccountEntity testAccount;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
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
        testAccount.setAccountType(AccountType.BANK_ACCOUNT);
        testAccount.setCurrency("USD");
        testAccount.setBalance(BigDecimal.valueOf(5000));
        testAccount.setAccountNumber("1234567890");
        testAccount.setBankName("Test Bank");
        testAccount.setUser(testUser);
        testAccount = accountRepository.save(testAccount);

        // Create test transaction
        testTransaction = new TransactionEntity();
        testTransaction.setType(TransactionType.EXPENSE);
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setMerchant("Test Merchant");
        testTransaction.setCurrency("USD");
        testTransaction.setCategory("Food");
        testTransaction.setDescription("Test Transaction");
        testTransaction.setTags("test,expense");
        testTransaction.setUser(testUser);
        testTransaction.setAccount(testAccount);
        testTransaction = transactionRepository.save(testTransaction);
    }

    // TODO: Fix TransactionService to handle accountId and userId from DTO
    // @Test
    @DisplayName("POST /api/transactions - Create transaction successfully")
    void createTransaction_ShouldReturnCreated() throws Exception {
        TransactionCreateDTO createDTO = new TransactionCreateDTO();
        createDTO.setType("INCOME");
        createDTO.setAmount(BigDecimal.valueOf(500));
        createDTO.setMerchant("Salary");
        createDTO.setCurrency("USD");
        createDTO.setCategory("Salary");
        createDTO.setDescription("Monthly salary");
        createDTO.setTags("income,salary");
        createDTO.setPaymentMethod("BANK_TRANSFER");
        createDTO.setAccountId(testAccount.getId());

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type", is("INCOME")))
                .andExpect(jsonPath("$.amount", is(500)))
                .andExpect(jsonPath("$.merchant", is("Salary")))
                .andExpect(jsonPath("$.currency", is("USD")))
                .andExpect(jsonPath("$.category", is("Salary")));
    }

    @Test
    @DisplayName("GET /api/transactions/{id} - Get transaction by ID")
    void getTransactionById_ShouldReturnTransaction() throws Exception {
        mockMvc.perform(get("/api/transactions/{id}", testTransaction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testTransaction.getId().intValue())))
                .andExpect(jsonPath("$.type", is("EXPENSE")))
                .andExpect(jsonPath("$.amount", is(100)))
                .andExpect(jsonPath("$.merchant", is("Test Merchant")))
                .andExpect(jsonPath("$.currency", is("USD")))
                .andExpect(jsonPath("$.category", is("Food")))
                .andExpect(jsonPath("$.description", is("Test Transaction")));
    }

    @Test
    @DisplayName("GET /api/transactions/{id} - Transaction not found")
    void getTransactionById_NotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/transactions/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/transactions - Get all transactions")
    void getAllTransactions_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].merchant", is("Test Merchant")));
    }

    @Test
    @DisplayName("PUT /api/transactions/{id} - Update transaction successfully")
    void updateTransaction_ShouldReturnUpdated() throws Exception {
        TransactionUpdateDTO updateDTO = new TransactionUpdateDTO();
        updateDTO.setAmount(BigDecimal.valueOf(150));
        updateDTO.setMerchant("Updated Merchant");
        updateDTO.setCategory("Updated Category");
        updateDTO.setDescription("Updated Description");

        mockMvc.perform(put("/api/transactions/{id}", testTransaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(150)))
                .andExpect(jsonPath("$.merchant", is("Updated Merchant")))
                .andExpect(jsonPath("$.category", is("Updated Category")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.type", is("EXPENSE")));  // unchanged
    }

    @Test
    @DisplayName("PUT /api/transactions/{id} - Update non-existent transaction should fail")
    void updateTransaction_NotFound_ShouldReturn404() throws Exception {
        TransactionUpdateDTO updateDTO = new TransactionUpdateDTO();
        updateDTO.setAmount(BigDecimal.valueOf(200));

        mockMvc.perform(put("/api/transactions/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/transactions/{id} - Soft delete transaction")
    void deleteTransaction_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/transactions/{id}", testTransaction.getId()))
                .andExpect(status().isNoContent());

        // Verify soft delete
        TransactionEntity deletedTransaction = transactionRepository.findById(testTransaction.getId()).orElseThrow();
        assert deletedTransaction.getDeleted();
    }

    @Test
    @DisplayName("GET /api/transactions/search?query={merchant} - Search by merchant")
    void searchTransactionsByMerchant_ShouldReturnResults() throws Exception {
        mockMvc.perform(get("/api/transactions/search")
                        .param("query", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].merchant", containsString("Test")));
    }

    @Test
    @DisplayName("GET /api/transactions/search?query= - Empty query should fail")
    void searchTransactionsByMerchant_EmptyQuery_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/transactions/search")
                        .param("query", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/transactions/filter?type={type} - Filter by type")
    void filterTransactionsByType_ShouldReturnResults() throws Exception {
        mockMvc.perform(get("/api/transactions/filter")
                        .param("type", "EXPENSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].type", is("EXPENSE")));
    }

    @Test
    @DisplayName("GET /api/transactions/filter?type= - Empty type should fail")
    void filterTransactionsByType_EmptyType_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/transactions/filter")
                        .param("type", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/transactions/filter?type=INVALID - Invalid type should fail")
    void filterTransactionsByType_InvalidType_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/transactions/filter")
                        .param("type", "INVALID_TYPE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/transactions/user/{userId} - Get transactions by user")
    void getTransactionsByUser_ShouldReturnResults() throws Exception {
        mockMvc.perform(get("/api/transactions/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].merchant", is("Test Merchant")));
    }

    @Test
    @DisplayName("GET /api/transactions/account/{accountId} - Get transactions by account")
    void getTransactionsByAccount_ShouldReturnResults() throws Exception {
        mockMvc.perform(get("/api/transactions/account/{accountId}", testAccount.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].merchant", is("Test Merchant")));
    }

    @Test
    @DisplayName("GET /api/transactions/user/{userId}/type/{type} - Get transactions by user and type")
    void getTransactionsByUserAndType_ShouldReturnResults() throws Exception {
        mockMvc.perform(get("/api/transactions/user/{userId}/type/{type}",
                        testUser.getId(), "EXPENSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].type", is("EXPENSE")))
                .andExpect(jsonPath("$[0].merchant", is("Test Merchant")));
    }

    @Test
    @DisplayName("GET /api/transactions/user/{userId}/type/INVALID - Invalid type should fail")
    void getTransactionsByUserAndType_InvalidType_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/transactions/user/{userId}/type/{type}",
                        testUser.getId(), "INVALID_TYPE"))
                .andExpect(status().isBadRequest());
    }
}
