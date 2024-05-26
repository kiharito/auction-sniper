package test.integration.auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.jupiter.api.Test;
import test.endtoend.auctionsniper.AuctionSniperDriver;

import static org.hamcrest.Matchers.*;

public class MainWindowTest {
    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<>(equalTo("item1"), "join request");

        mainWindow.addUserRequestListener(buttonProbe::setReceivedValue);

        driver.startBiddingFor("item1");
        driver.check(buttonProbe);
    }
}
