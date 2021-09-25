package nextstep.jdbc;


import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {
    abstract T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
