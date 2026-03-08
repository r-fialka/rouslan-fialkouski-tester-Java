package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    public void setUpPerTest() {
        ticket = new Ticket();
    }

    /**
     * Verifies that the fare for a car parked for exactly one hour
     * is equal to the hourly car rate.
     */
    @Test
    public void calculateFareCar_shouldReturnCorrectPriceRorOneHourParking() {

        // Arrange
        Date inTime = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
        Date outTime = new Date();

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act
        fareCalculatorService.calculateFare(ticket, false);

        // Assert
        assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice(), 0.01);
    }

    /**
     * Verifies that the fare for a bike parked for exactly one hour
     * is equal to the hourly bike rate.
     */
    @Test
    public void calculateFareBike_shouldReturnCorrectPriceRorOneHourParking() {

        // Arrange
        Date inTime = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act
        fareCalculatorService.calculateFare(ticket, false);

        // Assert
        assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice(), 0.01);
    }

    /**
     * Verifies that an exception is throws when the parking type is unknown.
     */
    @Test
    public void calculateFareUnkownType_souldThrowIllegalArgumentException() {

        // Arrange
        Date inTime = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> fareCalculatorService.calculateFare(ticket, false));
    }

    /**
     * Verifies that an exception is thrown if the in-time is after the out-time.
     */
    @Test
    public void calculateFareBikeWithFutureInTime_shouldThrowIllegalArgumentException() {

        // Arrange
        Date inTime = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> fareCalculatorService.calculateFare(ticket, false));
    }

    /**
     * Verifies that the fare is calculated proportionally when a bike
     * is parked for less than one hour (45 minutes).
     */
    @Test
    public void calculateFareBikeWithLessThanOneHourParking_shouldReturnProportionalPrice() {

        // Arrange
        Date inTime = new Date(System.currentTimeMillis() - 45 * 60 * 1000);
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act
        fareCalculatorService.calculateFare(ticket, false);

        // Assert
        assertEquals(0.75 * Fare.BIKE_RATE_PER_HOUR, ticket.getPrice(), 0.01);
    }

    /**
     * Verifies that parking for less than 30 minutes is free for cars.
     */
    @Test
    public void calculateFareCarWithLessThan30minutesParking_souldReturnZeroPrice() {

        // Arrange
        Date inTime = new Date(System.currentTimeMillis() - 20 * 60 * 1000);
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act
        fareCalculatorService.calculateFare(ticket, false);

        // Assert
        assertEquals(0, ticket.getPrice(), 0.01);
    }

    /**
     * Verifies that parking for less than 30 minutes is free for cars.
     */
    @Test
    public void calculateFareCarWithDiscount_shouldApplyFivePercentDiscount() {

        // Arrange
        Date inTime = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act
        fareCalculatorService.calculateFare(ticket, true);

        // Assert
        assertEquals(Fare.CAR_RATE_PER_HOUR * 0.95, ticket.getPrice(), 0.01);
    }

    /**
     * Verifies that a 5% discount is correctly applied for a recurring user with a car.
     */
    @Test
    public void calculateFareBikeWithDiscount_shouldApplyFivePercentDiscount() {

        // Arrange
        Date inTime = new Date(System.currentTimeMillis() - 90 * 60 * 1000);
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act
        fareCalculatorService.calculateFare(ticket, true);

        // Assert
        assertEquals(Fare.BIKE_RATE_PER_HOUR * 1.5 * 0.95,
                ticket.getPrice(), 0.01);
    }
}