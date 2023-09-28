package org.springframework.jdbc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.SqlQueryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final ConnectionManager connectionManager;

    public JdbcTemplate(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void executeUpdate(String query, List<Object> parameters) {
        try (final Connection connection = connectionManager.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            log.info("query: {}", query);
            setParameters(preparedStatement, parameters);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new SqlQueryException(exception.getMessage(), query);
        }
    }

    public <T> T executeQuery(String query, List<Object> parameters, RowMapper<T> rowMapper) {
        try (final Connection connection = connectionManager.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query);
             final ResultSet resultSet = executePreparedStatementQuery(preparedStatement, parameters)) {
            log.info("query: {}", query);
            return rowMapper.mapRow(resultSet);
        } catch (SQLException exception) {
            throw new SqlQueryException(exception.getMessage(), query);
        }
    }

    private ResultSet executePreparedStatementQuery(PreparedStatement preparedStatement, List<Object> parameters)
            throws SQLException {
        setParameters(preparedStatement, parameters);
        return preparedStatement.executeQuery();
    }

    private void setParameters(PreparedStatement preparedStatement, List<Object> parameters) throws SQLException {
        for (int index = 1; index <= parameters.size(); index++) {
            preparedStatement.setString(index, String.valueOf(parameters.get(index - 1)));
        }
    }

}
