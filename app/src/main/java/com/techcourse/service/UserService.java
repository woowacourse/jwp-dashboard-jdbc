package com.techcourse.service;

import com.techcourse.domain.User;

import java.sql.SQLException;

public interface UserService {

    User findbyId(final long id);

    void insert(final User user);

    void changePassword(final long id, final String newPassword, final String createBy) throws SQLException;
}
