package info.vircletrx.agents.contract;

import info.vircletrx.agents.addition.DefaultBlockParameterNumber;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import org.tron.tronj.abi.datatypes.Address;
import org.tron.tronj.abi.datatypes.generated.Uint32;

import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import info.vircletrx.agents.addition.TrxContract;
import info.vircletrx.agents.addition.DefaultBlockParameter;
import org.tron.tronj.client.exceptions.IllegalException;
import org.tron.tronj.proto.Chain;

public class VircleERC20ManagerExtended extends  VircleERC20Manager {

    public VircleERC20ManagerExtended(TrxContract trxCntr) {
        super(trxCntr);
    }

    public static String getAddress(String networkId) {
        return getPreviouslyDeployedAddress(networkId);
    }


    public List<CancelTransferRequestEventResponse> getCancelTransferRequestEvents(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<CancelTransferRequestEventResponse> result = new ArrayList<>();

        BigInteger start =  ((DefaultBlockParameterNumber)startBlock).getBlockNumber();
        BigInteger stop =  ((DefaultBlockParameterNumber)endBlock).getBlockNumber();
        try {
            Chain.Block startBk = this.client.getBlockByNum(start.longValue());
            Chain.Block stopBk = this.client.getBlockByNum(stop.longValue());

            long startTime = startBk.getBlockHeader().getRawData().getTimestamp();
            long stopTime = stopBk.getBlockHeader().getRawData().getTimestamp();

            CancelTransferRequestEventResponse newCancelTransferEventResponse =
                    new CancelTransferRequestEventResponse();

            String url = "https://api.shasta.trongrid.io/v1/contracts/";
            url += this.getStaticDeployedAddress("1");
            url += "/events?event_name=" + this.CANCELTRANSFERREQUEST_EVENT.getName();
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
                        newCancelTransferEventResponse.canceller = new Address(log.getString("canceller"));
                        newCancelTransferEventResponse.bridgetransferid = new Uint32(log.getBigInteger("bridgetransferid"));
                        result.add(newCancelTransferEventResponse);
                    }
                }
            }
        }catch(IllegalException e){
            System.out.println(e.getMessage());
        }
        return result;
    }

}
