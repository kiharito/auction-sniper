package auctionsniper.ui;

import auctionsniper.SniperSnapshot;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPERS_TABLE_NAME = "Snipers Table";
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_WON = "Won";
    private final SnipersTableModel snipers = new SnipersTableModel();

    public MainWindow() {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void sniperStatusChanged(SniperSnapshot sniperSnapshot, String statusText) {
        snipers.sniperStatusChanged(sniperSnapshot, statusText);
    }

    private void fillContentPane(JTable snipersTable) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public void showStatusText(String statusText) {
        snipers.setStatusText(statusText);
    }
}
