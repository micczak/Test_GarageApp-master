package org.example.repository;

import feign.Param;
import org.example.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;


import javax.persistence.LockModeType;
import javax.persistence.QueryHint;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="5000")})
    @Query("SELECT r FROM Reservation r WHERE r.id IN :ids")
    List<Reservation> findAllByIds(@Param("ids") List<Long> ids);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="5000")})
    List<Reservation> findByDeletedFalse();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="5000")})
    Optional<Reservation> findByIdAndDeletedFalse(Long id);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="5000")})
    @Query("SELECT r FROM Reservation r WHERE r.garage.id = :garageId AND " +
            "((r.fromDate <= :toDate AND r.toDate >= :fromDate) OR " +
            "(r.fromDate <= :fromDate AND r.toDate >= :fromDate) OR " +
            "(r.fromDate <= :toDate AND r.toDate >= :toDate))")
    List<Reservation> findOverlappingReservations(@Param("fromDate") LocalDate fromDate,
                                                  @Param("toDate") LocalDate toDate,
                                                  @Param("garageId") Long garageId);
}
