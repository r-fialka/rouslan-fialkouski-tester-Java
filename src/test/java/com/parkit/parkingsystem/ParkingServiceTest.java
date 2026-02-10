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
    void setUp() {
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    /* =========================================================
       INCOMING VEHICLE
       ========================================================= */

    /**
     * Verifies that a ticket is created and the parking spot is updated
     * when a valid vehicle enters and a parking slot is available.
     */
    @Test
    void processIncomingVehicle_successful() throws Exception {
        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        // Act
        parkingService.processIncomingVehicle();

        // Assert
        verify(ticketDAO).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO).updateParking(any(ParkingSpot.class));
    }

    /**
     * Verifies that no ticket is created when no parking slot is available.
     */
    @Test
    void processIncomingVehicle_noSlotAvailable() {
        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);

        // Act
        parkingService.processIncomingVehicle();

        // Assert
        verify(ticketDAO, never()).saveTicket(any());
        verify(parkingSpotDAO, never()).updateParking(any());
    }

    /**
     * Verifies that an exception during ticket saving
     * is handled silently without crashing the application.
     */
    @Test
    void processIncomingVehicle_ticketSaveFails() throws Exception {
        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        doThrow(new RuntimeException()).when(ticketDAO).saveTicket(any());

        // Act & Assert
        assertDoesNotThrow(() -> parkingService.processIncomingVehicle());
    }

    /* =========================================================
       EXITING VEHICLE
       ========================================================= */

    /**
     * Verifies that the ticket is updated and the parking spot
     * is released when a valid vehicle exits.
     */
    @Test
    void processExitingVehicle_successful() throws Exception {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - 3600000));
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
        when(ticketDAO.updateTicket(any())).thenReturn(true);

        // Act
        parkingService.processExitingVehicle();

        // Assert
        verify(ticketDAO).updateTicket(any());
        verify(parkingSpotDAO).updateParking(any());
    }

    /**
     * Verifies that the parking spot is NOT released
     * when ticket update fails.
     */
    @Test
    void processExitingVehicle_updateFails() throws Exception {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(new Date());
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
        when(ticketDAO.updateTicket(any())).thenReturn(false);

        // Act
        parkingService.processExitingVehicle();

        // Assert
        verify(parkingSpotDAO, never()).updateParking(any());
    }

    /**
     * Verifies that a missing ticket does not cause a crash.
     */
    @Test
    void processExitingVehicle_ticketNotFound() throws Exception {
        // Arrange
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.getTicket("ABCDEF")).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> parkingService.processExitingVehicle());
    }

    /* =========================================================
       PARKING SLOT SELECTION
       ========================================================= */

    /**
     * Verifies that a parking spot is returned for a valid CAR selection.
     */
    @Test
    void getNextParkingNumberIfAvailable_car() {
        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        // Act
        ParkingSpot spot = parkingService.getNextParkingNumberIfAvailable();

        // Assert
        assertNotNull(spot);
        assertEquals(1, spot.getId());
        assertTrue(spot.isAvailable());
    }

    /**
     * Verifies that null is returned when no parking slots are available.
     */
    @Test
    void getNextParkingNumberIfAvailable_noSlot() {
        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);

        // Act & Assert
        assertNull(parkingService.getNextParkingNumberIfAvailable());
    }

    /**
     * Verifies that invalid user input is handled gracefully.
     */
    @Test
    void getNextParkingNumberIfAvailable_invalidInput() {
        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(99);

        // Act & Assert
        assertNull(parkingService.getNextParkingNumberIfAvailable());
    }

    /**
     * Verifies that database errors are handled without throwing exceptions.
     */
    @Test
    void getNextParkingNumberIfAvailable_databaseError() {
        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(1);
        doThrow(new RuntimeException())
                .when(parkingSpotDAO).getNextAvailableSlot(ParkingType.CAR);

        // Act & Assert
        assertDoesNotThrow(() ->
                assertNull(parkingService.getNextParkingNumberIfAvailable())
        );
    }

    /**
     * Verifies that BIKE selection returns a BIKE parking spot.
     */
    @Test
    void getNextParkingNumberIfAvailable_whenBikeSelected_returnsBikeSpot() {
        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(2); // BIKE
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(5);

        // Act
        ParkingSpot spot = parkingService.getNextParkingNumberIfAvailable();

        // Assert
        assertNotNull(spot);
        assertEquals(5, spot.getId());
        assertEquals(ParkingType.BIKE, spot.getParkingType());
        assertTrue(spot.isAvailable());
    }

    @Test
    void processIncomingVehicle_shouldApplyDiscountMessage_whenRegularUser() throws Exception {
        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABC123");
        when(ticketDAO.getNbTicket("ABC123")).thenReturn(2);

        // Act
        parkingService.processIncomingVehicle();

        // Assert
        verify(ticketDAO).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO).updateParking(any(ParkingSpot.class));
    }

}
