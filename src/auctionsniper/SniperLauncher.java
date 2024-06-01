package auctionsniper;

import auctionsniper.ui.SnipersTableModel;

public class SniperLauncher implements UserRequestListener {
    private final AuctionHouse auctionHouse;
    private final SnipersTableModel snipers;

    public SniperLauncher(AuctionHouse auctionHouse1, SnipersTableModel snipers1) {
        this.auctionHouse = auctionHouse1;
        this.snipers = snipers1;
    }

    @Override
    public void joinAuction(String itemId) {
        Auction auction = auctionHouse.auctionFor(itemId);
        AuctionSniper sniper = new AuctionSniper(itemId, auction);
        auction.addAuctionEventListener(sniper);
        snipers.addSniper(sniper);
        auction.join();
    }
}
