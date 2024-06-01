package auctionsniper;

import java.util.ArrayList;

public class SniperPortfolio implements SniperCollector {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final ArrayList<AuctionSniper> snipers = new ArrayList<>();
    private PortfolioListener listener;

    public void addPortfolioListener(PortfolioListener listener) {
        this.listener = listener;
    }

    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        listener.sniperAdded(sniper);
    }
}
