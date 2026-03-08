package com.parkit.parkingsystem.integration.util;

import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputReaderUtilTest {

    /**
     * Verifies that the InputReaderUtil class can be instantiated
     * successfully and that the created object is not null.
     */
    @Test
    void constructor_shouldCreateInputReaderUtilInstance_whenClassIsInstantiated() {

        // Arrange
        // (No specific setup required)

        // Act
        InputReaderUtil util = new InputReaderUtil();

        // Assert
        assertNotNull(util);
    }

}

