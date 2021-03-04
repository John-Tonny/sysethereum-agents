package info.vircletrx.agents.contract;

import info.vircletrx.agents.addition.DefaultBlockParameterNumber;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import org.tron.tronj.abi.datatypes.Address;
import org.tron.tronj.abi.datatypes.generated.Bytes32;
import org.tron.tronj.abi.datatypes.generated.Uint256;
import org.tron.tronj.client.exceptions.IllegalException;
import org.tron.tronj.proto.Chain;
import org.tron.tronj.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import info.vircletrx.agents.addition.TrxContract;
import info.vircletrx.agents.addition.DefaultBlockParameter;

public class VircleClaimManagerExtended extends VircleClaimManager {

    public VircleClaimManagerExtended(TrxContract trxCntr) {
        super(trxCntr);
    }

    public List<VircleClaimManager.SuperblockClaimSuccessfulEventResponse> getSuperblockClaimSuccessfulEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<VircleClaimManager.SuperblockClaimSuccessfulEventResponse> result = new ArrayList<>();

        BigInteger start =  ((DefaultBlockParameterNumber)startBlock).getBlockNumber();
        BigInteger stop =  ((DefaultBlockParameterNumber)endBlock).getBlockNumber();
        try {
            Chain.Block startBk = this.client.getBlockByNum(start.longValue());
            Chain.Block stopBk = this.client.getBlockByNum(stop.longValue());

            long startTime = startBk.getBlockHeader().getRawData().getTimestamp();
            long stopTime = stopBk.getBlockHeader().getRawData().getTimestamp();

            VircleClaimManager.SuperblockClaimSuccessfulEventResponse newEventResponse =
                    new VircleClaimManager.SuperblockClaimSuccessfulEventResponse();
            String url = "https://api.shasta.trongrid.io/v1/contracts/";
            url += this.getStaticDeployedAddress("1");
            url += "/events?event_name=" + this.SUPERBLOCKCLAIMSUCCESSFUL_EVENT.getName();
            url += "&min_block_timestamp=" + Long.toString(startTime);
            url += "&max_block_timestamp=" + Long.toString(stopTime);
            HttpResponse<JsonNode> response= Unirest.get(url)
                    .asJson();
            if(response.getStatus() == 200){
                JSONObject myObj = response.getBody().getObject();
                boolean status = myObj.getBoolean("success");
                JSONArray mydata = myObj.getJSONArray("data");
                if (status && !mydata.isEmpty()) {
                    for(int j=0; j<mydata.length(); j++) {
                        JSONObject data = (JSONObject) mydata.get(j);

                        JSONObject log = data.getJSONObject("result");
                        newEventResponse.superblockHash = new Bytes32(Numeric.hexStringToByteArray(log.getString("superblockHash")));
                        newEventResponse.submitter = new Address(log.getString("submitter"));
                        newEventResponse.processCounter = new Uint256(Numeric.toBigIntNoPrefix(log.getString("processCounter")));
                        result.add(newEventResponse);
                    }
                }
            }
        }catch(IllegalException e){
            System.out.println(e.getMessage());
        }
        return result;
    }
    public List<VircleClaimManager.SuperblockClaimFailedEventResponse> getSuperblockClaimFailedEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<VircleClaimManager.SuperblockClaimFailedEventResponse> result = new ArrayList<>();

        BigInteger start =  ((DefaultBlockParameterNumber)startBlock).getBlockNumber();
        BigInteger stop =  ((DefaultBlockParameterNumber)endBlock).getBlockNumber();
        try {
            Chain.Block startBk = this.client.getBlockByNum(start.longValue());
            Chain.Block stopBk = this.client.getBlockByNum(stop.longValue());

            long startTime = startBk.getBlockHeader().getRawData().getTimestamp();
            long stopTime = stopBk.getBlockHeader().getRawData().getTimestamp();

            VircleClaimManager.SuperblockClaimFailedEventResponse newEventResponse =
                    new VircleClaimManager.SuperblockClaimFailedEventResponse();
            String url = "https://api.shasta.trongrid.io/v1/contracts/";
            url += this.getStaticDeployedAddress("1");
            url += "/events?event_name=" + this.SUPERBLOCKCLAIMFAILED_EVENT.getName();
            url += "&min_block_timestamp=" + Long.toString(startTime);
            url += "&max_block_timestamp=" + Long.toString(stopTime);

            HttpResponse<JsonNode> response = Unirest.get(url)
                    .asJson();
            if(response.getStatus() == 200){
                JSONObject myObj = response.getBody().getObject();
                boolean status = myObj.getBoolean("success");
                JSONArray mydata = myObj.getJSONArray("data");
                if (status && !mydata.isEmpty()) {
                    for(int j=0; j<mydata.length(); j++) {
                        JSONObject data = (JSONObject) mydata.get(j);

                        JSONObject log = data.getJSONObject("result");

                        newEventResponse.superblockHash = new Bytes32(Numeric.hexStringToByteArray(log.getString("superblockHash")));

                        newEventResponse.challenger = new Address(log.getString("challenger"));
                        newEventResponse.processCounter = new Uint256(log.getBigInteger("processCounter"));
                        result.add(newEventResponse);
                    }
                }
            }
        }catch(IllegalException e){
            System.out.println(e.getMessage());
        }
        return result;
    }
}
