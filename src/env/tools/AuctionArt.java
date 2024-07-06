package tools;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.ObsProperty;
import cartago.INTERNAL_OPERATION;

/**
 *      Artefato que implementa o leilão.
 */
public class AuctionArt extends Artifact {

    @OPERATION public void init(String taskDs, int maxValue) {
        // Propriedades observáveis
        defineObsProperty("task",          taskDs);
        defineObsProperty("maxValue",      maxValue);
        defineObsProperty("currentBid",    maxValue);
        defineObsProperty("currentWinner", "no_winner");
        defineObsProperty("auctionState",  "open");
        defineObsProperty("time",          0);
        defineObsProperty("maxTime",       5); // Definindo um tempo máximo de 5 segundos para o leilão
    }

    @OPERATION public void bid(double bidValue) {
        ObsProperty opAuctionState  = getObsProperty("auctionState");
        if (opAuctionState.stringValue().equals("open")) {
            ObsProperty opCurrentValue  = getObsProperty("currentBid");
            ObsProperty opCurrentWinner = getObsProperty("currentWinner");
            if (bidValue < opCurrentValue.doubleValue()) {
                opCurrentValue.updateValue(bidValue);
                opCurrentWinner.updateValue(getCurrentOpAgentId().getAgentName());
            }
        } else {
            failed("Auction is closed");
        }
    }

    @OPERATION public void openAuction() {
        ObsProperty opAuctionState = getObsProperty("auctionState");
        opAuctionState.updateValue("open");
        execInternalOp("advanceTime");
    }

    @OPERATION public void closeAuction() {
        ObsProperty opAuctionState = getObsProperty("auctionState");
        opAuctionState.updateValue("closed");
    }

    @INTERNAL_OPERATION void advanceTime() {
        while (true) {
            await_time(1000); 

            ObsProperty timeProp = getObsProperty("time");
            
            int currentTime = timeProp.intValue();
            timeProp.updateValue(currentTime + 1);

            if (currentTime + 1 > getObsProperty("maxTime").intValue()) {
                closeAuction();
                break;
            }
        }
    }

    /*
    @OPERATION public void clearAuction() {
        ObsProperty opAuctionState = getObsProperty("auctionState");
        opAuctionState.updateValue("closed");

        ObsProperty opCurrentValue  = getObsProperty("currentBid");
        ObsProperty opCurrentWinner = getObsProperty("currentWinner");
        opCurrentValue.updateValue(getObsProperty("maxValue").doubleValue());
        opCurrentWinner.updateValue("no_winner");
    }
    */
}
