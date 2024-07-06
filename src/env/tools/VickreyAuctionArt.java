package tools;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.ObsProperty;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 *      Artefato que implementa o leilão Vickrey.
 */
public class VickreyAuctionArt extends Artifact {

    private class Bid {
        double value;

        Bid(double value, String bidder) {
            this.value = value;
        }
    }

    private PriorityQueue<Bid> bids;

    @OPERATION
    public void init(String taskDs, int maxValue) {
        // Propriedades observáveis
        defineObsProperty("task", taskDs);
        defineObsProperty("maxValue", maxValue);
        defineObsProperty("currentBid", maxValue);
        defineObsProperty("currentWinner", "no_winner");
        defineObsProperty("auctionState", "open");
        
        bids = new PriorityQueue<>(Comparator.comparingDouble(b -> b.value));
    }

    @OPERATION
    public void bid(double bidValue) {
        ObsProperty opAuctionState = getObsProperty("auctionState");
        if (opAuctionState.stringValue().equals("open")) {
            String bidder = getCurrentOpAgentId().getAgentName();
            bids.add(new Bid(bidValue, bidder));

            if (bids.size() > 1) {
                Bid secondHighest = bids.peek();
                getObsProperty("currentBid").updateValue(secondHighest.value);
            } else {
                getObsProperty("currentBid").updateValue(bidValue);
            }
            getObsProperty("currentWinner").updateValue(bidder);
        } else {
            failed("Auction is closed");
        }
    }

    @OPERATION
    public void openAuction() {
        getObsProperty("auctionState").updateValue("open");
    }

    @OPERATION
    public void closeAuction() {
        getObsProperty("auctionState").updateValue("closed");
    }

    @OPERATION
    public void startAuctionWithTimer(int duration) {
        openAuction();
        execInternalOp("advanceTimeAndCloseAuction", duration);
    }

    @INTERNAL_OPERATION
    void advanceTimeAndCloseAuction(int duration) {
        await_time(duration);
        closeAuction();
    }
}
