package auctionsniper.xmpp;

import java.util.logging.Logger;

public class LoggingXMPPFailureReporter implements XMPPFailureReporter {
    public final Logger logger;

    public LoggingXMPPFailureReporter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void cannotTranslateMessage(String auctionId, String failureMessage, Exception exception) {
        logger.severe("<" + auctionId + "> Could not translate message \"" + failureMessage + "\" because \"" + exception + "\"");
    }
}
