package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperLost(SniperSnapshot sniperSnapshot);

    void sniperWon(SniperSnapshot sniperSnapshot);

    void sniperBidding(SniperSnapshot sniperSnapshot);

    void sniperWinning(SniperSnapshot sniperSnapshot);
}
