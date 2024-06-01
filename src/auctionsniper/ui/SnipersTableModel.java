package auctionsniper.ui;

import auctionsniper.*;
import com.objogate.exception.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {
    private static final String[] STATUS_TEXT = {"Joining", "Bidding", "Winning", "Lost", "Won"};
    private final ArrayList<SniperSnapshot> sniperSnapshots = new ArrayList<>();

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public int getRowCount() {
        return sniperSnapshots.toArray().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(sniperSnapshots.get(rowIndex));
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
        int row = rowMatching(newSniperSnapshot);
        sniperSnapshots.set(row, newSniperSnapshot);
        fireTableRowsUpdated(row, row);
    }

    @Override
    public void sniperAdded(AuctionSniper sniper) {
        sniperSnapshots.add(sniper.getSniperSnapshot());
        int row = sniperSnapshots.size() - 1;
        fireTableRowsInserted(row, row);
        sniper.addSniperListener(new SwingThreadSniperListener(this));
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    private int rowMatching(SniperSnapshot snapshot) {
        for (int i = 0; i < sniperSnapshots.size(); i++) {
            if (snapshot.isForSameItemAs(sniperSnapshots.get(i))) {
                return i;
            }
        }
        throw new Defect("Cannot find match for " + snapshot);
    }
}
