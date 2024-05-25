package auctionsniper.ui;

import auctionsniper.Column;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;

import static auctionsniper.ui.MainWindow.*;

public class SnipersTableModel extends AbstractTableModel {
    private static final String[] STATUS_TEXT = {STATUS_JOINING, STATUS_BIDDING, STATUS_WINNING, STATUS_LOST, STATUS_WON};
    private String sniperState = STATUS_JOINING;
    private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
    private SniperSnapshot sniperSnapshot = STARTING_UP;

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (Column.at(columnIndex)) {
            case ITEM_IDENTIFIER -> {
                return sniperSnapshot.itemId;
            }
            case LAST_PRICE -> {
                return sniperSnapshot.lastPrice;
            }
            case LAST_BID -> {
                return sniperSnapshot.lastBid;
            }
            case SNIPER_STATE -> {
                return sniperState;
            }
            default -> throw new IllegalStateException("No column at " + columnIndex);
        }
    }

    public void sniperStatusChanged(SniperSnapshot newSniperSnapshot) {
        this.sniperSnapshot = newSniperSnapshot;
        this.sniperState = STATUS_TEXT[newSniperSnapshot.sniperState.ordinal()];
        fireTableRowsUpdated(0, 0);
    }
}
