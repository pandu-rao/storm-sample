package com.socketek.prototypes.storm;

import backtype.storm.utils.DRPCClient;

import com.socketek.prototypes.storm.SampleConfig;

public class SampleClient {
    public static void main(String[] args) throws Exception {
	System.out.println("Start");
	int drpcPort = SampleConfig.getDrpcPort();
	DRPCClient client = new DRPCClient("localhost", drpcPort);

	long startTime = System.nanoTime();
	String out = client.execute("to_upper", "hello");
	long endTime = System.nanoTime();
	double processingTime = (endTime - startTime) / 1000000000.0;

	String s = "****************************************\n";
	System.out.println(s + s + out + "\n" + s + s);

	System.out.printf("Processing time: %f seconds\n", processingTime);
	System.out.println("End");
    }
}
