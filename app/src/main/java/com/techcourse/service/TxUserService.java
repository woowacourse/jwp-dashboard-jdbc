package com.techcourse.service;

import com.techcourse.domain.User;
import nextstep.jdbc.exception.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TxUserService implements UserService {

    private final PlatformTransactionManager transactionManager;
    private final UserService userService;

    public TxUserService(final PlatformTransactionManager transactionManager, final UserService userService) {
        this.transactionManager = transactionManager;
        this.userService = userService;
    }

    @Override
    public User findById(final long id) {
        return userService.findById(id);
    }

    @Override
    public void insert(final User user) {
        userService.insert(user);
    }

    @Override
    public void changePassword(final long id, final String newPassword, final String createBy) {
        startTransaction(() -> userService.changePassword(id, newPassword, createBy));
    }

    private void startTransaction(final Runnable runnable) {
        final TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            runnable.run();
            transactionManager.commit(transactionStatus);
        } catch (final DataAccessException e) {
            transactionManager.rollback(transactionStatus);
            throw new DataAccessException();
        }
    }
}
