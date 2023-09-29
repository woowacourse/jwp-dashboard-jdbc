package org.springframework.jdbc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper, final Object... args) {
        try (final Connection conn = dataSource.getConnection()) {
            final PreparedStatement pstmt = conn.prepareStatement(sql);
            setArguments(pstmt, args);
            ResultSet rs = pstmt.executeQuery();

            log.debug("query : {}", sql);

            return calculateResult(rowMapper, rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setArguments(final PreparedStatement pstmt, final Object[] args) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            final int parameterIndex = i + 1;
            pstmt.setObject(parameterIndex, args[i]);
        }
    }

    private static <T> T calculateResult(final RowMapper<T> rowMapper, final ResultSet rs) throws SQLException {
        if (rs.next()) {
            return rowMapper.mapRow(rs, rs.getRow());
        }

        return null;
    }

    public int update(final String sql, final Object... args) {
        try(final Connection conn = dataSource.getConnection()) {
            final PreparedStatement pstmt = conn.prepareStatement(sql);
            setArguments(pstmt, args);

            log.debug("query : {}", sql);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
