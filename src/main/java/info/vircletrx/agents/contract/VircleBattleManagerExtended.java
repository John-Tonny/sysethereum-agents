package info.vircletrx.agents.contract;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import kong.unirest.json.JSONArray;

import org.tron.tronj.abi.datatypes.Address;
import org.tron.tronj.abi.datatypes.generated.Uint256;
import org.tron.tronj.abi.datatypes.generated.Bytes32;
import org.tron.tronj.client.exceptions.IllegalException;
import org.tron.tronj.proto.Chain.Block;
import org.tron.tronj.proto.Chain.BlockHeader;
import org.tron.tronj.utils.Numeric;

import info.vircletrx.agents.addition.TrxContract;
import info.vircletrx.agents.addition.DefaultBlockParameter;
import info.vircletrx.agents.addition.DefaultBlockParameterNumber;

public class VircleBattleManagerExtended extends  VircleBattleManager {
    public VircleBattleManagerExtended(TrxContract trxCntr) {
        super(trxCntr);
    }

    public static String getAddress(String networkId) {
        return getPreviouslyDeployedAddress(networkId);
    }


    public List<NewBattleEventResponse> getNewBattleEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<NewBattleEventResponse> result = new ArrayList<>();

        BigInteger start =  ((DefaultBlockParameterNumber)startBlock).getBlockNumber();
        BigInteger stop =  ((DefaultBlockParameterNumber)endBlock).getBlockNumber();

        try {
            Block startBk = this.client.getBlockByNum(start.longValue());
            Block stopBk = this.client.getBlockByNum(stop.longValue());

            long startTime = startBk.getBlockHeader().getRawData().getTimestamp();
            long stopTime = stopBk.getBlockHeader().getRawData().getTimestamp();

            NewBattleEventResponse newBattleEventResponse =
                    new NewBattleEventResponse();
            String url = "https://api.shasta.trongrid.io/v1/contracts/";
            url += this.getStaticDeployedAddress("1");
            url += "/events?event_name=" + this.NEWBATTLE_EVENT.getName();
            url += "&min_block_timestamp=" + Long.toString(startTime);
            url += "&max_block_timestamp=" + Long.toString(stopTime);
            HttpResponse<JsonNode> response= Unirest.get(url)
                    .asJson();
            if(response.getStatus() == 200) {
                JSONObject myObj = response.getBody().getObject();
                boolean status = myObj.getBoolean("success");
                JSONArray mydata = myObj.getJSONArray("data");
                if (status && !mydata.isEmpty()) {
                    for(int j=0; j<mydata.length(); j++) {
                        JSONObject data = (JSONObject) mydata.get(j);

                        JSONObject log = data.getJSONObject("result");
                        newBattleEventResponse.superblockHash = new Bytes32(Numeric.hexStringToByteArray(log.getString("superblockhash")));
                        newBattleEventResponse.submitter = new Address(log.getString("submitter"));
                        newBattleEventResponse.challenger = new Address(log.getString("challenger"));
                        result.add(newBattleEventResponse);
                    }
                }
            }
        }catch(IllegalException e){
            System.out.println(e.getMessage());
        }
        return result;
    }
    public List<RespondBlockHeadersEventResponse> getNewBlockHeadersEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<RespondBlockHeadersEventResponse> result = new ArrayList<>();

        BigInteger start =  ((DefaultBlockParameterNumber)startBlock).getBlockNumber();
        BigInteger stop =  ((DefaultBlockParameterNumber)endBlock).getBlockNumber();

        try {
            Block startBk = this.client.getBlockByNum(start.longValue());
            Block stopBk = this.client.getBlockByNum(stop.longValue());

            long startTime = startBk.getBlockHeader().getRawData().getTimestamp();
            long stopTime = stopBk.getBlockHeader().getRawData().getTimestamp();

            RespondBlockHeadersEventResponse newBlockHeadersEventResponse =
                    new RespondBlockHeadersEventResponse();
            String url = "https://api.shasta.trongrid.io/v1/contracts/";
            url += this.getStaticDeployedAddress("1");
            url += "/events?event_name=" + this.RESPONDBLOCKHEADERS_EVENT.getName();
            url += "&min_block_timestamp=" + Long.toString(startTime);
            url += "&max_block_timestamp=" + Long.toString(stopTime);
            HttpResponse<JsonNode> response= Unirest.get(url)
                    .asJson();
            if(response.getStatus() == 200) {
                JSONObject myObj = response.getBody().getObject();
                boolean status = myObj.getBoolean("success");
                JSONArray mydata = myObj.getJSONArray("data");
                if (status && !mydata.isEmpty()) {
                    for(int j=0; j<mydata.length(); j++) {
                        JSONObject data = (JSONObject) mydata.get(j);

                        JSONObject log = data.getJSONObject("result");
                        newBlockHeadersEventResponse.superblockHash = new Bytes32(Numeric.hexStringToByteArray(log.getString("superblockhash")));
                        newBlockHeadersEventResponse.merkleHashCount = new Uint256(log.getBigInteger("merkleHashCount"));
                        newBlockHeadersEventResponse.submitter = new Address(log.getString("submitter"));
                        result.add(newBlockHeadersEventResponse);
                    }
                }
            }
        }catch(IllegalException e){
            System.out.println(e.getMessage());
        }
        return result;
    }

}
