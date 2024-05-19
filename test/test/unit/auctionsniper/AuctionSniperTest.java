package test.unit.auctionsniper;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.Auction;

import static auctionsniper.AuctionEventListener.PriceSource;

import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class AuctionSniperTest {
    @RegisterExtension
    private final JUnit5Mockery context = new JUnit5Mockery();
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);
    private final States sniperState = context.states("sniper");

    @Test
    void reportsLostWhenAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            oneOf(sniperListener).sniperLost();
        }});
        sniper.auctionClosed();
    }

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperBidding();
            then(sniperState.is("bidding"));

            atLeast(1).of(sniperListener).sniperLost();
            when(sniperState.is("bidding"));
        }});
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        context.checking(new Expectations() {{
            oneOf(auction).bid(price + increment);
            atLeast(1).of(sniperListener).sniperBidding();
        }});
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperWinning();
        }});
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
    }
}
