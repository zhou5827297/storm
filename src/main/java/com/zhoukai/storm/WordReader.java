package com.zhoukai.storm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class WordReader implements IRichSpout {
    private static final long serialVersionUID = 1L;
    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private boolean completed = false;

    public boolean isDistributed() {
        return false;
    }

    /**
     * 这是第一个方法，里面接收了三个参数，第一个是创建Topology时的配置，
     * 第二个是所有的Topology数据，第三个是用来把Spout的数据发射给bolt
     **/
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        try {
            //获取创建Topology时指定的要读取的文件路径
            this.fileReader = new FileReader(conf.get("wordsFile").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading file [" + conf.get("wordFile") + "]");
        }
        //初始化发射器
        this.collector = collector;

    }

    /**
     * 这是Spout最主要的方法，在这里我们读取文本文件，并把它的每一行发射出去（给bolt）
     * 这个方法会不断被调用，为了降低它对CPU的消耗，当任务完成时让它sleep一下
     **/
    public void nextTuple() {
        if (completed) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Do nothing
            }
            return;
        }
        String str;
        // Open the reader
        BufferedReader reader = new BufferedReader(fileReader);
        try {
            // Read all lines
            while ((str = reader.readLine()) != null) {
                /**
                 * 发射每一行，Values是一个ArrayList的实现
                 */
                this.collector.emit(new Values(str), str);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading tuple", e);
        } finally {
            completed = true;
        }

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("line"));

    }

    public void close() {
        // TODO Auto-generated method stub
    }

    public void activate() {
        // TODO Auto-generated method stub

    }

    public void deactivate() {
        // TODO Auto-generated method stub

    }

    public void ack(Object msgId) {
        System.out.println("OK:" + msgId);
    }

    public void fail(Object msgId) {
        System.out.println("FAIL:" + msgId);

    }

    public Map<String, Object> getComponentConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }
}