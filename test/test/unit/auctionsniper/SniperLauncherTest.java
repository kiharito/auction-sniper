package test.unit.auctionsniper;

import auctionsniper.*;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.CoreMatchers.equalTo;

public class SniperLauncherTest {
    @RegisterExtension
    private final JUnit5Mockery context = new JUnit5Mockery();
    private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
    private final Auction auction = context.mock(Auction.class);
    private final SniperCollector sniperCollector = context.mock(SniperCollector.class);
    private final SniperLauncher launcher = new SniperLauncher(auctionHouse, sniperCollector);
    private final States auctionState = context.states("auction").startsAs("not joined");

    @Test
    void addsNewSniperToCollectorAndThenJoinsAuction() {
        final String itemId = "item123";
        context.checking(new Expectations() {{
            allowing(auctionHouse).auctionFor(itemId);
            will(returnValue(auction));

            oneOf(auction).addAuctionEventListener(with(sniperForItem(itemId)));
            when(auctionState.is("not joined"));

            oneOf(sniperCollector).addSniper(with(sniperForItem(itemId)));
            when(auctionState.is("not joined"));

            oneOf(auction).join();
            then(auctionState.is("joined"));
        }});
        launcher.joinAuction(itemId);
    }

    private Matcher<AuctionSniper> sniperForItem(final String itemId) {
        return new FeatureMatcher<>(equalTo(itemId), "sniper that is ", "was") {
            @Override
            protected String featureValueOf(AuctionSniper sniper) {
                return sniper.getSniperSnapshot().itemId;
            }
        };
    }
}
