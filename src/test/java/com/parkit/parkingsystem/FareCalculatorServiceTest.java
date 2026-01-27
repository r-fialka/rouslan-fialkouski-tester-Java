package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

/**
 * Test class for FareCalculatorService
 * Tests various fare calculation scenarios for parking system
 */
public class FareCalculatorServiceTest {

    // Shared FareCalculatorService instance for all tests
    private static FareCalculatorService fareCalculatorService;

    // Ticket instance recreated before each test
    private Ticket ticket;

    /**
     * Set up before all tests - initialize FareCalculatorService once
     */
    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    /**
     * Set up before each test - create fresh Ticket instance
     */
    @BeforeEach
    public void setUpPerTest() {
        ticket = new Ticket();
    }

    /**
     * Test case: Calculate fare for CAR parked for exactly 1 hour
     * Verifies basic fare calculation without discounts
     */
    @Test
    public void calculateFareCar(){
        // Arrange: Create time stamps for 1 hour parking duration
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) ); // 1 hour ago
        Date outTime = new Date(); // Current time

        // Create parking spot for CAR
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        // Setup ticket with parking details
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act: Calculate fare
        fareCalculatorService.calculateFare(ticket);

        // Assert: Should equal standard CAR rate per hour
        assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    /**
     * Test case: Calculate fare for BIKE parked for exactly 1 hour
     * Verifies basic fare calculation for bikes
     */
    @Test
    public void calculateFareBike(){
        // Arrange: Create time stamps for 1 hour parking duration
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) ); // 1 hour ago
        Date outTime = new Date(); // Current time

        // Create parking spot for BIKE
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        // Setup ticket with parking details
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act: Calculate fare
        fareCalculatorService.calculateFare(ticket);

        // Assert: Should equal standard BIKE rate per hour
        assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }

    /**
     * Test case: Calculate fare with null parking type
     * Verifies that NullPointerException is thrown when parking type is not specified
     */
    @Test
    public void calculateFareUnkownType(){
        // Arrange: Create time stamps for 1 hour parking
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) ); // 1 hour ago
        Date outTime = new Date(); // Current time

        // Create parking spot with NULL type (invalid scenario)
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        // Setup ticket
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act & Assert: Should throw NullPointerException when parking type is null
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * Test case: Invalid time scenario - out time before in time
     * Verifies validation of time logic
     */
    @Test
    public void calculateFareBikeWithFutureInTime(){
        // Arrange: Create invalid time scenario - in time is in the future
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) ); // 1 hour in FUTURE
        Date outTime = new Date(); // Current time (before in time)

        // Create parking spot for BIKE
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        // Setup ticket with invalid times
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act & Assert: Should throw IllegalArgumentException for invalid time sequence
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * Test case: Calculate fare for BIKE parked for 45 minutes (less than 1 hour)
     * Verifies calculation for partial hours
     */
    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        // Arrange: 45 minutes parking duration
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) ); // 45 minutes ago
        Date outTime = new Date(); // Current time

        // Create parking spot for BIKE
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        // Setup ticket
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act: Calculate fare
        fareCalculatorService.calculateFare(ticket);

        // Assert: Should be 75% of hourly rate (45/60 = 0.75)
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    /**
     * Test case: Calculate fare for CAR parked for 45 minutes (less than 1 hour)
     * Verifies pro-rata calculation for cars
     */
    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        // Arrange: 45 minutes parking duration
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) ); // 45 minutes ago
        Date outTime = new Date(); // Current time

        // Create parking spot for CAR
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        // Setup ticket
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act: Calculate fare
        fareCalculatorService.calculateFare(ticket);

        // Assert: Should be 75% of hourly rate
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    /**
     * Test case: Calculate fare for CAR parked for 24 hours
     * Verifies calculation for extended parking duration
     */
    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        // Arrange: 24 hours parking duration
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) ); // 24 hours ago
        Date outTime = new Date(); // Current time

        // Create parking spot for CAR
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        // Setup ticket
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act: Calculate fare
        fareCalculatorService.calculateFare(ticket);

        // Assert: Should be 24 times the hourly rate
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    /**
     * Test case: Calculate fare for CAR parked for 20 minutes (less than free period)
     * Verifies free parking for short durations
     */
    @Test
    public void calculateFareCarWithLessThan30minutesParkingTime() {
        // Arrange: 20 minutes parking duration (within free period)
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000)); // 20 minutes ago
        Date outTime = new Date(); // Current time

        // Create parking spot for CAR
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Setup ticket
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act: Calculate fare
        fareCalculatorService.calculateFare(ticket);

        // Assert: Should be free (0) for parking less than 30 minutes
        assertEquals(0, ticket.getPrice());
    }

    /**
     * Test case: Calculate fare for BIKE parked for 25 minutes (less than free period)
     * Verifies free parking for bikes for short durations
     */
    @Test
    public void calculateFareBikeWithLessThan30minutesParkingTime() {
        // Arrange: 25 minutes parking duration (within free period)
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (25 * 60 * 1000)); // 25 minutes ago
        Date outTime = new Date(); // Current time

        // Create parking spot for BIKE
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        // Setup ticket
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act: Calculate fare
        fareCalculatorService.calculateFare(ticket);

        // Assert: Should be free (0) for parking less than 30 minutes
        assertEquals(0, ticket.getPrice());
    }

    /**
     * Test case: Calculate fare for CAR with 5% discount for returning customers
     * Verifies discount application logic
     */
    @Test
    public void calculateFareCarWithDiscount() {
        // Arrange: 1 hour parking duration
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 hour ago
        Date outTime = new Date(); // Current time

        // Create parking spot for CAR
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Setup ticket
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act: Calculate fare WITH discount (true parameter)
        fareCalculatorService.calculateFare(ticket, true);

        // Assert: Should be 95% of standard rate (5% discount)
        assertEquals(Fare.CAR_RATE_PER_HOUR * 0.95, ticket.getPrice());
    }

    /**
     * Test case: Calculate fare for BIKE with 5% discount for returning customers
     * Verifies discount application for bikes with fractional hours
     */
    @Test
    public void calculateFareBikeWithDiscount() {
        // Arrange: 1.5 hours (90 minutes) parking duration
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (90 * 60 * 1000)); // 1.5 hours ago
        Date outTime = new Date(); // Current time

        // Create parking spot for BIKE
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        // Setup ticket
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act: Calculate fare WITH discount (true parameter)
        fareCalculatorService.calculateFare(ticket, true);

        // Assert: Should be (1.5 * hourly rate) * 0.95 (with 5% discount)
        assertEquals(Fare.BIKE_RATE_PER_HOUR * 1.5 * 0.95, ticket.getPrice());
    }
}