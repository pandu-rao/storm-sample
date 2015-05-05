package com.socketek.prototypes.storm;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

import java.io.FileNotFoundException;

import org.yaml.snakeyaml.Yaml;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SampleConfig {
    private static final String CONFIG_FILE = "storm-sample.yml";

    public static String getDrpcServer() throws FileNotFoundException {
	File file = new File(CONFIG_FILE);
	InputStream input = new FileInputStream(file);

	Yaml yaml = new Yaml();

	Map config = (Map) yaml.load(input);
	Object mode = config.get("mode");
	Map stormType = (Map) config.get(mode);
	String drpcServer = (String) stormType.get("drpc_server");

	return drpcServer;
    }

    public static Integer getDrpcPort() throws FileNotFoundException {
	File file = new File(CONFIG_FILE);
	InputStream input = new FileInputStream(file);

	Yaml yaml = new Yaml();

	Map config = (Map) yaml.load(input);
	Object mode = config.get("mode");
	Map stormType = (Map) config.get(mode);
	int drpcPort = (Integer) stormType.get("drpc_port");

	return drpcPort;
    }
}
