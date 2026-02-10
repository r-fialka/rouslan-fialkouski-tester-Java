package com.parkit.parkingsystem.integration.constants;

import com.parkit.parkingsystem.constants.ParkingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingTypeTest {

    @Test
    void parkingType_shouldContainExpectedValues() {
        // Arrange & Act
        ParkingType[] values = ParkingType.values();

        // Assert
        assertEquals(2, values.length);
        assertEquals(ParkingType.CAR, values[0]);
        assertEquals(ParkingType.BIKE, values[1]);
    }

    @Test
    void parkingType_valueOf_shouldWork() {
        // Act & Assert
        assertEquals(ParkingType.CAR, ParkingType.valueOf("CAR"));
        assertEquals(ParkingType.BIKE, ParkingType.valueOf("BIKE"));
    }
}

