#!/bin/bash

nohup java -Dvircletrx.agents.conf.file=/mnt/ethereum/agents/vircletrx-agents/data/vircletrx-agents.conf -jar ./target/vircletrx-agents-1.0-jar-with-dependencies.jar &
