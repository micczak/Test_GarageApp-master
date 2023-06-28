package org.example.model;

import lombok.*;
import org.example.model.Car;
import org.example.model.Garage;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate fromDate;

    @NotNull
    private LocalDate toDate;

    @ManyToOne
    @NotNull
    private Car car;

    @ManyToOne
    @NotNull
    private Garage garage;

    private boolean deleted;
}
