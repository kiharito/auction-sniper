package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.Main;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuctionHouse implements AuctionHouse {
    private final XMPPConnection connection;

    public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, Main.AUCTION_RESOURCE);
        return new XMPPAuctionHouse(connection);
    }

    public XMPPAuctionHouse(XMPPConnection connection) {
        this.connection = connection;
    }

    public void disconnect() {
        connection.disconnect();
    }

    @Override
    public Auction auctionFor(String itemId) {
        return new XMPPAuction(connection, itemId);
    }
}
