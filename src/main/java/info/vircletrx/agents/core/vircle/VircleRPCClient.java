/*
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package info.vircletrx.agents.core.vircle;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import info.vircletrx.agents.constants.SystemProperties;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
@Slf4j(topic = "VircleRPCClient")
public class VircleRPCClient {

    private final JSONRPC2Session rpcSession;

    public VircleRPCClient(SystemProperties config) throws MalformedURLException {
        rpcSession = new JSONRPC2Session(new URL(config.vircleRPCURL()));
        rpcSession.getOptions().ignoreVersion(true);
        rpcSession.setConnectionConfigurator(new VircleRPCBasicAuth(config.vircleRPCUser(), config.vircleRPCPassword()));
    }

    public String makeCoreCall(String method, List<Object> params) throws JSONRPC2SessionException {
        JSONObject result = rpcCall(method, params);
        if (result != null) {
            return result.toJSONString();
        }
        throw new JSONRPC2SessionException("Empty response not expected");
    }

    private JSONObject rpcCall(String method, List<Object> params) throws JSONRPC2SessionException {
        JSONRPC2Request request = new JSONRPC2Request(method, params, 1);
        JSONRPC2Response response = rpcSession.send(request);
        JSONObject result = new JSONObject();
        if (response.indicatesSuccess()) {
            Object r1 = response.getResult();
            if (r1 instanceof String) {
                result.put("result", r1.toString());
            } else if(r1 instanceof JSONArray) {
                result.put("result", r1);
            } else if(r1 instanceof  JSONObject)  {
                result = (JSONObject) response.getResult();
            } else {
                result.put("result", r1);
            }
        } else {
            throw new JSONRPC2SessionException(response.getError().getMessage());
        }
        return result;
    }
}
