package nextstep.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class ArgumentPreparedStatementSetter {

    private final Object[] args;

    public ArgumentPreparedStatementSetter(final Object[] args) {
        this.args = args;
    }

    public void setValues(PreparedStatement ps) throws SQLException {
        if (Objects.isNull(args)) {
            return;
        }

        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, this.args[i]);
        }
    }
}
