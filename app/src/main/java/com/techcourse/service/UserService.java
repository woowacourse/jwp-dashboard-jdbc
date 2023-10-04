package com.techcourse.service;

import com.techcourse.dao.UserDao;
import com.techcourse.dao.UserHistoryDao;
import com.techcourse.domain.User;
import com.techcourse.domain.UserHistory;
import org.springframework.jdbc.datasource.ConnectionManager;
import org.springframework.transaction.support.TransactionManager;

public class UserService {

    private final UserDao userDao;
    private final UserHistoryDao userHistoryDao;

    public UserService(final UserDao userDao, final UserHistoryDao userHistoryDao) {
        this.userDao = userDao;
        this.userHistoryDao = userHistoryDao;
    }

    public User findById(final long id) {
        return userDao.findById(id);
    }

    public void insert(final User user) {
        userDao.insert(user);
    }

    public void changePassword(final long id, final String newPassword, final String createBy) {
        final var user = findById(id);
        user.changePassword(newPassword);

        TransactionManager.begin();
        try {
            userDao.update(user);
            userHistoryDao.insert(new UserHistory(user, createBy));
        } catch (Exception e) {
            TransactionManager.setRollback();
            throw e;
        } finally {
            ConnectionManager.releaseConnection();
        }
    }
}
