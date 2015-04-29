package com.socketek.prototypes.storm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.StormSubmitter;

import backtype.storm.drpc.DRPCSpout;
import backtype.storm.drpc.ReturnResults;

import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;

import backtype.storm.task.ShellBolt;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;

import backtype.storm.topology.base.BaseBasicBolt;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SampleTopology {
    // a bolt that does not spawn an external shell process
    public static class SelfBolt extends BaseBasicBolt {
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	    declarer.declare(new Fields("result", "return-info"));
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
	    String input = tuple.getString(0);
	    Object retInfo = tuple.getValue(1);
	    collector.emit(new Values(input.toUpperCase(), retInfo));
	}
    }

    public static class PythonBolt extends ShellBolt implements IRichBolt {
	// todo: study serial version uid
	private static final long serialVersionUID = -2944145284614143111L;

	public PythonBolt()	{
	    super("python", "python_bolt.py");
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	    declarer.declare(new Fields("result", "return-info"));
	}

	public Map<String, Object> getComponentConfiguration() {
	    return null;
	}
    }

    public static void main(String[] args) throws Exception {
	Config conf = new Config();
	TopologyBuilder builder = new TopologyBuilder();

	if (args == null || args.length == 0) {
	    LocalDRPC drpc = new LocalDRPC();

	    DRPCSpout spout = new DRPCSpout("to_upper", drpc);
	    builder.setSpout("drpc", spout);
	    builder.setBolt("pythonbolt", new PythonBolt(), 3).shuffleGrouping("drpc");
	    builder.setBolt("return", new ReturnResults(), 3).shuffleGrouping("pythonbolt");

	    LocalCluster cluster = new LocalCluster();
	    cluster.submitTopology("pythonbolt", conf, builder.createTopology());

	    long startTime = System.nanoTime();
	    String out = drpc.execute("to_upper", "hello");
	    long endTime = System.nanoTime();
	    double processingTime = (endTime - startTime) / 1000000000.0;

	    String s = "****************************************\n";
	    System.out.println(s + s + out + "\n" + s + s);
	    System.out.printf("Processing time: %f seconds\n", processingTime);

	    cluster.shutdown();
	    drpc.shutdown();
	} else {
	    List<String> drpc_servers = new ArrayList<String>();
	    drpc_servers.add("127.0.0.1");
	    conf.put(Config.DRPC_SERVERS, drpc_servers);
	    conf.setNumWorkers(3);

	    DRPCSpout spout = new DRPCSpout("to_upper");
	    builder.setSpout("drpc", spout);
	    builder.setBolt("pythonbolt", new PythonBolt(), 3).shuffleGrouping("drpc");
	    builder.setBolt("return", new ReturnResults(), 3).shuffleGrouping("pythonbolt");

	    StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
	}
    }
}
