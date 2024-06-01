package auctionsniper.xmpp;

import auctionsniper.Announcer;
import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuction implements Auction {
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
    private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
    private final Chat chat;

    public XMPPAuction(XMPPConnection connection, String auctionId) {
        AuctionMessageTranslator translator = translatorFor(connection);
        chat = connection.getChatManager().createChat(auctionId, translator);
        addAuctionEventListener(chatDisconnectorFor(translator));
    }

    @Override
    public void bid(int amount) {
        sendMessage(String.format(BID_COMMAND_FORMAT, amount));
    }

    @Override
    public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener listener) {
        auctionEventListeners.addListener(listener);
    }

    private AuctionMessageTranslator translatorFor(XMPPConnection connection) {
        return new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce());
    }

    private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) {
        return new AuctionEventListener() {

            @Override
            public void auctionClosed() {

            }

            @Override
            public void currentPrice(int price, int increment, PriceSource priceSource) {

            }

            @Override
            public void auctionFailed() {
                chat.removeMessageListener(translator);
            }
        };
    }

    private void sendMessage(final String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
