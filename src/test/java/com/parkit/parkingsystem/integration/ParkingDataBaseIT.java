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

    /**
     * Verifies that when a car enters the parking lot,
     * a new ticket is created and the parking spot becomes unavailable.
     */
    @Test
    void processIncomingVehicle_shouldCreateTicketAndOccupyParkingSpot_whenCarEntersParking() {

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

    /**
     * Verifies that when a vehicle exits the parking lot,
     * the system calculates the parking price, records the exit time,
     * and marks the parking spot as available again.
     */
    @Test
    void processExitingVehicle_shouldCalculateFareAndFreeParkingSpot_whenVehicleLeavesParking() {

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

    /**
     * Verifies that a recurring user receives a discount
     * when exiting the parking lot for the second time.
     */
    @Nested
    @Tag("ExitingVehicleTest")
    @DisplayName("Tests pour la sortie du véhicule")
    class ExitingVehicleTest {

        /**
         * Verifies that the first parking visit does not apply any discount.
         */
        @Test
        void processExitingVehicle_shouldCalculateNormalPrice_whenFirstParking() {

            // Arrange
            ParkingService parkingService =
                    new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

            Date oneHourAgo = new Date(System.currentTimeMillis() - 60 * 60 * 1000);

            parkingService.processIncomingVehicle();

            dataBasePrepareService.updateInTimeForVehicle("ABCDEF", oneHourAgo);

            // Act
            parkingService.processExitingVehicle();

            // Assert
            Ticket ticket = ticketDAO.getTicket("ABCDEF");

            assertNotNull(ticket.getOutTime(), "Out time should be recorded");
            assertTrue(ticket.getPrice() > 0, "First parking should have a normal price");
        }

        /**
         * Verifies that the second parking visit applies a discount
         * for recurring users.
         */
        @Test
        void processExitingVehicle_shouldApplyDiscount_whenSecondParking() {

            // Arrange
            ParkingService parkingService =
                    new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

            Date oneHourAgo = new Date(System.currentTimeMillis() - 60 * 60 * 1000);

            // First parking
            parkingService.processIncomingVehicle();
            dataBasePrepareService.updateInTimeForVehicle("ABCDEF", oneHourAgo);
            parkingService.processExitingVehicle();

            Ticket firstTicket = ticketDAO.getTicket("ABCDEF");
            double firstPrice = firstTicket.getPrice();

            // Second parking
            parkingService.processIncomingVehicle();
            dataBasePrepareService.updateInTimeForVehicle("ABCDEF", oneHourAgo);

            // Act
            parkingService.processExitingVehicle();

            // Assert
            Ticket secondTicket = ticketDAO.getTicket("ABCDEF");
            double secondPrice = secondTicket.getPrice();

            assertTrue(
                    secondPrice < firstPrice,
                    "Recurring user should receive a discount"
            );
        }
    }
}
