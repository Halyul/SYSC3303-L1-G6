package project.scheduler.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import project.scheduler.src.*;

class ReceiverTest {
    private Receiver r;

    @BeforeEach
    public void setUp() {
        this.r = new Receiver(true);
    }

    @Test
    @DisplayName("parse should work")
    public void testParse() {
        String role = "test";
        int id = 0;
        String state = "test";
        long time = 1234567890;
        byte[] string = ("role:" + role + ";id:" + id + ";state:" + state + ";time:" + time + ";type:sendState;").getBytes();
        assertEquals(new String(string), new String(r.debug(string)), "The Receiver can parse a message");
    }

}