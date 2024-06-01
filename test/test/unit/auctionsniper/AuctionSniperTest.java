package test.unit.auctionsniper;

import auctionsniper.*;

import static auctionsniper.AuctionEventListener.PriceSource;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.CoreMatchers.*;

public class AuctionSniperTest {
    private final String ITEM_ID = "item";

    @RegisterExtension
    private final JUnit5Mockery context = new JUnit5Mockery();
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final Item item = new Item(ITEM_ID, 2000);
    private final AuctionSniper sniper = new AuctionSniper(item, auction);
    private final States sniperState = context.states("sniper");

    @BeforeEach
    void prepareAuctionSniper() {
        sniper.addSniperListener(sniperListener);
    }

    @Test
    void reportsLostWhenAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            oneOf(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
        }});
        sniper.auctionClosed();
    }

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        allowingSniperBidding();
        context.checking(new Expectations() {{
            ignoring(auction);

            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
            when(sniperState.is("bidding"));
        }});
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WINNING)));
            then(sniperState.is("winning"));

            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WON)));
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
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
        }});
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        allowingSniperBidding();
        context.checking(new Expectations() {{
            ignoring(auction);

            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
            when(sniperState.is("bidding"));
        }});
        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, PriceSource.FromSniper);
    }

    @Test
    void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        allowingSniperBidding();
        context.checking(new Expectations() {{
            int bid = 123 + 45;
            allowing(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 2345, bid, SniperState.LOSING)
            );
            when(sniperState.is("bidding"));
        }});
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    }

    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<>(equalTo(state), "sniper that is ", "was") {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.sniperState;
            }
        };
    }

    private void allowingSniperBidding() {
        context.checking(new Expectations() {{
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
            then(sniperState.is("bidding"));
        }});
    }
}
