package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private InputReaderUtil inputReaderUtil;

    @BeforeAll
    static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;

        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;

        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    void testParkingACar() {
        // Arrange
        ParkingService parkingService =
                new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        // Act
        parkingService.processIncomingVehicle();
        // Assert
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        assertNotNull(ticket, "Ticket should be saved");
        assertNull(ticket.getOutTime(), "Out time should be null");
        assertFalse(ticket.getParkingSpot().isAvailable(),
                "Parking spot should be unavailable");
    }

    @Test
    void testParkingLotExit() {
        // Arrange
        ParkingService parkingService =
                new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle();

        dataBasePrepareService.updateInTimeForVehicle(
                "ABCDEF",
                new Date(System.currentTimeMillis() - 60 * 60 * 1000)
        );
        // Act
        parkingService.processExitingVehicle();
        // Assert
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        assertNotNull(ticket.getOutTime(), "Out time should be set");
        assertTrue(ticket.getPrice() > 0, "Price should be greater than zero");

        int nextAvailableSlot =
                parkingSpotDAO.getNextAvailableSlot(ticket.getParkingSpot().getParkingType());

        assertEquals(
                ticket.getParkingSpot().getId(),
                nextAvailableSlot,
                "Parking spot should be available again in database"
        );
    }


    @Test
    void testParkingLotExitRecurringUser() {
        // Arrange
        ParkingService parkingService =
                new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Act - FIRST PARKING
        parkingService.processIncomingVehicle();

        dataBasePrepareService.updateInTimeForVehicle(
                "ABCDEF",
                new Date(System.currentTimeMillis() - 60 * 60 * 1000)
        );

        parkingService.processExitingVehicle();
        // Assert - FIRST PARKING
        Ticket firstTicket = ticketDAO.getTicket("ABCDEF");
        double firstPrice = firstTicket.getPrice();

        assertTrue(firstPrice > 0, "First price should be greater than zero");

        // Act - SECOND PARKING (RECURRING USER)
        parkingService.processIncomingVehicle();

        dataBasePrepareService.updateInTimeForVehicle(
                "ABCDEF",
                new Date(System.currentTimeMillis() - 60 * 60 * 1000)
        );

        parkingService.processExitingVehicle();
        // Assert - SECOND PARKING (DISCOUNT)
        Ticket secondTicket = ticketDAO.getTicket("ABCDEF");
        double secondPrice = secondTicket.getPrice();

        assertTrue(
                secondPrice < firstPrice,
                "Recurring user should get 5% discount"
        );
    }
}
