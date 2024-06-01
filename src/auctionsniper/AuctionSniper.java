package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final Item item;
    private final Auction auction;
    private SniperListener sniperListener;
    private SniperSnapshot sniperSnapshot;

    public AuctionSniper(Item item, Auction auction) {
        this.item = item;
        this.auction = auction;
        this.sniperSnapshot = SniperSnapshot.joining(item.identifier);
    }

    public SniperSnapshot getSniperSnapshot() {
        return sniperSnapshot;
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
                if (item.allowsBid(bid)) {
                    auction.bid(bid);
                    sniperSnapshot = sniperSnapshot.bidding(price, bid);
                } else {
                    sniperSnapshot = sniperSnapshot.losing(price);
                }
            }
        }
        notifyChange();
    }

    @Override
    public void auctionFailed() {

    }

    private void notifyChange() {
        sniperListener.sniperStateChanged(sniperSnapshot);
    }
}
