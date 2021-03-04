package info.vircletrx.agents.service.rest;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import info.vircletrx.agents.core.VircleToTrxClient;
import info.vircletrx.agents.core.vircle.Keccak256Hash;
import info.vircletrx.agents.util.RestError;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j(topic = "GetSuperblockHandler")
public class GetSuperblockHandler extends JsonHttpHandler {

    private final Gson gson;

    private final VircleToTrxClient vircleToTrxClient;

    public GetSuperblockHandler(
            Gson gson,
            VircleToTrxClient vircleToTrxClient
    ) {
        this.gson = gson;
        this.vircleToTrxClient = vircleToTrxClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handle(HttpExchange httpExchange) throws IOException {
        HttpsExchange httpsExchange = (HttpsExchange) httpExchange;
        if (setOriginAndHandleOptionsMethod(httpsExchange)) return;

        Map<String, String> params;

        if (isMatchedApplicationJson(httpsExchange.getRequestHeaders())) {
            params = (Map<String, String>) (Object) getParams(httpsExchange);
        } else {
            params = queryToMap(httpsExchange.getRequestURI().getQuery());
        }

        String response;

        try {
            String hash = sanitizeHash(params.get("hash"));
            String height = params.get("height");
            response = gson.toJson(vircleToTrxClient.getSuperblock(Keccak256Hash.wrapNullable(hash), height != null ? Integer.decode(height) : -1));
        } catch (Exception exception) {
            response = gson.toJson(new RestError("Could not get Superblock, internal error!"));
        }

        writeResponse(httpsExchange, response);
    }

}
