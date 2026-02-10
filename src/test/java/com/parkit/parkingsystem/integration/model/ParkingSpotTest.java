package com.parkit.parkingsystem.integration.model;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingSpotTest {

    @Test
    void constructor_and_getters_shouldWorkCorrectly() {
        // Arrange
        ParkingSpot spot = new ParkingSpot(1, ParkingType.CAR, true);

        // Act & Assert
        assertEquals(1, spot.getId());
        assertEquals(ParkingType.CAR, spot.getParkingType());
        assertTrue(spot.isAvailable());
    }

    @Test
    void setters_shouldUpdateValuesCorrectly() {
        // Arrange
        ParkingSpot spot = new ParkingSpot(1, ParkingType.CAR, true);

        // Act
        spot.setId(5);
        spot.setParkingType(ParkingType.BIKE);
        spot.setAvailable(false);

        // Assert
        assertEquals(5, spot.getId());
        assertEquals(ParkingType.BIKE, spot.getParkingType());
        assertFalse(spot.isAvailable());
    }

    @Test
    void equals_shouldReturnTrue_forSameObject() {
        // Arrange
        ParkingSpot spot = new ParkingSpot(1, ParkingType.CAR, true);

        // Act & Assert
        assertEquals(spot, spot);
    }

    @Test
    void equals_shouldReturnTrue_forSameId() {
        // Arrange
        ParkingSpot spot1 = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot spot2 = new ParkingSpot(1, ParkingType.BIKE, false);

        // Act & Assert
        assertEquals(spot1, spot2);
    }

    @Test
    void equals_shouldReturnFalse_forDifferentId() {
        // Arrange
        ParkingSpot spot1 = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot spot2 = new ParkingSpot(2, ParkingType.CAR, true);

        // Act & Assert
        assertNotEquals(spot1, spot2);
    }

    @Test
    void equals_shouldReturnFalse_forNullAndDifferentClass() {
        // Arrange
        ParkingSpot spot = new ParkingSpot(1, ParkingType.CAR, true);

        // Act & Assert
        assertNotEquals(spot, null);
        assertNotEquals(spot, "not a parking spot");
    }

    @Test
    void hashCode_shouldBeBasedOnId() {
        // Arrange
        ParkingSpot spot = new ParkingSpot(10, ParkingType.CAR, true);

        // Act & Assert
        assertEquals(10, spot.hashCode());
    }
}

