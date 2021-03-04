package info.vircletrx.agents.service.rest;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.springframework.stereotype.Service;
import info.vircletrx.agents.core.VircleToTrxClient;
import info.vircletrx.agents.util.RestError;

import java.io.IOException;
import java.util.LinkedHashMap;

@Service
@Slf4j(topic = "GetSuperblockByVircleHandler")
public class GetSuperblockByVircleHandler extends CommonHttpHandler {

    private final Gson gson;
    private final VircleToTrxClient vircleToTrxClient;

    public GetSuperblockByVircleHandler(
            Gson gson,
            VircleToTrxClient vircleToTrxClient
    ) {
        this.gson = gson;
        this.vircleToTrxClient = vircleToTrxClient;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        HttpsExchange httpsExchange = (HttpsExchange) httpExchange;
        if (setOriginAndHandleOptionsMethod(httpsExchange)) return;

        LinkedHashMap<String, String> params = queryToMap(httpsExchange.getRequestURI().getQuery());
        String response;

        try {
            String hash = sanitizeHash(params.get("hash"));
            String height = params.get("height");
            response = gson.toJson(vircleToTrxClient.getSuperblockByVircleBlock(hash != null ? Sha256Hash.wrap(hash) : null, height != null ? Integer.decode(height) : -1));
        } catch (Exception exception) {
            response =  gson.toJson(new RestError("Could not get Superblock, internal error!"));
        }
        writeResponse(httpsExchange, response);
    }
}
