package test.unit.auctionsniper.ui;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.Item;
import auctionsniper.ui.Column;
import auctionsniper.SniperSnapshot;
import auctionsniper.ui.SnipersTableModel;
import com.objogate.exception.Defect;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

public class SnipersTableModelTest {
    @RegisterExtension
    private final JUnit5Mockery context = new JUnit5Mockery();
    private final TableModelListener listener = context.mock(TableModelListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(new Item("item0", Integer.MAX_VALUE), auction);
    private final SnipersTableModel model = new SnipersTableModel();

    @BeforeEach
    public void attachModelListener() {
        model.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setsSniperValuesInColumns() {
        SniperSnapshot bidding = sniper.getSniperSnapshot().bidding(555, 666);
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));
            oneOf(listener).tableChanged(with(aChangeInRow(0)));
        }});
        model.sniperAdded(sniper);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(0, bidding);
    }

    @Test
    public void setsUpColumnHeadings() {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    public void notifiesListenerWhenAddingASniper() {
        context.checking(new Expectations() {{
            oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());

        model.sniperAdded(sniper);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, sniper.getSniperSnapshot());
    }

    @Test
    public void holdsSnipersInAdditionOrder() {
        AuctionSniper sniper2 = new AuctionSniper(new Item("item1", Integer.MAX_VALUE), auction);
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.sniperAdded(sniper);
        model.sniperAdded(sniper2);

        assertEquals("item0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    @Test
    public void updatesCorrectRowForSniper() {
        AuctionSniper sniper2 = new AuctionSniper(new Item("item1", Integer.MAX_VALUE), auction);
        SniperSnapshot bidding1 = sniper.getSniperSnapshot().bidding(100, 123);
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.sniperAdded(sniper);
        model.sniperAdded(sniper2);
        model.sniperStateChanged(bidding1);

        assertRowMatchesSnapshot(0, bidding1);
        assertRowMatchesSnapshot(1, sniper2.getSniperSnapshot());
    }

    @Test
    public void throwsDefectIfNoExistingSniperForAnUpdate() {
        AuctionSniper sniper2 = new AuctionSniper(new Item("item1", Integer.MAX_VALUE), auction);
        SniperSnapshot bidding1 = sniper.getSniperSnapshot().bidding(100, 123);
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.sniperAdded(sniper2);
        assertThrows(Defect.class, () -> model.sniperStateChanged(bidding1));
    }

    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
        assertEquals(snapshot.itemId, cellValue(row, Column.ITEM_IDENTIFIER));
        assertEquals(snapshot.lastPrice, cellValue(row, Column.LAST_PRICE));
        assertEquals(snapshot.lastBid, cellValue(row, Column.LAST_BID));
        assertEquals(SnipersTableModel.textFor(snapshot.sniperState), cellValue(row, Column.SNIPER_STATE));
    }

    private Object cellValue(int row, Column column) {
        return model.getValueAt(row, column.ordinal());
    }

    private Matcher<TableModelEvent> aChangeInRow(int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row));
    }

    private Matcher<TableModelEvent> anInsertionAtRow(int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }
}
