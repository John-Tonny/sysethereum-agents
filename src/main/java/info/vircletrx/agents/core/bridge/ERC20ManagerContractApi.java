package info.vircletrx.agents.core.bridge;

import info.vircletrx.agents.addition.Tuple6;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import info.vircletrx.agents.contract.VircleERC20Manager;
import info.vircletrx.agents.contract.VircleERC20ManagerExtended;
import info.vircletrx.agents.core.bridge.battle.NewCancelTransferRequestEvent;
import info.vircletrx.agents.core.trx.BridgeTransferInfo;
import info.vircletrx.agents.addition.DefaultBlockParameter;
import org.tron.tronj.abi.datatypes.Address;
import org.tron.tronj.abi.datatypes.generated.Uint256;
import org.tron.tronj.abi.datatypes.generated.Uint32;
import org.tron.tronj.abi.datatypes.generated.Uint8;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class ERC20ManagerContractApi {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("ERC20ManagerContractApi");

    private final VircleERC20ManagerExtended challenges;

    public ERC20ManagerContractApi(
            VircleERC20ManagerExtended erc20ManagerForChallenges
    ) {
        this.challenges = erc20ManagerForChallenges;
    }

    /**
     * Listens to CancelTransferRequest events from VircleERC20Manager contract within a given block window
     * and parses web3j-generated instances into easier to manage NewCancelTransferRequest objects.
     *
     * @param startBlock First Tronx block to poll.
     * @param endBlock   Last Tronx block to poll.
     * @return All CancelTransferRequest events from VircleERC20Manager as NewCancelTransferRequest objects.
     * @throws IOException
     */
    public List<NewCancelTransferRequestEvent> getNewCancelTransferEvents(long startBlock, long endBlock) throws IOException {
        List<NewCancelTransferRequestEvent> result = new ArrayList<>();
        List<VircleERC20Manager.CancelTransferRequestEventResponse> newCancelTransferEvents =
                challenges.getCancelTransferRequestEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (VircleERC20Manager.CancelTransferRequestEventResponse response : newCancelTransferEvents) {
            NewCancelTransferRequestEvent newCancelTransferRequestEvent = new NewCancelTransferRequestEvent();
            newCancelTransferRequestEvent.canceller = response.canceller.getValue();
            newCancelTransferRequestEvent.bridgeTransferId = response.bridgetransferid.getValue().intValue();
            result.add(newCancelTransferRequestEvent);
        }
        return result;
    }
    public BridgeTransferInfo getBridgeTransfer(Integer bridgeTransferId) throws Exception{
        Tuple6<Uint256, Uint256, Address, Address, Uint32, Uint8> bridgeTransferData = challenges.getBridgeTransfer(new Uint32(bridgeTransferId));
        BridgeTransferInfo bridgeInfo = new BridgeTransferInfo();
        bridgeInfo.timestamp = bridgeTransferData.component1().getValue().intValue();
        bridgeInfo.value = bridgeTransferData.component2().getValue();
        bridgeInfo.erc20ContractAddress = bridgeTransferData.component3().getValue();
        bridgeInfo.tokenFreezerAddress = bridgeTransferData.component4().getValue();
        bridgeInfo.assetGUID = bridgeTransferData.component5().getValue().intValue();
        bridgeInfo.status = BridgeTransferInfo.BridgeTransferStatus.fromInteger(bridgeTransferData.component6().getValue().intValue());
        return bridgeInfo;
    }
}
