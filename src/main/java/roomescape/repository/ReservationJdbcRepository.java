package roomescape.repository;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;

@Repository
public class ReservationJdbcRepository implements ReservationRepository {

    private static final RowMapper<Reservation> RESERVATION_ROW_MAPPER = (resultSet, rowNum) -> new Reservation(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getObject("date", LocalDate.class),
            new ReservationTime(
                    resultSet.getLong("time_id"),
                    resultSet.getTime("time_value").toLocalTime())
    );

    private final JdbcTemplate jdbcTemplate;

    public ReservationJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Reservation> findAll() {
        String sql = """
                SELECT r.id as reservation_id, r.name, r.date, t.id as time_id, t.start_at as time_value
                FROM reservation as r
                inner join reservation_time as t
                on r.time_id = t.id
                """;

        return jdbcTemplate.query(sql, RESERVATION_ROW_MAPPER);
    }

    @Override
    public Reservation save(Reservation reservation) {
        String sql = "INSERT INTO reservation (name, date, time_id) values (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreator preparedStatementCreator = connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, reservation.getName());
            statement.setString(2, reservation.getDate().toString());
            statement.setLong(3, reservation.getReservationTime().getId());
            return statement;
        };

        jdbcTemplate.update(preparedStatementCreator, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return new Reservation(id, reservation.getName(), reservation.getDate(), reservation.getReservationTime());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
