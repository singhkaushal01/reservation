package com.gl.reservation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@Table(name = "reservation")
@AllArgsConstructor
public class ReservationEntity {
    @Id
    private String reservationId;
    private String userId;
    private String reservationDate;
    private String sourceLocation;
    private String destinationLocation;
}
