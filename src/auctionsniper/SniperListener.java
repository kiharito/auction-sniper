package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperLost();

    void sniperWon();

    void sniperBidding(SniperState sniperState);

    void sniperWinning();
}
