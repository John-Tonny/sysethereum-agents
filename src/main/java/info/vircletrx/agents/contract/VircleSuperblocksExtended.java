package info.vircletrx.agents.contract;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import org.tron.tronj.abi.datatypes.Address;
import org.tron.tronj.abi.datatypes.generated.Bytes32;
import org.tron.tronj.client.exceptions.IllegalException;
import org.tron.tronj.proto.Chain.Block;
import org.tron.tronj.utils.Numeric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.vircletrx.agents.addition.TrxContract;

/**
 * Extension of web3j auto-generated VircleSuperblocks class
 * with event polling methods for SuperblockDefenderClient.
 *
 * @author Catalina Juarros
 */
public class VircleSuperblocksExtended extends VircleSuperblocks {

    public VircleSuperblocksExtended(TrxContract trxCntr) {
        super(trxCntr);
    }

    public static String getAddress(String networkId) {
        return getPreviouslyDeployedAddress(networkId);
    }
    /* ---- EVENTS FOR POLLING ---- */

    public List<NewSuperblockEventResponse> getNewSuperblockEvents(long startBlock, long endBlock) throws IOException {

        List<NewSuperblockEventResponse> result = new ArrayList<>();

        try {
            Block startBk = this.client.getBlockByNum(startBlock);
            Block stopBk = this.client.getBlockByNum(endBlock);

            long startTime = startBk.getBlockHeader().getRawData().getTimestamp();
            long stopTime = stopBk.getBlockHeader().getRawData().getTimestamp();

            NewSuperblockEventResponse newResponse = new NewSuperblockEventResponse();

            String url = "https://api.shasta.trongrid.io/v1/contracts/";
            url += this.getStaticDeployedAddress("1");
            url += "/events?event_name=" + this.NEWSUPERBLOCK_EVENT.getName();
            url += "&min_block_timestamp=" + Long.toString(startTime);
            url += "&max_block_timestamp=" + Long.toString(stopTime);

            HttpResponse<JsonNode> response= Unirest.get(url).asJson();
            if(response.getStatus() == 200) {

                JSONObject myObj = response.getBody().getObject();
                boolean status = myObj.getBoolean("success");
                JSONArray mydata = myObj.getJSONArray("data");
                if (status && !mydata.isEmpty()) {
                    for(int j=0; j<mydata.length(); j++) {
                        JSONObject data = (JSONObject) mydata.get(j);

                        JSONObject log = data.getJSONObject("result");

                        newResponse.superblockHash = new Bytes32(Numeric.hexStringToByteArray(log.getString("superblockHash")));
                        newResponse.who = new Address(log.getString("who"));
                        result.add(newResponse);
                    }
                }
            }
        }catch(IllegalException e){
            System.out.println(e.getMessage());
        }
        return result;
    }

    public List<ApprovedSuperblockEventResponse> getApprovedSuperblockEvents(long startBlock, long endBlock) throws IOException {

        List<ApprovedSuperblockEventResponse> result = new ArrayList<>();

        try {
            Block startBk = this.client.getBlockByNum(startBlock);
            Block stopBk = this.client.getBlockByNum(endBlock);

            long startTime = startBk.getBlockHeader().getRawData().getTimestamp();
            long stopTime = stopBk.getBlockHeader().getRawData().getTimestamp();

            ApprovedSuperblockEventResponse newResponse = new ApprovedSuperblockEventResponse();

            String url = "https://api.shasta.trongrid.io/v1/contracts/";
            url += this.getStaticDeployedAddress("1");
            url += "/events?event_name=" + this.APPROVEDSUPERBLOCK_EVENT.getName();
            url += "&min_block_timestamp=" + Long.toString(startTime);
            url += "&max_block_timestamp=" + Long.toString(stopTime);

            HttpResponse<JsonNode> response= Unirest.get(url).asJson();
            if(response.getStatus() == 200) {
                JSONObject myObj = response.getBody().getObject();
                boolean status = myObj.getBoolean("success");
                JSONArray mydata = myObj.getJSONArray("data");
                if (status && !mydata.isEmpty()) {
                    for(int j=0; j<mydata.length(); j++) {
                        JSONObject data = (JSONObject) mydata.get(j);

                        JSONObject log = data.getJSONObject("result");

                        newResponse.superblockHash = new Bytes32(Numeric.hexStringToByteArray(log.getString("superblockHash")));
                        newResponse.who = new Address(log.getString("who"));
                        result.add(newResponse);
                    }
                }
            }
        }catch(IllegalException e){
            System.out.println(e.getMessage());
        }
        return result;
    }

    public List<SemiApprovedSuperblockEventResponse> getSemiApprovedSuperblockEvents(long startBlock, long endBlock) throws IOException {

        List<SemiApprovedSuperblockEventResponse> result = new ArrayList<>();

        try {
            Block startBk = this.client.getBlockByNum(startBlock);
            Block stopBk = this.client.getBlockByNum(endBlock);

            long startTime = startBk.getBlockHeader().getRawData().getTimestamp();
            long stopTime = stopBk.getBlockHeader().getRawData().getTimestamp();

            SemiApprovedSuperblockEventResponse newResponse = new SemiApprovedSuperblockEventResponse();

            String url = "https://api.shasta.trongrid.io/v1/contracts/";
            url += this.getStaticDeployedAddress("1");
            url += "/events?event_name=" + this.SEMIAPPROVEDSUPERBLOCK_EVENT.getName();
            url += "&min_block_timestamp=" + Long.toString(startTime);
            url += "&max_block_timestamp=" + Long.toString(stopTime);

            HttpResponse<JsonNode> response= Unirest.get(url).asJson();
            if(response.getStatus() == 200) {
                JSONObject myObj = response.getBody().getObject();
                boolean status = myObj.getBoolean("success");
                JSONArray mydata = myObj.getJSONArray("data");
                if (status && !mydata.isEmpty()) {
                    for(int j=0; j<mydata.length(); j++) {
                        JSONObject data = (JSONObject) mydata.get(j);

                        JSONObject log = data.getJSONObject("result");

                        newResponse.superblockHash = new Bytes32(Numeric.hexStringToByteArray(log.getString("superblockHash")));
                        newResponse.who = new Address(log.getString("who"));
                        result.add(newResponse);
                    }
                }
            }
        }catch(IllegalException e){
            System.out.println(e.getMessage());
        }
        return result;
    }

    /*
    private List<EthLog.LogResult> filterLog(long startBlock, long endBlock, Event event) throws IOException {
        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)),
                getContractAddress()
        );

        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        return ethLog.getLogs();

    }

     */
}