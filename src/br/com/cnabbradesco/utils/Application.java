package br.com.cnabbradesco.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Application {
	public final static String DIR = System.getProperty("user.dir");
	public final static String RES = DIR + "/br/com/cnabbradesco/resources";
	public final static Logger LOG = LogManager.getLogger(br.com.cnabbradesco.utils.Application.class);
}
