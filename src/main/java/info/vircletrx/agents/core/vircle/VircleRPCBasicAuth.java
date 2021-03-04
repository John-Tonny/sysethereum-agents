/*
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package info.vircletrx.agents.core.vircle;


import com.thetransactioncompany.jsonrpc2.client.ConnectionConfigurator;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import java.util.Base64;

@Slf4j(topic = "VircleRPCBasicAuth")
public class VircleRPCBasicAuth implements ConnectionConfigurator {

    private final String username;
    private final String password;

    public VircleRPCBasicAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void configure(HttpURLConnection conn) {
        String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encoding);
    }
}
