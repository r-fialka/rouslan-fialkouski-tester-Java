package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    private ParkingService parkingService;

    @Mock
    private InputReaderUtil inputReaderUtil;

    @Mock
    private ParkingSpotDAO parkingSpotDAO;

    @Mock
    private TicketDAO ticketDAO;

    @BeforeEach
    public void setUp() {
        // Initialize ParkingService with mocked dependencies before each test
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    /**
     * Test case: Process incoming vehicle successfully
     * Verifies that a vehicle can be parked and ticket is saved
     */
    @Test
    void testProcessIncomingVehicle() throws Exception {
        // Arrange: Setup mock responses
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR selection
        when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(1); // Returning customer
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1); // Spot available
        when(parkingSpotDAO.updateParking(any())).thenReturn(true); // Parking update successful

        // Act: Process incoming vehicle
        parkingService.processIncomingVehicle();

        // Assert: Verify interactions with DAOs
        verify(ticketDAO).saveTicket(any(Ticket.class)); // Ticket should be saved
        verify(parkingSpotDAO).updateParking(any(ParkingSpot.class)); // Parking spot should be updated
    }

    /**
     * Test case: Process exiting vehicle successfully
     * Verifies vehicle exit with fare calculation and parking spot update
     */
    @Test
    void processExitingVehicleTest() throws Exception {
        // Arrange: Create a ticket for a parked vehicle
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - 3600000)); // 1 hour ago
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false)); // Occupied spot
        ticket.setVehicleRegNumber("ABCDEF");

        // Setup mock responses
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket); // Ticket found
        when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(2); // Returning customer for discount
        when(ticketDAO.updateTicket(any())).thenReturn(true); // Ticket update successful

        // Act: Process exiting vehicle
        parkingService.processExitingVehicle();

        // Assert: Parking spot should be updated to available
        verify(parkingSpotDAO).updateParking(any());
    }

    /**
     * Test case: Process exiting vehicle when ticket update fails
     * Verifies that parking spot is not updated when ticket update fails
     */
    @Test
    void processExitingVehicleTestUnableUpdate() throws Exception {
        // Arrange: Create a ticket
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date()); // Current time
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setVehicleRegNumber("ABCDEF");

        // Setup mock responses
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket); // Ticket found
        when(ticketDAO.updateTicket(any())).thenReturn(false); // Ticket update FAILS

        // Act: Process exiting vehicle
        parkingService.processExitingVehicle();

        // Assert: Parking spot should NOT be updated when ticket update fails
        verify(parkingSpotDAO, never()).updateParking(any());
    }

    /**
     * Test case: Get next available parking spot for CAR
     * Verifies that available parking spot is returned
     */
    @Test
    void testGetNextParkingNumberIfAvailable() {
        // Arrange: Setup mock responses for CAR parking
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR selection
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1); // Spot #1 available

        // Act: Get next available parking spot
        ParkingSpot spot = parkingService.getNextParkingNumberIfAvailable();

        // Assert: Verify spot properties
        assert spot != null; // Spot should not be null
        assert spot.getId() == 1; // Should be spot #1
        assert spot.isAvailable(); // Spot should be marked as available
    }

    /**
     * Test case: No parking spot available
     * Verifies that null is returned when no spots are available
     */
    @Test
    void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
        // Arrange: Setup mock responses - no spots available
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR selection
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0); // No spots available

        // Act: Get next available parking spot
        ParkingSpot spot = parkingService.getNextParkingNumberIfAvailable();

        // Assert: Spot should be null when no spots available
        assert spot == null;
    }

    /**
     * Test case: Invalid vehicle type selection
     * Verifies that null is returned for invalid input
     */
    @Test
    void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
        // Arrange: Invalid vehicle type selection
        when(inputReaderUtil.readSelection()).thenReturn(3); // Invalid selection (not 1 or 2)

        // Act: Get next available parking spot
        ParkingSpot spot = parkingService.getNextParkingNumberIfAvailable();

        // Assert: Spot should be null for invalid input
        assert spot == null;
    }

    /**
     * Test case: Get next available parking spot for BIKE
     * Verifies BIKE parking spot retrieval
     */
    @Test
    void getNextParkingNumberIfAvailable_Bike() {
        // Arrange: Setup mock responses for BIKE parking
        when(inputReaderUtil.readSelection()).thenReturn(2); // BIKE selection
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(2); // Spot #2 available

        // Act: Get next available parking spot and get its ID
        int result = parkingService.getNextParkingNumberIfAvailable().getId();

        // Assert: Should return spot #2
        assertEquals(2, result);
    }

    /**
     * Test case: Database exception handling in getNextParkingNumberIfAvailable
     * Verifies that method doesn't throw exception when database fails
     */
    @Test
    void getNextParkingNumberIfAvailable_shouldNotThrowExceptionOnError() {
        // Arrange: Setup mock to throw database exception
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR selection

        // Simulate database unavailable
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR))
                .thenThrow(new RuntimeException("Database unavailable"));

        // Act & Assert: Method should handle exception gracefully
        assertDoesNotThrow(() -> {
            ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();
            // Should return null when exception occurs
            assertNull(result);
        });
    }

    /**
     * Test case: Exception handling in processExitingVehicle
     * Verifies that method handles ticket not found exception gracefully
     */
    @Test
    void processExitingVehicle_shouldHandleExceptionGracefully() throws Exception {
        // Arrange: Setup mock to throw ticket not found exception
        String regNumber = "TEST123";
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumber);

        // Simulate ticket not found in database
        when(ticketDAO.getTicket(regNumber))
                .thenThrow(new RuntimeException("Ticket not found"));

        // Act & Assert: Method should not throw exception
        assertDoesNotThrow(() -> parkingService.processExitingVehicle());

        // Verify that getTicket was called
        verify(ticketDAO).getTicket(regNumber);

        // Verify that updateTicket was NOT called (exception occurred earlier)
        verify(ticketDAO, never()).updateTicket(any());
    }

    /**
     * Test case: Exception handling in processIncomingVehicle
     * Verifies that catch block executes when saveTicket fails
     */
    @Test
    void processIncomingVehicle_catchBlockShouldExecuteOnException() throws Exception {
        // Arrange: Setup mock to throw exception on saveTicket
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABC123");
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR selection
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1); // Spot available

        // Simulate database save failure
        RuntimeException expectedException = new RuntimeException("Test exception");
        when(ticketDAO.saveTicket(any(Ticket.class))).thenThrow(expectedException);

        // Act & Assert: Method should not throw exception (catch block should handle it)
        assertDoesNotThrow(() -> parkingService.processIncomingVehicle());

        // Verify that saveTicket was attempted
        verify(ticketDAO).saveTicket(any(Ticket.class));
    }
}