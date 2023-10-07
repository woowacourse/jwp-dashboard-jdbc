package com.techcourse.dao;

import com.techcourse.domain.User;

import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    private final JdbcTemplate jdbcTemplate;

    public UserDao(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public UserDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(final Connection conn, final User user) {
        final var sql = "insert into users (account, password, email) values (?, ?, ?)";
        jdbcTemplate.execute(conn, sql, user.getAccount(), user.getPassword(), user.getEmail());
    }

    public void update(final Connection conn, final User user) {
        final var sql = "update users set account = ?, password = ?, email = ? where id = ?";
        jdbcTemplate.execute(conn, sql, user.getAccount(), user.getPassword(), user.getEmail(), user.getId());
    }

    public List<User> findAll(final Connection conn) {
        final var sql = "select id, account, password, email from users";
        return jdbcTemplate.query(conn, sql, rowMapper);
    }

    public User findById(final Connection conn, final Long id) {
        final var sql = "select id, account, password, email from users where id = ?";
        return jdbcTemplate.queryForObject(conn, sql, rowMapper, id);
    }

    public User findByAccount(final Connection conn, final String account) {
        final var sql = "select id, account, password, email from users where account = ?";
        return jdbcTemplate.queryForObject(conn, sql, rowMapper, account);
    }

    private final RowMapper<User> rowMapper = resultSet -> {
        Long id = resultSet.getLong("id");
        String account = resultSet.getString("account");
        String password = resultSet.getString("password");
        String email = resultSet.getString("email");
        return new User(id, account, password, email);
    };
}