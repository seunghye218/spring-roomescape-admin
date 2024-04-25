package roomescape.dao;

import java.util.List;

public interface ReservationTimeDao {

    List<ReservationTime> findAll();
    ReservationTime findById(Long id);
    ReservationTime save(ReservationTime reservationTime);
    void delete(ReservationTime reservationTime);
}
