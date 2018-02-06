package com.kayakwise.jportmap;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IConnectionTimeoutHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;


public class In implements IDataHandler, IConnectHandler, IConnectionTimeoutHandler , IDisconnectHandler{
	static protected Log log = LogFactory.getLog(In.class);
	
	Rule rule;
	
	public In(Rule rule){
		this.rule = rule;
	}

	public boolean onDisconnect(INonBlockingConnection connection) throws IOException {
		
		Map map = (Map)connection.getAttachment();
		log.info( map.get("remoteAddress") + ":" + map.get("remotePort") + " disconnected." );
		INonBlockingConnection outConnection = (INonBlockingConnection)map.get("outConnection");
		outConnection.close();
		return true;
	}


	public boolean onConnectionTimeout(INonBlockingConnection connection)
			throws IOException {
		log.info( connection.getRemoteAddress()+ ":" + connection.getRemotePort() + " timeout." );
		return true;
	}


	public boolean onConnect(INonBlockingConnection connection) throws IOException,
			BufferUnderflowException, MaxReadSizeExceededException {
		log.info( connection.getRemoteAddress()+ ":" + connection.getRemotePort() + " connected." );
		Map map = new HashMap();
		map.put("remoteAddress", connection.getRemoteAddress().toString());
		map.put("remotePort", Integer.toString(connection.getRemotePort()));
		INonBlockingConnection outConnection = new NonBlockingConnection(rule.outAddr, rule.outPort, new Out(connection) );
		map.put( "outConnection", outConnection);
		connection.setAttachment(map);
		
		
		
		return true;
	}


	public boolean onData(INonBlockingConnection connection) throws IOException,
			BufferUnderflowException, ClosedChannelException,
			MaxReadSizeExceededException {
		INonBlockingConnection outConnection = (INonBlockingConnection)((Map)connection.getAttachment()).get("outConnection");
		outConnection.write(connection.readBytesByLength(connection.available()));
		return true;
	}
	
}
