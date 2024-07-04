package com.parasoft.report.transformer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class XMLToSarifTest {
    @Test
    public void testCall() {
        XMLToSarif classUnderTest = new XMLToSarif();
        assertNotNull(classUnderTest.call());
    }
}
