package info.vircletrx.agents.core.bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import info.vircletrx.agents.contract.VircleBattleManager;
import info.vircletrx.agents.contract.VircleBattleManagerExtended;
import info.vircletrx.agents.core.bridge.battle.NewBattleEvent;
import info.vircletrx.agents.core.vircle.Keccak256Hash;
import org.tron.tronj.abi.datatypes.generated.Bytes32;
import info.vircletrx.agents.addition.DefaultBlockParameter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class BattleContractApi {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("BattleContractApi");

    private final VircleBattleManagerExtended main;
    private final VircleBattleManagerExtended challenges;

    public BattleContractApi(
            VircleBattleManagerExtended battleManager,
            VircleBattleManagerExtended battleManagerForChallenges
    ) {
        this.main = battleManager;
        this.challenges = battleManagerForChallenges;
    }
    public VircleBattleManagerExtended getBattleManagerForChallenges(){
        return challenges;
    }
    public void updateGasPrice(BigInteger gasPriceMinimum) {
    }

    public boolean getSubmitterHitTimeout(Keccak256Hash superblockHash) throws Exception {
        return challenges.getSubmitterHitTimeout(new Bytes32(superblockHash.getBytes())).getValue();
    }

    public int getNumMerkleHashesBySession(Keccak256Hash superblockHash) throws Exception {
        BigInteger ret = main.getNumMerkleHashesBySession(new Bytes32(superblockHash.getBytes())).getValue();
        return ret.intValue();
    }

    public boolean sessionExists(Keccak256Hash superblockHash) throws Exception {
        return main.sessionExists(new Bytes32(superblockHash.getBytes())).getValue();
    }

    /**
     * Listens to NewBattle events from VircleBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage NewBattleEvent objects.
     *
     * @param startBlock First Tronx block to poll.
     * @param endBlock   Last Tronx block to poll.
     * @return All NewBattle events from VircleBattleManager as NewBattleEvent objects.
     * @throws IOException
     */
    public List<NewBattleEvent> getNewBattleEvents(long startBlock, long endBlock) throws IOException {
        List<NewBattleEvent> result = new ArrayList<>();
        List<VircleBattleManager.NewBattleEventResponse> newBattleEvents =
                challenges.getNewBattleEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (VircleBattleManager.NewBattleEventResponse response : newBattleEvents) {
            NewBattleEvent newBattleEvent = new NewBattleEvent();
            newBattleEvent.superblockHash = Keccak256Hash.wrap(response.superblockHash.getValue());
            newBattleEvent.submitter = response.submitter.getValue();
            newBattleEvent.challenger = response.challenger.getValue();
            result.add(newBattleEvent);
        }

        return result;
    }
}
