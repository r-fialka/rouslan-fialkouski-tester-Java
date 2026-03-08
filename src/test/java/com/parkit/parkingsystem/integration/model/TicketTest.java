package com.parkit.parkingsystem.integration.model;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketTest {

    /**
     * Verifies that all Ticket getters and setters correctly
     * store and return the expected values.
     */
    @Test
    void gettersAndSetters_shouldStoreAndReturnCorrectValues_whenTicketPropertiesAreUpdated() {

        // Arrange
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Date inTime = new Date();
        Date outTime = new Date();

        // Act
        ticket.setId(1);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABC123");
        ticket.setPrice(10.0);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);

        // Assert
        assertEquals(1, ticket.getId());
        assertEquals(parkingSpot, ticket.getParkingSpot());
        assertEquals("ABC123", ticket.getVehicleRegNumber());
        assertEquals(10.0, ticket.getPrice(), 0.01);
        assertEquals(inTime, ticket.getInTime());
        assertEquals(outTime, ticket.getOutTime());
    }
}
