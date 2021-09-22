package com.techcourse.dao;

import com.techcourse.domain.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import nextstep.jdbc.JdbcTemplate;

public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(User user) {
        String sql = "insert into users (account, password, email) values (?, ?, ?)";

        jdbcTemplate.update(sql, pstmt -> {
            pstmt.setString(1, user.getAccount());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
        });
    }

    public void update(User user) {
        String sql = "update users set password = ? where id = ?";

        jdbcTemplate.update(sql, pstmt -> {
            pstmt.setString(1, user.getPassword());
            pstmt.setLong(2, user.getId());
        });
    }

    public List<User> findAll() {
        String sql = "select id, account, password, email from users";

        return jdbcTemplate.query(sql, this::getUser);
    }

    public User findById(Long id) {
        String sql = "select id, account, password, email from users where id = ?";

        return jdbcTemplate.queryForObject(
            sql,
            pstmt -> pstmt.setLong(1, id),
            rs -> {
                if (rs.next()) {
                    return getUser(rs);
                }
                return null;
            });
    }

    public User findByAccount(String account) {
        String sql = "select id, account, password, email from users where account = ?";

        return jdbcTemplate.queryForObject(
            sql,
            pstmt -> pstmt.setString(1, account),
            rs -> {
                if (rs.next()) {
                    return getUser(rs);
                }
                return null;
            });
    }

    private User getUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getLong("id"),
            rs.getString("account"),
            rs.getString("password"),
            rs.getString("email"));
    }
}
