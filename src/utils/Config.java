package utils;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {
	private static Config instance = new Config();

	private Properties configFile;

	public static Config getInstance() {
		return instance;
	}

	private Config() {
		configFile = new java.util.Properties();
		try {
			configFile.load(new FileInputStream("config.properties"));
		} catch (Exception eta) {
			eta.printStackTrace();
		}
	}
	
	public String getOntologyFile() {
		return this.configFile.getProperty("owlFile");
	}
	
	public String getOntologyIri() {
		return this.configFile.getProperty("ontologyIri");
	}
	
	public String getPolicyFile() {
		return this.configFile.getProperty("policyFile");
	}
	
	public String getObdaFile() {
		return this.configFile.getProperty("obdaFile");
	}
	
	public String getObdaPropertiesFile() {
		return this.configFile.getProperty("obdaPropertiesFile");
	}

	public String getProperty(String key) {
		return this.configFile.getProperty(key);
	}

}