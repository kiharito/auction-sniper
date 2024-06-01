package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import org.apache.commons.io.FilenameUtils;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class XMPPAuctionHouse implements AuctionHouse {
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction%s";
    public static final String ACCOUNT_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    public static final String LOG_FILE_NAME = "log/auction-sniper.log";
    private static final String LOGGER_NAME = "auction-sniper";
    private final XMPPConnection connection;
    private final XMPPFailureReporter failureReporter;

    public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException, XMPPPAuctionException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return new XMPPAuctionHouse(connection);
    }

    public XMPPAuctionHouse(XMPPConnection connection) throws XMPPPAuctionException {
        this.connection = connection;
        this.failureReporter = new LoggingXMPPFailureReporter(makeLogger());
    }

    public void disconnect() {
        connection.disconnect();
    }

    @Override
    public Auction auctionFor(String itemId) {
        return new XMPPAuction(connection, auctionId(itemId, connection), failureReporter);
    }

    private String auctionId(String itemId, XMPPConnection connection) {
        return String.format(ACCOUNT_ID_FORMAT, itemId, connection.getServiceName());
    }

    private Logger makeLogger() throws XMPPPAuctionException {
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.addHandler(simpleFileHandler());
        return logger;
    }

    private FileHandler simpleFileHandler() throws XMPPPAuctionException {
        try {
            FileHandler handler = new FileHandler(LOG_FILE_NAME);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        } catch (Exception e) {
            throw new XMPPPAuctionException("Could not create logger FileHandler " + FilenameUtils.getFullPath(LOG_FILE_NAME), e);
        }
    }

    public static class XMPPPAuctionException extends Exception {
        public XMPPPAuctionException(String message, Exception cause) {
            super(message, cause);
        }
    }
}
