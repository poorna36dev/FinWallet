package com.poorna.fintech.service;

import java.util.List;


import org.springframework.stereotype.Service;

import com.poorna.fintech.dtos.TransactionResponse;
import com.poorna.fintech.dtos.UserRequest;
import com.poorna.fintech.dtos.UserResponse;
import com.poorna.fintech.entity.Transaction;
import com.poorna.fintech.entity.User;
import com.poorna.fintech.repository.UserRepo;
import com.poorna.fintech.exception.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public User createUser(String name, String email, String userName, String password) {
        if(userRepo.existsByUserNameAndEmail(userName,email)){
            throw new BadRequestException("User already Exists");
        } 
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setUserName(userName);
        user.setPassword(passwordEncoder.encode(password));
        return userRepo.save(user);

    }

    private UserResponse toUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setUserName(user.getUserName());
        return userResponse;
    }

    @Cacheable(value = "transactions", key = "#userId")
    public List<TransactionResponse> getUserTransactions(long userId) {
        User user = userRepo.findByIdJoinFetch(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Transaction> transactions = user.getWallets().stream()
                .flatMap(wallet -> wallet.getTransactions().stream())
                .toList();
        return toTransactionResponseList(transactions)      ;
    }

    public List<User> getUsers(){
        return userRepo.findAll();
    }
    private TransactionResponse toTransactionResponse(Transaction transaction){
        TransactionResponse response=new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setType(transaction.getType());
        response.setStatus(transaction.getStatus());
        response.setCurrency(transaction.getCurrency());
        return response;
    }

    private List<TransactionResponse> toTransactionResponseList(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toTransactionResponse)
                .toList();
    }
    @Cacheable(value = "users", key = "#id")
    public UserResponse getUser(Long id){
        User user=userRepo.findById(id).orElseThrow(()-> new UserNotFoundException("user not found"));
        return toUserResponse(user);
    }
    @CachePut(value = "users", key = "#userId")
    public UserResponse updateUser(long userId, UserRequest userRequest) {
        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setUserName(userRequest.getUserName());
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        return toUserResponse(userRepo.save(user));
    }
    @CachePut(value = "users", key = "#userId")
    public UserResponse updatePassword(long userId, String oldPassword, String newPassword) {
        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return toUserResponse(userRepo.save(user));
    }
    @CacheEvict(value = "users", key = "#userId")
    public UserResponse deactivateUser(long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setActive(false);
        return toUserResponse(userRepo.save(user));
    }

    public boolean isEmailExists(String email){
        return userRepo.existsByEmail(email);
    }

    public User findByEmailForPasswordReset(String email){
        return userRepo.findByEmail(email).orElseThrow(()-> new UserNotFoundException("If an account with that email exists, a password reset link has been sent."));
    }

    public User findByUsername(String username){
        return userRepo.findByUserName(username).orElseThrow(()-> new UserNotFoundException("User not Found"));
    }
}
