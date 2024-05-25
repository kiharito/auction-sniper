package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final String itemId;
    private final Auction auction;
    private final SniperListener sniperListener;
    private boolean isWinning = false;
    private SniperSnapshot sniperSnapshot;

    public AuctionSniper(String itemId, Auction auction, SniperListener sniperListener) {
        this.itemId = itemId;
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.sniperSnapshot = SniperSnapshot.joining(itemId);
    }

    @Override
    public void auctionClosed() {
        if (isWinning) {
            sniperListener.sniperWon(new SniperSnapshot(itemId, sniperSnapshot.lastPrice, sniperSnapshot.lastPrice, SniperState.WON));
        } else {
            sniperListener.sniperLost(new SniperSnapshot(itemId, 0, 0, SniperState.LOST));
        }
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FromSniper;
        if (isWinning) {
            sniperSnapshot = sniperSnapshot.winning(price);
        } else {
            int bid = price + increment;
            auction.bid(bid);
            sniperSnapshot = sniperSnapshot.bidding(price, bid);
        }
        sniperListener.sniperStateChanged(sniperSnapshot);
    }
}
