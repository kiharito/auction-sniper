package test.integration.auctionsniper.ui;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import test.endtoend.auctionsniper.AuctionSniperDriver;

import static org.hamcrest.Matchers.*;

public class MainWindowTest {
    private final SniperPortfolio portfolio = new SniperPortfolio();
    private final MainWindow mainWindow = new MainWindow(portfolio);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<Item> itemProbe =
                new ValueMatcherProbe<>(equalTo(new Item("item1", 789)), "item request");

        mainWindow.addUserRequestListener(itemProbe::setReceivedValue);

        driver.startBiddingFor("item1", 789);
        driver.check(itemProbe);
    }

    @AfterEach
    void stopDriver() {
        driver.dispose();
    }
}
