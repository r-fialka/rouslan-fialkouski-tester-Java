package com.parkit.parkingsystem.integration.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParkingSpotDAOTest {

    private ParkingSpotDAO parkingSpotDAO;

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
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseConfig;

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    /**
     * Verifies that the DAO returns the correct parking slot number
     * when an available parking slot exists in the database.
     */
    @Test
    void getNextAvailableSlot_shouldReturnSlotNumber_whenSlotExists() throws Exception {

        // Arrange
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(3);

        // Act
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // Assert
        assertEquals(3, result);
    }

    /**
     * Verifies that the DAO returns the correct parking slot number
     * when an available parking slot exists in the database.
     */
    @Test
    void getNextAvailableSlot_shouldReturnMinusOne_whenNoSlotFound() throws Exception {

        // Arrange
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);

        // Assert
        assertEquals(-1, result);
    }

    /**
     * Verifies that the DAO returns -1 when no available parking slot
     * is found in the database.
     */
    @Test
    void getNextAvailableSlot_shouldReturnMinusOne_whenExceptionOccurs() throws Exception {

        // Arrange
        when(connection.prepareStatement(anyString()))
                .thenThrow(new RuntimeException("DB error"));

        // Act
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // Assert
        assertEquals(-1, result);
    }

    /**
     * Verifies that the DAO safely returns -1 when a database exception occurs
     * while searching for an available parking slot.
     */
    @Test
    void updateParking_shouldReturnTrue_whenUpdateCountIsOne() throws Exception {

        // Arrange
        ParkingSpot spot = new ParkingSpot(1, ParkingType.CAR, true);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        boolean result = parkingSpotDAO.updateParking(spot);

        // Assert
        assertTrue(result);
    }

    /**
     * Verifies that the updateParking method returns true
     * when the database successfully updates one parking spot.
     */
    @Test
    void updateParking_shouldReturnFalse_whenUpdateCountIsZero() throws Exception {

        // Arrange
        ParkingSpot spot = new ParkingSpot(1, ParkingType.CAR, false);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act
        boolean result = parkingSpotDAO.updateParking(spot);

        // Assert
        assertFalse(result);
    }

    /**
     * Verifies that the updateParking method returns false
     * when no rows are updated in the database.
     */
    @Test
    void updateParking_shouldReturnFalse_whenExceptionOccurs() throws Exception {

        // Arrange
        ParkingSpot spot = new ParkingSpot(1, ParkingType.CAR, true);
        when(connection.prepareStatement(anyString()))
                .thenThrow(new RuntimeException("DB error"));

        // Act
        boolean result = parkingSpotDAO.updateParking(spot);

        // Assert
        assertFalse(result);
    }
}

