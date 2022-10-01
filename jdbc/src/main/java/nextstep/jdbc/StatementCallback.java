package nextstep.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.IntStream;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.Nullable;

public abstract class StatementCallback implements AutoCloseable {

    private PreparedStatement pstmt;

    public StatementCallback(final PreparedStatement pstmt) {
        this.pstmt = pstmt;
    }

    public void setPreparedSql(@Nullable final Object... objects) {
        if (objects == null) {
            return;
        }
        IntStream.range(0, objects.length)
                .forEach(IntConsumerWrapper.accept(index -> pstmt.setObject(index + 1, objects[index])));
    }

    public abstract <T> T doInStatement(final ResultSetExtractor<T> resultSetExtractor);

    protected PreparedStatement getPstmt() {
        return pstmt;
    }

    @Override
    public void close() throws SQLException {
        if (pstmt != null || !pstmt.isClosed()) {
            pstmt.close();
        }
    }
}
