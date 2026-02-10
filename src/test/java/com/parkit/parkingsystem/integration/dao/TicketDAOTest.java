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

    /* =========================================================
       getNbTicket
       ========================================================= */

    @Test
    void getNbTicket_shouldReturnZero() {
        assertEquals(0, ticketDAO.getNbTicket("ABC123"));
    }

    /* =========================================================
       saveTicket
       ========================================================= */

    @Test
    void saveTicket_shouldReturnTrue_whenInsertSucceeds() throws Exception {
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

    @Test
    void saveTicket_shouldReturnFalse_whenExceptionOccurs() throws Exception {
        // Arrange
        when(connection.prepareStatement(anyString()))
                .thenThrow(new RuntimeException("DB error"));

        Ticket ticket = new Ticket();

        // Act
        boolean result = ticketDAO.saveTicket(ticket);

        // Assert
        assertFalse(result);
    }

    /* =========================================================
       getTicket
       ========================================================= */

    @Test
    void getTicket_shouldReturnTicket_whenFound() throws Exception {
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

    @Test
    void getTicket_shouldReturnNull_whenNoTicketFound() throws Exception {
        // Arrange
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        Ticket ticket = ticketDAO.getTicket("ABC123");

        // Assert
        assertNull(ticket);
    }

    @Test
    void getTicket_shouldReturnNull_whenExceptionOccurs() throws Exception {
        // Arrange
        when(connection.prepareStatement(anyString()))
                .thenThrow(new RuntimeException("DB error"));

        // Act
        Ticket ticket = ticketDAO.getTicket("ABC123");

        // Assert
        assertNull(ticket);
    }

    /* =========================================================
       updateTicket
       ========================================================= */

    @Test
    void updateTicket_shouldReturnTrue_whenUpdateSucceeds() throws Exception {
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

    @Test
    void updateTicket_shouldReturnFalse_whenExceptionOccurs() throws Exception {
        // Arrange
        when(connection.prepareStatement(anyString()))
                .thenThrow(new RuntimeException("DB error"));

        Ticket ticket = new Ticket();

        // Act
        boolean result = ticketDAO.updateTicket(ticket);

        // Assert
        assertFalse(result);
    }
}

