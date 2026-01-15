package com.kcdevdes.synk.controller;

import com.kcdevdes.synk.dto.request.UserCreateDTO;
import com.kcdevdes.synk.dto.request.UserUpdateDTO;
import com.kcdevdes.synk.dto.response.UserDTO;
import com.kcdevdes.synk.entity.UserEntity;
import com.kcdevdes.synk.mapper.UserMapper;
import com.kcdevdes.synk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return ResponseEntity.ok(UserMapper.toDTOList(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserEntity user = userService.getUserById(id);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserEntity user = userService.getUserByEmail(email);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserEntity user = userService.getUserByUsername(username);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        UserEntity entity = UserMapper.toEntity(createDTO);
        UserEntity created = userService.createUser(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        UserEntity entity = new UserEntity();
        UserMapper.updateEntity(entity, updateDTO);
        UserEntity updated = userService.updateUser(id, entity);
        return ResponseEntity.ok(UserMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/verify-email")
    public ResponseEntity<Void> verifyEmail(@PathVariable Long id) {
        userService.verifyEmail(id);
        return ResponseEntity.ok().build();
    }
}
