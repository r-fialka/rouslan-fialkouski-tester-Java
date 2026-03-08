package com.parkit.parkingsystem.integration.constants;

import com.parkit.parkingsystem.constants.ParkingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingTypeTest {

    /**
     * Verifies that the ParkingType enum contains exactly two values
     * and that they match the expected parking types.
     */
    @Test
    void values_shouldReturnCarAndBikeParkingTypes_whenEnumIsLoaded() {

        // Arrange & Act
        ParkingType[] values = ParkingType.values();

        // Assert
        assertEquals(2, values.length);
        assertEquals(ParkingType.CAR, values[0]);
        assertEquals(ParkingType.BIKE, values[1]);
    }

    /**
     * Verifies that valueOf correctly converts a string
     * to the corresponding ParkingType enum value.
     */
    @Test
    void valueOf_shouldReturnCorrectParkingType_whenValidStringIsProvided() {

        // Act & Assert
        assertEquals(ParkingType.CAR, ParkingType.valueOf("CAR"));
        assertEquals(ParkingType.BIKE, ParkingType.valueOf("BIKE"));
    }
}

