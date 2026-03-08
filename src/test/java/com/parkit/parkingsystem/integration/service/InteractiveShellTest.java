package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class InteractiveShellTest {

    /**
     * Verifies that the menu can be displayed successfully
     * without throwing any exceptions when the loadMenu method is executed.
     */
    @Test
    void loadMenu_shouldDisplayMenuWithoutThrowingException() throws Exception {

        // Arrange
        Method loadMenu = InteractiveShell.class
                .getDeclaredMethod("loadMenu");
        loadMenu.setAccessible(true);

        // Act & Assert
        assertDoesNotThrow(() -> loadMenu.invoke(null));
    }

    /**
     * Verifies that the application exits immediately when the user selects
     * the shutdown option (3) in the console menu.
     * This test prevents the interactive loop from running indefinitely.
     */
    @Test
    void loadInterface_shouldExitApplication_whenShutdownOptionIsSelected() {

        // Arrange
        System.setIn(new ByteArrayInputStream("3\n".getBytes()));

        // Act & Assert
        assertDoesNotThrow(InteractiveShell::loadInterface);
    }
}

