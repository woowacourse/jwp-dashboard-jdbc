package nextstep.jdbc;

import nextstep.jdbc.core.RowMapper;
import nextstep.jdbc.exception.NotSingleResultDataException;
import nextstep.jdbc.support.DataSourceConfig;
import nextstep.jdbc.support.DatabasePopulatorUtils;
import nextstep.jdbc.support.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class JdbcTemplateTest {

    private final AtomicLong seq = new AtomicLong(1);
    private JdbcTemplate jdbcTemplate;
    private RowMapper<User> userRowMapper;

    @BeforeEach
    void setUp() {
        DatabasePopulatorUtils.execute(DataSourceConfig.getInstance());
        jdbcTemplate = new JdbcTemplate<>(DataSourceConfig.getInstance());
        userRowMapper = userRowMapper = rs -> new User(
                rs.getLong("id"),
                rs.getString("account"),
                rs.getString("password"),
                rs.getString("email"));

        String sql = "insert into users (account, password, email) values (?, ?, ?)";
        jdbcTemplate.update(sql, "mazzi" + seq, "qwe123", "mazzi@woowa.com");
    }

    @DisplayName("DataSource가 존재하지 않는 경우 예외가 발생한다.")
    @Test
    void requireDataSource() {
        assertThatThrownBy(() -> new JdbcTemplate<>(null))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("영향을 받은 row count를 반환한다.")
    @Test
    void updateReturnCount() {
        String sql = "insert into users (account, password, email) values (?, ?, ?)";
        int rowCount = jdbcTemplate.update(sql, "mazzi", "qwe123", "mazzi@woowa.com");

        assertThat(rowCount).isEqualTo(1);
    }

    @DisplayName("result가 0인 경우 예외가 발생한다.")
    @Test
    void queryForObjectException() {
        String sql = "select * from users where id = ?";

        assertThatThrownBy(() -> jdbcTemplate.queryForObject(sql, userRowMapper, 0L))
                .isInstanceOf(NotSingleResultDataException.class);
    }

    @DisplayName("result가 1인 경우 조회한 객체를 반환한다.")
    @Test
    void queryForObject1() {
        String sql = "select * from users where id = ?";

        Object actual = jdbcTemplate.queryForObject(sql, userRowMapper, 1L);
        assertAll(
                () -> assertThat(actual).isInstanceOf(User.class),
                () -> {
                    User user = (User) actual;
                    assertThat(user.getId()).isEqualTo(1L);
                }
        );
    }

    @DisplayName("조회한 객체 리스트를 반환한다.")
    @Test
    void query() {
        String sql = "select * from users";
        List<User> actual = jdbcTemplate.query(sql, userRowMapper);

        assertAll(
                () -> assertThat(actual).isNotEmpty(),
                () -> assertThat(actual).isNotNull()
        );
    }
}