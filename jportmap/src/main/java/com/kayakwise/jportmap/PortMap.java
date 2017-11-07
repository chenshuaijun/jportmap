package com.kayakwise.jportmap;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.xsocket.connection.Server;

public class PortMap {
	static protected Log	log		= LogFactory.getLog(PortMap.class);
	static ObjectMapper		mapper	= new ObjectMapper();

	static void init() throws Exception {
		InputStream is = null;
		try {
			is = In.class.getResourceAsStream("/conf/portmap.conf");
			Rule[] rules = (Rule[]) mapper.readValue(is, Rule[].class);
			for (int i = 0; i < rules.length; i++) {
				Rule rule = rules[i];
				In router = new In(rule);
				Server srv = new Server(rule.inAddr, rule.inPort, router);
				log.info("listen on " + rule.inAddr + ":" + rule.inPort + " out " + rule.outAddr + ":" + rule.outPort
						+ " : " + rule.remark);
				srv.start();
			}
			log.info("all started.");
		} finally {
			is.close();
		}
	}

	public static void main(String args[]) {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
