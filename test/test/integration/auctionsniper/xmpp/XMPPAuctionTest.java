package test.integration.auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.xmpp.XMPPAuction;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.endtoend.auctionsniper.ApplicationRunner;
import test.endtoend.auctionsniper.FakeAuctionServer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static test.endtoend.auctionsniper.FakeAuctionServer.*;
import static test.endtoend.auctionsniper.ApplicationRunner.*;

public class XMPPAuctionTest {
    private final FakeAuctionServer auctionServer = new FakeAuctionServer("item54321");
    private final XMPPConnection connection = new XMPPConnection(XMPP_HOSTNAME);

    @BeforeEach
    void startSellingItem() throws XMPPException {
        auctionServer.startSellingItem();
    }

    @BeforeEach
    void prepareSniperConnection() throws XMPPException {
        connection.connect();
        connection.login(SNIPER_ID, SNIPER_PASSWORD, AUCTION_RESOURCE);
    }

    @Test
    void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        Auction auction = new XMPPAuction(connection, auctionServer.getItemId());
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

        auction.join();
        auctionServer.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
        auctionServer.announceClosed();

        assertTrue(auctionWasClosed.await(2, TimeUnit.SECONDS), "should have been closed");
    }

    private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            @Override
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource priceSource) {

            }
        };
    }

    @AfterEach
    void stopAuction() {
        auctionServer.stop();
    }

    @AfterEach
    void stopSniperConnection() {
        connection.disconnect();
    }
}
