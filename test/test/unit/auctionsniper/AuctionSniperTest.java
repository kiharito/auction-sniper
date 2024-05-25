package test.unit.auctionsniper;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.Auction;

import static auctionsniper.AuctionEventListener.PriceSource;

import auctionsniper.SniperSnapshot;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class AuctionSniperTest {
    private final String ITEM_ID = "item";

    @RegisterExtension
    private final JUnit5Mockery context = new JUnit5Mockery();
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(ITEM_ID, auction, sniperListener);
    private final States sniperState = context.states("sniper");

    @Test
    void reportsLostWhenAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            oneOf(sniperListener).sniperLost(with(any(SniperSnapshot.class)));
        }});
        sniper.auctionClosed();
    }

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperBidding(with(any(SniperSnapshot.class)));
            then(sniperState.is("bidding"));

            atLeast(1).of(sniperListener).sniperLost(with(any(SniperSnapshot.class)));
            when(sniperState.is("bidding"));
        }});
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperWinning(with(any(SniperSnapshot.class)));
            then(sniperState.is("winning"));

            atLeast(1).of(sniperListener).sniperWon(with(any(SniperSnapshot.class)));
            when(sniperState.is("winning"));
        }});
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        context.checking(new Expectations() {{
            oneOf(auction).bid(price + increment);
            atLeast(1).of(sniperListener).sniperBidding(new SniperSnapshot(ITEM_ID, price, bid));
        }});
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperWinning(with(any(SniperSnapshot.class)));
        }});
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
    }
}
