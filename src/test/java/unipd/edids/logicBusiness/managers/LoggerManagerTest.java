package unipd.edids.logicBusiness.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoggerManagerTest {

    private static final Logger logger = LogManager.getLogger(LoggerManagerTest.class);
    private int testNumber = 0;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: LoggerManagerTest");
    }

    @BeforeEach
    void setUp() {
        logger.info("Running test #{}", ++testNumber);
    }



    @Test
    void testGetInstanceNotNull() {
        logger.info("Testing getInstance() returns non-null...");
        LoggerManager instance = LoggerManager.getInstance();
        assertNotNull(instance, "getInstance() should return a non-null instance of LoggerManager");
    }

    @Test
    void testGetInstanceSingleton() {
        logger.info("Testing getInstance() returns the same instance...");
        LoggerManager instance1 = LoggerManager.getInstance();
        LoggerManager instance2 = LoggerManager.getInstance();
        assertSame(instance1, instance2, "getInstance() should return the same LoggerManager instance");
    }

    @Test
    void testGetLoggerValidClass() {
        logger.info("Testing getLogger() with a valid class...");
        LoggerManager instance = LoggerManager.getInstance();
        Logger testLogger = instance.getLogger(LoggerManagerTest.class);
        assertNotNull(testLogger, "getLogger() should return a valid Logger instance for the provided class");
    }

    @Test
    void testGetLoggerConsistency() {
        logger.info("Testing consistency of getLogger()...");
        LoggerManager instance = LoggerManager.getInstance();
        Logger logger1 = instance.getLogger(LoggerManagerTest.class);
        Logger logger2 = instance.getLogger(LoggerManagerTest.class);
        assertSame(logger1, logger2, "getLogger() should consistently return the same Logger instance for the same class");
    }

    @AfterEach
    void tearDown() {
        logger.info("Finished test #{}", testNumber);
    }

    @AfterAll
    void cleanUp() {
        logger.info("Finished test suite: LoggerManagerTest");
    }
}