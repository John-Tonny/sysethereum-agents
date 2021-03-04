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
@Slf4j(topic = "GetSPVHandler")
public class GetSPVHandler extends CommonHttpHandler {

    private final Gson gson;
    private final VircleToTrxClient vircleToTrxClient;

    public GetSPVHandler(
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
            response = gson.toJson(vircleToTrxClient.getSuperblockSPVProof(hash != null ? Sha256Hash.wrap(hash) : null, height != null ? Integer.decode(height) : -1, true));
        } catch (Exception exception) {
            response = gson.toJson(new RestError("Could not get SPV proof, internal error!"));
        }
        writeResponse(httpsExchange, response);
    }
}
