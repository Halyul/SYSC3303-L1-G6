package project.utils.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import project.utils.Calculator;

import java.util.ArrayList;

public class CalculatorTest {
    private Calculator c;
    private ArrayList<Double> lst = new ArrayList<Double>();

    @BeforeEach
    public void setUp() throws Exception {
        this.c = new Calculator();
        for (int i = 0; i < 10; i++) {
            this.lst.add((double) (i+1));
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
        assertEquals(5.5, c.mean(this.lst), "mean is not working");
    }

    @Test
    @DisplayName("calculate standard deviation should work")
    public void testStdDev() {
        assertTrue(((c.stdDev(this.lst) - 3.028)/3.028) < 0.001, "stdDev is not working");
    }

    @Test
    @DisplayName("calculate confidence interval should work")
    public void testConfidenceInterval() {
        assertTrue(((c.confidenceInterval(this.lst, 80) - 1.227)/1.227) < 0.001, "confidenceInterval is not working");
        assertTrue(((c.confidenceInterval(this.lst, 85) - 1.379)/1.379) < 0.001, "confidenceInterval is not working");
        assertTrue(((c.confidenceInterval(this.lst, 90) - 1.575)/1.575) < 0.001, "confidenceInterval is not working");
        assertTrue(((c.confidenceInterval(this.lst, 95) - 1.877)/1.877) < 0.001, "confidenceInterval is not working");
        assertTrue(((c.confidenceInterval(this.lst, 99) - 2.466)/2.466) < 0.001, "confidenceInterval is not working");
        assertTrue(((c.confidenceInterval(this.lst, 99.5) - 2.687)/2.687) < 0.001, "confidenceInterval is not working");
        assertTrue(((c.confidenceInterval(this.lst, 99.9) - 3.151)/3.151) < 0.001, "confidenceInterval is not working");
    }
}
