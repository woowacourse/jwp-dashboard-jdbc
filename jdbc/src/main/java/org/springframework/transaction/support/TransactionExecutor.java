package org.springframework.transaction.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionExecutor {

    private static final Logger log = LoggerFactory.getLogger(TransactionExecutor.class);

    private final DataSource dataSource;

    public TransactionExecutor(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute(final TransactionExecuteStrategy executeStrategy) {
        final Connection connection = getConnection();

        try {
            connection.setAutoCommit(false);

            executeStrategy.strategy(connection);

            connection.commit();
        } catch (SQLException e) {
            log.error("execute exception : {}", e);
            rollback(connection);
            throw new RuntimeException(e);
        } finally {
            close(connection);
        }
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error("getConnection exception : {}", e);
            throw new RuntimeException(e);
        }
    }

    public void rollback(final Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            log.error("rollback exception : {}", e);
            throw new RuntimeException(e);
        }
    }

    public void close(final Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("close exception : {}", e);
            throw new RuntimeException(e);
        }
    }
}
