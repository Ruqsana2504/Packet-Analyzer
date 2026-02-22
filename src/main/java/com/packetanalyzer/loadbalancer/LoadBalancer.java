package com.packetanalyzer.loadbalancer;

public class LoadBalancer {

    public int getWorkerIndex(String connectionKey, int workerCount) {
        return Math.abs(connectionKey.hashCode()) % workerCount;
    }
}