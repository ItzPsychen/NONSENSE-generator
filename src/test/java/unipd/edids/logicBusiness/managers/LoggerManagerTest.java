package unipd.edids.logicBusiness.managers;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.managers.LoggerManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class LoggerManagerTest {

    /**
     * Tests for the LoggerManager class:
     * - Focus specifically on the getInstance() method and its functionality.
     * - Validate that only one instance of LoggerManager is created and always returned.
     */

    @BeforeAll
    static void setUpBeforeAll() {
        System.out.println("Starting LoggerManager Tests...");
    }

    @AfterAll
    static void tearDownAfterAll() {
        System.out.println("Completed LoggerManager Tests.");
    }

    @BeforeEach
    void setUp() {
        System.out.println("Starting a new test case.");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finished test case.");
    }

    @Test
    void testGetInstanceNotNull() {
        LoggerManager instance = LoggerManager.getInstance();
        assertNotNull(instance, "getInstance() returned null. An instance of LoggerManager should have been created.");
    }

    @Test
    void testGetInstanceSameInstance() {
        LoggerManager instance1 = LoggerManager.getInstance();
        LoggerManager instance2 = LoggerManager.getInstance();
        assertSame(instance1, instance2, "getInstance() returned different instances. It should always return the same instance.");
    }

    @Test
    void testGetLoggerValidClass() {
        LoggerManager instance = LoggerManager.getInstance();
        Logger logger = instance.getLogger(LoggerManagerTest.class);
        assertNotNull(logger, "getLogger() returned null. A Logger instance should have been created for the given class.");
    }

    @Test
    void testGetLoggerConsistencyForClass() {
        LoggerManager instance = LoggerManager.getInstance();
        Logger logger1 = instance.getLogger(LoggerManagerTest.class);
        Logger logger2 = instance.getLogger(LoggerManagerTest.class);
        assertSame(logger1, logger2, "getLogger() returned different Logger instances for the same class. Consistency is expected.");
    }
}