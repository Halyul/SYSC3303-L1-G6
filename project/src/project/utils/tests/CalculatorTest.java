package project.utils.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import project.utils.Calculator;

import java.util.ArrayList;

public class CalculatorTest {
    private Calculator c;
    private ArrayList<Long> lst = new ArrayList<Long>();

    @BeforeEach
    public void setUp() throws Exception {
        this.c = new Calculator();
        for (int i = 0; i < 10; i++) {
            this.lst.add((long) (i+1));
        }
    }

    @Test
    @DisplayName("calculate sum should work")
    public void testSum() {
        assertEquals(55, c.sum(this.lst), "sum is not working");
    }

    @Test
    @DisplayName("calculate mean should work")
    public void testMean() {
        assertEquals((long)5.5, c.mean(this.lst), "mean is not working");
    }

    @Test
    @DisplayName("calculate standard deviation should work")
    public void testStdDev() {
        assertTrue(((c.stdDev(this.lst) - 2.872281323269)/2.872281323269) < 0.001, "stdDev is not working");
    }

    @Test
    @DisplayName("calculate confidence interval should work")
    public void testConfidenceInterval() {
        assertTrue(((c.confidenceInterval(this.lst, 80) - 1.164)/1.164) < 0.001, "confidenceInterval is not working");
        assertTrue(((c.confidenceInterval(this.lst, 85) - 1.308)/1.308) < 0.001, "confidenceInterval is not working");
        assertTrue(((c.confidenceInterval(this.lst, 90) - 1.494)/1.494) < 0.001, "confidenceInterval is not working");
        assertTrue(((c.confidenceInterval(this.lst, 95) - 1.78)/1.78) < 0.001, "confidenceInterval is not working");
        assertTrue(((c.confidenceInterval(this.lst, 99) - 2.34)/2.34) < 0.001, "confidenceInterval is not working");
        assertTrue(((c.confidenceInterval(this.lst, 99.5) - 2.55)/2.55) < 0.001, "confidenceInterval is not working");
        assertTrue(((c.confidenceInterval(this.lst, 99.9) - 2.989)/2.989) < 0.001, "confidenceInterval is not working");
    }
}
