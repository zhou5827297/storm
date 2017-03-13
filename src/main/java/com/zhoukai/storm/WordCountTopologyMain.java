package com.zhoukai.storm;


import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import java.util.Arrays;

public class WordCountTopologyMain {
    public static void main(String[] args) {
        try {
            //定义一个Topology
            TopologyBuilder builder = new TopologyBuilder();
            builder.setSpout("word-reader", new WordReader());
            builder.setBolt("word-normalizer", new WordNormalizer()).shuffleGrouping("word-reader");
            builder.setBolt("word-counter", new WordCounter(), 2).fieldsGrouping("word-normalizer", new Fields("word"));
            //配置
            Config conf = new Config();
            conf.put("wordsFile", "/data/test.txt");
            conf.setDebug(true);

//            conf.put(Config.NIMBUS_HOST, "192.168.0.50"); //配置nimbus连接主机地址，比如：192.168.10.1
            conf.put(Config.NIMBUS_THRIFT_PORT, 6627);//配置nimbus连接端口，默认 6627
            conf.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(new String[]{"192.168.0.50"})); //配置zookeeper连接主机地址，可以使用集合存放多个
            conf.put(Config.STORM_ZOOKEEPER_PORT, 2181); //配置zookeeper连接端口，默认2181
            //提交Topology
            conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 5000);
            conf.setNumWorkers(3);
            StormSubmitter.submitTopology("Getting-Started-Toplogie", conf, builder.createTopology());


            //创建一个本地模式cluster
//            LocalCluster cluster = new LocalCluster();
//            cluster.submitTopology("Getting-Started-Toplogie", conf, builder.createTopology());
//            Thread.sleep(10000);
//            cluster.killTopology("Getting-Started-Toplogie");
//            cluster.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}