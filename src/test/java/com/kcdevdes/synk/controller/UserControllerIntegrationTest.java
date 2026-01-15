package com.kcdevdes.synk.controller;

import tools.jackson.databind.ObjectMapper;
import com.kcdevdes.synk.dto.request.UserCreateDTO;
import com.kcdevdes.synk.dto.request.UserUpdateDTO;
import com.kcdevdes.synk.entity.UserEntity;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest // Bean Creation
@Transactional // Rollback after each test
@ActiveProfiles("test") // Use application-test.properties
@DisplayName("UserController Integration Test")
class UserControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper; // java object <-> json

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        userRepository.deleteAll();

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
    }

    @Test
    @DisplayName("GET /api/users - Get all users")
    void getAllUsers_ShouldReturnUserList() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(equalTo(1))))
                .andExpect(jsonPath("$[0].email", is("test@example.com")))
                .andExpect(jsonPath("$[0].username", is("testuser")));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Get user by ID")
    void getUserById_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.firstName", is("Test")))
                .andExpect(jsonPath("$.lastName", is("User")));
    }

    @Test
    @DisplayName("GET /api/users/{id} - User not found")
    void getUserById_NotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - Get user by email")
    void getUserByEmail_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/email/{email}", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    @DisplayName("GET /api/users/username/{username} - Get user by username")
    void getUserByUsername_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/username/{username}", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    @DisplayName("POST /api/users - Create user successfully")
    void createUser_ShouldReturnCreated() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("newuser@example.com");
        createDTO.setUsername("newuser");
        createDTO.setPassword("password123");
        createDTO.setFirstName("New");
        createDTO.setLastName("User");
        createDTO.setMobile("+9876543210");
        createDTO.setDefaultCurrency("EUR");
        createDTO.setLocale("en_GB");
        createDTO.setTimezone("Europe/London");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("newuser@example.com")))
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.firstName", is("New")))
                .andExpect(jsonPath("$.lastName", is("User")))
                .andExpect(jsonPath("$.defaultCurrency", is("EUR")));
    }

    @Test
    @DisplayName("POST /api/users - Duplicate email should fail")
    void createUser_DuplicateEmail_ShouldReturn409() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("test@example.com"); // Duplicate
        createDTO.setUsername("anotheruser");
        createDTO.setPassword("password123");
        createDTO.setFirstName("Another");
        createDTO.setLastName("User");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/users - Duplicate username should fail")
    void createUser_DuplicateUsername_ShouldReturn409() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("another@example.com");
        createDTO.setUsername("testuser"); // Duplicate
        createDTO.setPassword("password123");
        createDTO.setFirstName("Another");
        createDTO.setLastName("User");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Update user successfully")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setFirstName("Updated");
        updateDTO.setLastName("Name");
        updateDTO.setMobile("+1111111111");
        updateDTO.setDefaultCurrency("GBP");

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Updated")))
                .andExpect(jsonPath("$.lastName", is("Name")))
                .andExpect(jsonPath("$.mobile", is("+1111111111")))
                .andExpect(jsonPath("$.defaultCurrency", is("GBP")))
                .andExpect(jsonPath("$.email", is("test@example.com"))) // Email unchanged
                .andExpect(jsonPath("$.username", is("testuser"))); // Username unchanged
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Update non-existent user should fail")
    void updateUser_NotFound_ShouldReturn404() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setFirstName("Updated");

        mockMvc.perform(put("/api/users/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Soft delete user successfully")
    void deleteUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId()))
                .andExpect(status().isNoContent());

        // Verify soft delete
        UserEntity deletedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assert deletedUser.getDeleted();
        assert !deletedUser.getActive();
    }

    @Test
    @DisplayName("POST /api/users/{id}/verify-email - Verify email successfully")
    void verifyEmail_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/users/{id}/verify-email", testUser.getId()))
                .andExpect(status().isOk());

        // Verify email is verified
        UserEntity verifiedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assert verifiedUser.getEmailVerified();
    }
}
