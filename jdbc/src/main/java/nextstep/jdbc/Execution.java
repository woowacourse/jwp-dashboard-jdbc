package nextstep.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Execution<T> {
    T execute(PreparedStatement statement) throws SQLException;

    String getSql();
}
