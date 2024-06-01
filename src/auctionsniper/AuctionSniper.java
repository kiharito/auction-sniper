package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private SniperListener sniperListener;
    private SniperSnapshot sniperSnapshot;

    public AuctionSniper(String itemId, Auction auction) {
        this.auction = auction;
        this.sniperSnapshot = SniperSnapshot.joining(itemId);
    }

    public void addSniperListener(SniperListener sniperListener) {
        this.sniperListener = sniperListener;
    }

    @Override
    public void auctionClosed() {
        sniperSnapshot = sniperSnapshot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FromSniper -> sniperSnapshot = sniperSnapshot.winning(price);
            case FromOtherBidder -> {
                int bid = price + increment;
                auction.bid(bid);
                sniperSnapshot = sniperSnapshot.bidding(price, bid);
            }
        }
        notifyChange();
    }

    private void notifyChange() {
        sniperListener.sniperStateChanged(sniperSnapshot);
    }
}
