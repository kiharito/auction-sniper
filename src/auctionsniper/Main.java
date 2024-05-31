package auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.xmpp.XMPPAuctionHouse;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Main {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final ArrayList<Auction> notToBeGCd = new ArrayList<>();
    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

    public Main() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow(snipers));
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
        ui.addUserRequestListener(itemId -> {
            snipers.addSniper(SniperSnapshot.joining(itemId));
            Auction auction = auctionHouse.auctionFor(itemId);
            notToBeGCd.add(auction);

            auction.addAuctionEventListener(new AuctionSniper(itemId, auction, new SwingThreadSniperListener()));
            auction.join();
        });
    }

    private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                auctionHouse.disconnect();
            }
        });
    }


    public class SwingThreadSniperListener implements SniperListener {
        @Override
        public void sniperStateChanged(SniperSnapshot sniperSnapshot) {
            SwingUtilities.invokeLater(() -> snipers.sniperStateChanged(sniperSnapshot));
        }

        @Override
        public void addSniper(SniperSnapshot sniperSnapshot) {

        }
    }
}
