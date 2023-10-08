package com.techcourse.service;

import com.techcourse.domain.User;

public class TxUserService implements UserService {

    private final UserService userService;

    public TxUserService(AppUserService appUserService) {
        this.userService = appUserService;
    }

    @Override
    public User findById(long id) {
        return TransactionExecutor.transactionQuery(() -> userService.findById(id));
    }

    @Override
    public void insert(User user) {
        TransactionExecutor.transactionCommand(() -> userService.insert(user));
    }

    @Override
    public void changePassword(long id, String newPassword, String createBy) {
        TransactionExecutor.transactionCommand(() -> userService.changePassword(id, newPassword, createBy));
    }
}
