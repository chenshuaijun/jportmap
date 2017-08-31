package com.kayawise.jportmap;

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

class Out implements IDataHandler, IConnectHandler, IConnectionTimeoutHandler , IDisconnectHandler{
	static protected Log log = LogFactory.getLog(Out.class);
	
	INonBlockingConnection inConnection;
	public Out(INonBlockingConnection inConnection) {
		this.inConnection = inConnection;
	}
	public boolean onDisconnect(INonBlockingConnection connection) throws IOException {
		
		Map map = (Map)connection.getAttachment();
		log.info( map.get("remoteAddress") + ":" + map.get("remotePort") + " disconnected." );
		inConnection.close();
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
		connection.setAttachment(map);
		
		return true;
	}


	public boolean onData(INonBlockingConnection connection) throws IOException,
			BufferUnderflowException, ClosedChannelException,
			MaxReadSizeExceededException {
		
		inConnection.write(connection.readBytesByLength(connection.available()));
		return true;
	}
}