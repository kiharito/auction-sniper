package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperLost();

    void sniperWon();

    void sniperBidding(SniperSnapshot sniperSnapshot);

    void sniperWinning();
}
