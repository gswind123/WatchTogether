package com.player.net;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.xlightweb.client.HttpClient;

import com.player.util.TWebLogUtil;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.policy.privateutil.ServiceConfigurationError;

public class TConnectionPool {
	static private TConnectionPool mInstance = null;
	static public TConnectionPool getInstance() {
		if(mInstance == null) {
			mInstance = new TConnectionPool();
		}
		return mInstance;
	}
	
	private HttpClient mClient;
	private final int DefaultTimeOut = 20000;
	
	private TConnectionPool() {
		mClient = new HttpClient();
		mClient.setConnectTimeoutMillis(DefaultTimeOut);
	}

	/** ������Ӧ��ֻ���TWebServiceConnectionʵ�� */
	private HashSet<TWebSocketConnection> mServiceSet = new HashSet<TWebSocketConnection>(); 
	
	/**
	 * ��ȡһ������ServiceConnection
	 * @return null:��������ʧ�� 
	 */
	synchronized public @Nullable TWebServiceConnection requireWebServiceConnection() {
		cleanConnectionSet(mServiceSet);
		if(!mServiceSet.isEmpty()) {
			for(TWebSocketConnection connection : mServiceSet) {
				if(connection instanceof TWebServiceConnection) {
					TWebServiceConnection ServiceCon = (TWebServiceConnection)connection;
					if(!ServiceCon.isBusy()) {
						return ServiceCon;
					}
				}
			}
		}
		//û�п������ӣ��½�һ��
		TWebServiceConnection serviceCon = new TWebServiceConnection(mClient);
		if(serviceCon.isAlive()) {
			mServiceSet.add(serviceCon);
			return serviceCon;
		} else {
			return null;	
		}
	}
	
	private void cleanConnectionSet(Set<TWebSocketConnection> connectionSet) {
		ArrayList<TWebSocketConnection> cleanAry = new ArrayList<TWebSocketConnection>();
		for(TWebSocketConnection connection : connectionSet) {
			if(!connection.isAlive()) {
				cleanAry.add(connection);
			}
		}
		connectionSet.removeAll(cleanAry);
	}
	
	
	/** XXX:�ǲ��ǰ��������ָ����ˣ� */
	public void execSendTask(SendTask task) {
		if(task == null) {
			throw new NullPointerException("task must not be null in execSendTask");
		}
		if(task.request == null) {
			throw new IllegalArgumentException("Request must not be null in execSendTask");
		}
		
	}
	
}
