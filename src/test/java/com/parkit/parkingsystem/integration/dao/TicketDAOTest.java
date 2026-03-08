package com.parkit.parkingsystem.integration.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketDAOTest {

    private TicketDAO ticketDAO;

    @Mock
    private DataBaseConfig dataBaseConfig;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseConfig;

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    /**
     * Verifies that getNbTicket returns 0 when no tickets
     * exist for the specified vehicle registration number.
     */
    @Test
    void getNbTicket_shouldReturnZero_whenVehicleHasNoPreviousTickets() {

        // Arrange
        String vehicleRegNumber = "ABC123";

        // Act
        int result = ticketDAO.getNbTicket(vehicleRegNumber);

        // Assert
        assertEquals(0, result);
    }


    /**
     * Verifies that saveTicket returns true when the ticket
     * is successfully inserted into the database.
     */
    @Test
    void saveTicket_shouldReturnTrue_whenTicketIsSavedSuccessfully() throws Exception {

        // Arrange
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setVehicleRegNumber("ABC123");
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setPrice(5.0);
        ticket.setInTime(new Date());
        ticket.setOutTime(null);

        // Act
        boolean result = ticketDAO.saveTicket(ticket);

        // Assert
        assertTrue(result);
    }

    /**
     * Verifies that saveTicket returns false when a database
     * exception occurs during ticket insertion.
     */
    @Test
    void saveTicket_shouldReturnFalse_whenDatabaseExceptionOccurs() throws Exception {

        // Arrange
        when(connection.prepareStatement(anyString()))
                .thenThrow(new RuntimeException("DB error"));

        Ticket ticket = new Ticket();

        // Act
        boolean result = ticketDAO.saveTicket(ticket);

        // Assert
        assertFalse(result);
    }

    /**
     * Verifies that getTicket returns a Ticket object when
     * a matching ticket is found in the database.
     */
    @Test
    void getTicket_shouldReturnTicket_whenTicketExists() throws Exception {

        // Arrange
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        when(resultSet.getInt(1)).thenReturn(1);
        when(resultSet.getInt(2)).thenReturn(10);
        when(resultSet.getDouble(3)).thenReturn(3.5);
        when(resultSet.getTimestamp(4)).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getTimestamp(5)).thenReturn(null);
        when(resultSet.getString(6)).thenReturn("CAR");

        // Act
        Ticket ticket = ticketDAO.getTicket("ABC123");

        // Assert
        assertNotNull(ticket);
        assertEquals("ABC123", ticket.getVehicleRegNumber());
        assertEquals(ParkingType.CAR, ticket.getParkingSpot().getParkingType());
    }

    /**
     * Verifies that getTicket returns null when no ticket
     * is found for the provided vehicle registration number.
     */
    @Test
    void getTicket_shouldReturnNull_whenTicketDoesNotExist() throws Exception {

        // Arrange
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        Ticket ticket = ticketDAO.getTicket("ABC123");

        // Assert
        assertNull(ticket);
    }

    /**
     * Verifies that getTicket returns null when a database
     * exception occurs during ticket retrieval.
     */
    @Test
    void getTicket_shouldReturnNull_whenDatabaseExceptionOccurs() throws Exception {
        // Arrange
        when(connection.prepareStatement(anyString()))
                .thenThrow(new RuntimeException("DB error"));

        // Act
        Ticket ticket = ticketDAO.getTicket("ABC123");

        // Assert
        assertNull(ticket);
    }

    /**
     * Verifies that updateTicket returns true when the ticket
     * is successfully updated in the database.
     */
    @Test
    void updateTicket_shouldReturnTrue_whenTicketIsUpdatedSuccessfully() throws Exception {

        // Arrange
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setPrice(4.0);
        ticket.setOutTime(new Date());

        // Act
        boolean result = ticketDAO.updateTicket(ticket);

        // Assert
        assertTrue(result);
    }

    /**
     * Verifies that updateTicket returns false when a database
     * exception occurs during the update operation.
     */
    @Test
    void updateTicket_shouldReturnFalse_whenDatabaseExceptionOccurs() throws Exception {
        // Arrange
        when(connection.prepareStatement(anyString()))
                .thenThrow(new RuntimeException("DB error"));

        Ticket ticket = new Ticket();

        // Act
        boolean result = ticketDAO.updateTicket(ticket);

        // Assert
        assertFalse(result);
    }

    /**
     * Verifies that saveTicket returns false when a database exception occurs.
     */
    @Test
    void saveTicket_shouldReturnFalse_whenExceptionOccurs() throws Exception {

        // Arrange
        TicketDAO ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseConfig;

        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABC123");
        ticket.setPrice(0);
        ticket.setInTime(new Date());
        ticket.setOutTime(null);

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString()))
                .thenThrow(new RuntimeException("DB error"));

        // Act
        boolean result = ticketDAO.saveTicket(ticket);

        // Assert
        assertFalse(result);
    }

    /**
     * Verifies that updateTicket returns false when a database exception occurs.
     */
    @Test
    void updateTicket_shouldReturnFalse_whenExceptionOccurs() throws Exception {

        // Arrange
        TicketDAO ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseConfig;

        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setPrice(5.0);
        ticket.setOutTime(new Date());

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString()))
                .thenThrow(new RuntimeException("DB error"));

        // Act
        boolean result = ticketDAO.updateTicket(ticket);

        // Assert
        assertFalse(result);
    }
}

