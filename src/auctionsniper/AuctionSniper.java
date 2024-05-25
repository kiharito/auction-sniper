package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private final SniperListener sniperListener;
    private SniperSnapshot sniperSnapshot;

    public AuctionSniper(String itemId, Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.sniperSnapshot = SniperSnapshot.joining(itemId);
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
