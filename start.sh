#!/bin/bash

nohup java -Dvircletrx.agents.conf.file=/mnt/ethereum/eth-contract/vircletrx-agents/data/vircletrx-agents.conf -jar ./target/ vircletrx-agents-1.0-jar-with-dependencies.jar &
