package com.socketek.prototypes.storm;

import java.lang.ClassNotFoundException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;

import backtype.storm.topology.base.BaseBasicBolt;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.socketek.prototypes.storm.SampleConfig;

public class SampleTopology {
    private static final Logger LOG = Logger.getLogger(SampleTopology.class);
    private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";

    private static final String DATABASE_URL = "jdbc:mysql://localhost:33066/operations?user=mongoose&password=password";

    private static Connection connection = null;

    // a bolt that reads data from a database
    public static class DatabaseBolt extends BaseBasicBolt {
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	    declarer.declare(new Fields("result", "return-info"));
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
	    if (connection != null) {
		return;
	    }

	    try {
		Class.forName(DATABASE_DRIVER);
		connection = DriverManager.getConnection(DATABASE_URL);
	    } catch (ClassNotFoundException e) {
		LOG.error("Failed to load %s".format(DATABASE_DRIVER));
		e.printStackTrace();
	    } catch (SQLException e) {
		LOG.error("Caught SQLException");
		e.printStackTrace();
	    }
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
	    String input = tuple.getString(0);
	    Object retInfo = tuple.getValue(1);

	    try {
		String sql = "select index_ from feature where id in ";
		sql += "(select feature_id from model_feature where model_id=1 and item_id=1000)";

		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		List indexes = new ArrayList();
		while (resultSet.next()) {
		    Integer index_ = resultSet.getInt("index_");
		    indexes.add(index_);
		}

		LOG.info(indexes);
	    } catch (SQLException e) {
		LOG.error("Caught SQLException");
		e.printStackTrace();
	    }

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

    public static TopologyBuilder getTopologyBuilder(LocalDRPC drpc) {
	DRPCSpout spout = null;
	if (drpc == null) {
	    spout = new DRPCSpout("to_upper");
	} else {
	    spout = new DRPCSpout("to_upper", drpc);
	}

	TopologyBuilder builder = new TopologyBuilder();
	builder.setSpout("drpc", spout);
	builder.setBolt("pythonbolt", new PythonBolt(), 3).shuffleGrouping("drpc");
	builder.setBolt("databasebolt", new DatabaseBolt(), 3).shuffleGrouping("pythonbolt");
	builder.setBolt("return", new ReturnResults(), 3).shuffleGrouping("databasebolt");

	return builder;
    }

    public static void main(String[] args) throws Exception {
	Config conf = new Config();
	TopologyBuilder builder = null;

	if (args == null || args.length == 0) {
	    LocalDRPC drpc = new LocalDRPC();
	    builder = getTopologyBuilder(drpc);
	    LocalCluster cluster = new LocalCluster();
	    cluster.submitTopology("pythonbolt", conf, builder.createTopology());

	    long startTime = System.nanoTime();
	    String out = drpc.execute("to_upper", "hello");
	    long endTime = System.nanoTime();
	    double processingTime = (endTime - startTime) / 1000000.0;

	    String s = "****************************************\n";
	    System.out.println(s + s + out + "\n" + s + s);
	    System.out.printf("Processing time: %f ms\n", processingTime);

	    cluster.shutdown();
	    drpc.shutdown();
	} else {
	    List<String> drpcServers = new ArrayList<String>();

	    String drpcServer = SampleConfig.getDrpcServer();
	    drpcServers.add(drpcServer);

	    conf.put(Config.DRPC_SERVERS, drpcServers);
	    conf.setNumWorkers(3);

	    builder = getTopologyBuilder(null);

	    StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
	}
    }
}
