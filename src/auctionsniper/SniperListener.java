package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperStateChanged(SniperSnapshot sniperSnapshot);

    void sniperLost(SniperSnapshot sniperSnapshot);

    void sniperWon(SniperSnapshot sniperSnapshot);

    void sniperWinning(SniperSnapshot sniperSnapshot);
}
