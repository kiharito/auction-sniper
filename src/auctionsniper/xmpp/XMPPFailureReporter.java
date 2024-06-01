package auctionsniper.xmpp;

public interface XMPPFailureReporter {
    void cannotTranslateMessage(String auctionId, String failureMessage, Exception exception);
}
