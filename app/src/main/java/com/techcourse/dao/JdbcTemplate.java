package com.techcourse.dao;

import com.techcourse.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private ResultSet executeQuery(PreparedStatement pstmt) throws SQLException {
        return pstmt.executeQuery();
    }

    public void update(String sql, PreparedStatementSetter setter) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);

            setter.setValues(pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public <T> T query(String sql, RowMapper<T> rowMapper, PreparedStatementSetter setter) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            setter.setValues(pstmt);
            rs = executeQuery(pstmt);

            log.debug("query : {}", sql);

            if (rs.next()) {
                return rowMapper.mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = executeQuery(pstmt);

            log.debug("query : {}", sql);

            List<T> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rowMapper.mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }
}
