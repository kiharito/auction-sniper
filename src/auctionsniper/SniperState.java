package auctionsniper;

import com.objogate.exception.Defect;

public enum SniperState {
    JOINING {
        @Override
        public SniperState whenAuctionClosed() {
            return LOST;
        }
    },
    BIDDING {
        @Override
        public SniperState whenAuctionClosed() {
            return LOST;
        }
    },
    LOSING {
        @Override
        public SniperState whenAuctionClosed() {
            return LOST;
        }
    },
    WINNING {
        @Override
        public SniperState whenAuctionClosed() {
            return WON;
        }
    },
    LOST,
    WON,
    FAILED;

    public SniperState whenAuctionClosed() {
        throw new Defect("Auction is already closed");
    }
}
