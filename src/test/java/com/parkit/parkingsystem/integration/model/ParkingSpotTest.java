package com.parkit.parkingsystem.integration.model;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingSpotTest {

    /**
     * Verifies that the constructor correctly initializes
     * the parking spot fields and that the getter methods
     * return the expected values.
     */
    @Test
    void constructor_shouldInitializeFieldsCorrectly_whenParkingSpotIsCreated() {

        // Arrange
        ParkingSpot spot = new ParkingSpot(1, ParkingType.CAR, true);

        // Act & Assert
        assertEquals(1, spot.getId());
        assertEquals(ParkingType.CAR, spot.getParkingType());
        assertTrue(spot.isAvailable());
    }

    /**
     * Verifies that the setter methods correctly update
     * the parking spot properties.
     */
    @Test
    void setters_shouldUpdateParkingSpotFields_whenNewValuesAreProvided() {

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

    /**
     * Verifies that equals returns true when comparing
     * the same ParkingSpot object instance.
     */
    @Test
    void equals_shouldReturnTrue_whenComparingSameObjectInstance() {

        // Arrange
        ParkingSpot spot = new ParkingSpot(1, ParkingType.CAR, true);

        // Act & Assert
        assertEquals(spot, spot);
    }

    /**
     * Verifies that two ParkingSpot objects are considered equal
     * when they have the same parking spot ID.
     */
    @Test
    void equals_shouldReturnTrue_whenParkingSpotIdsAreEqual() {

        // Arrange
        ParkingSpot spot1 = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot spot2 = new ParkingSpot(1, ParkingType.BIKE, false);

        // Act & Assert
        assertEquals(spot1, spot2);
    }

    /**
     * Verifies that two ParkingSpot objects are not equal
     * when their parking spot IDs are different.
     */
    @Test
    void equals_shouldReturnFalse_whenParkingSpotIdsAreDifferent() {

        // Arrange
        ParkingSpot spot1 = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot spot2 = new ParkingSpot(2, ParkingType.CAR, true);

        // Act & Assert
        assertNotEquals(spot1, spot2);
    }

    /**
     * Verifies that equals returns false when comparing
     * a ParkingSpot with null or an object of another type.
     */
    @Test
    void equals_shouldReturnFalse_whenComparingWithNullOrDifferentClass() {

        // Arrange
        ParkingSpot spot = new ParkingSpot(1, ParkingType.CAR, true);

        // Act & Assert
        assertNotEquals(spot, null);
        assertNotEquals(spot, "not a parking spot");
    }

    /**
     * Verifies that the hashCode implementation
     * is based on the parking spot ID.
     */
    @Test
    void hashCode_shouldReturnParkingSpotId_whenHashCodeIsCalculated() {

        // Arrange
        ParkingSpot spot = new ParkingSpot(10, ParkingType.CAR, true);

        // Act & Assert
        assertEquals(10, spot.hashCode());
    }
}

