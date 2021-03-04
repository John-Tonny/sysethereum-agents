/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package info.vircletrx.agents.checker;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.PeerAddress;
import org.springframework.stereotype.Component;
import info.vircletrx.agents.constants.AgentConstants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Makes sure the vircle peer is up. Otherwise prevents the agent from starting (fail fast strategy)
 */
@Component
@Slf4j(topic = "OperatorPeersChecker")
public class OperatorPeersChecker {

    private final AgentConstants agentConstants;
    private final VirclePeerFactory virclePeerFactory;

    public OperatorPeersChecker(
            AgentConstants agentConstants,
            VirclePeerFactory virclePeerFactory
    ) {
        this.agentConstants = agentConstants;
        this.virclePeerFactory = virclePeerFactory;
    }

    public void setup() {
        int defaultPort = agentConstants.getVircleParams().getPort();
        // List<String> peerStrings = List.of("127.0.0.1");
        List<String> peerStrings = List.of("68.79.34.218:9804");
        List<PeerAddress> peerAddresses;

        try {
            peerAddresses = virclePeerFactory.buildVirclePeerAddresses(defaultPort, peerStrings);

            if (peerAddresses.isEmpty()) {
                // Can't happen until we implement peer list configuration
                throw new RuntimeException("No Vircle Peers");
            }
            for (PeerAddress address : peerAddresses) {
                checkAddressOrFail(address.getSocketAddress());
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkAddressOrFail(InetSocketAddress isa) {
        try {
            Socket socket = new Socket(isa.getHostName(), isa.getPort());
            socket.close();
        } catch (IOException ex) {
            throw new RuntimeException("Cannot connect to Vircle node " + isa.getHostName() + ":" + isa.getPort());
        }
    }

}
