package test.unit.auctionsniper.xmpp;

import auctionsniper.xmpp.LoggingXMPPFailureReporter;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.jmock.imposters.ByteBuddyClassImposteriser;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggingXMPPFailureReporterTest {
    @RegisterExtension
    private final JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};
    final Logger logger = context.mock(Logger.class);
    final LoggingXMPPFailureReporter reporter = new LoggingXMPPFailureReporter(logger);

    @AfterAll
    static void resetLogging() {
        LogManager.getLogManager().reset();
    }

    @Test
    void writesMessageTranslationFailureToLog() {
        context.checking(new Expectations() {{
            oneOf(logger).severe("<auction id> Could not translate message \"bad message\" because \"java.lang.Exception: bad\"");
        }});
        reporter.cannotTranslateMessage("auction id", "bad message", new Exception("bad"));
    }
}
