package org.springframework.jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void update(final String query, final Object... columns) {
        try (
                final Connection conn = dataSource.getConnection();
                final PreparedStatement pstmt = getPreparedstatement(conn, query, columns)
        ) {
            pstmt.executeUpdate();
        } catch (final SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }

    public <T> List<T> query(final String query, final RowMapper<T> rowMapper, final Object... columns) {
        try (
                final Connection conn = dataSource.getConnection();
                final PreparedStatement pstmt = getPreparedstatement(conn, query, columns);
                final ResultSet rs = pstmt.executeQuery()
        ) {
            final List<T> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rowMapper.map(rs));
            }
            return result;
        } catch (final SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }

    public <T> Optional<T> queryForObject(final String query, final RowMapper<T> rowMapper, final Object... columns) {
        try (
                final Connection conn = dataSource.getConnection();
                final PreparedStatement pstmt = getPreparedstatement(conn, query, columns);
                final ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                return Optional.of(rowMapper.map(rs));
            }
            return Optional.empty();
        } catch (final SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }

    private PreparedStatement getPreparedstatement(final Connection conn, final String query, final Object[] columns)
            throws SQLException {
        final PreparedStatement preparedStatement = conn.prepareStatement(query);
        setParameters(preparedStatement, columns);
        return preparedStatement;
    }

    private void setParameters(final PreparedStatement pstmt, final Object[] columns) throws SQLException {
        for (int i = 0; i < columns.length; i++) {
            pstmt.setObject(i + 1, columns[i]);
        }
    }
}
