package com.parkit.parkingsystem.integration.util;

import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class InputReaderUtilTest {

    @Test
    void inputReaderUtil_canBeInstantiated() {
        // Act
        InputReaderUtil util = new InputReaderUtil();

        // Assert
        assertNotNull(util);
    }

}

