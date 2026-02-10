package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.service.ParkingService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class InteractiveShellTest {

    /**
     * Verifies that the menu can be displayed without throwing exceptions.
     * This covers the loadMenu() method.
     */
    @Test
    void loadMenu_shouldPrintMenuWithoutException() throws Exception {
        // Arrange
        Method loadMenu = InteractiveShell.class
                .getDeclaredMethod("loadMenu");
        loadMenu.setAccessible(true);

        // Act & Assert
        assertDoesNotThrow(() -> loadMenu.invoke(null));
    }

    /**
     * Verifies that the application can start and exit immediately
     * when the shutdown option (3) is selected.
     *
     * This avoids infinite loops and database access.
     */
    @Test
    void loadInterface_shouldExitImmediately_whenOptionIsThree() {
        // Arrange
        System.setIn(new ByteArrayInputStream("3\n".getBytes()));

        // Act & Assert
        assertDoesNotThrow(InteractiveShell::loadInterface);
    }
}

