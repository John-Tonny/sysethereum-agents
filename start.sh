#!/bin/bash

nohup java -Dsysethereum.agents.conf.file=/mnt/ethereum/eth-contract/sysethereum-agents/data/sysethereum-agents.conf -jar ./target/sysethereum-agents-1.0-jar-with-dependencies.jar &
