package com.player.net;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.xlightweb.client.HttpClient;

import com.player.sender.ServiceTask;
import com.player.util.TWebLogUtil;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.policy.privateutil.ServiceConfigurationError;

public class TConnectionPool {
	static private TConnectionPool mInstance = null;
	static synchronized public TConnectionPool getInstance() {
		if(mInstance == null) {
			mInstance = new TConnectionPool();
		}
		return mInstance;
	}
	
	private HttpClient mClient;
	private final int DefaultConnectionTimeOut = 20000;
	private final int DefaultLatchTimeout = 20000;
	private final int MaxConnectionCount = 3;
	private Semaphore mConnectionLatch = null;
	
	private TConnectionPool() {
		mClient = new HttpClient();
		mClient.setConnectTimeoutMillis(DefaultConnectionTimeOut);
		mConnectionLatch = new Semaphore(MaxConnectionCount, true);
	}

	private HashSet<TWebServiceConnection> mServiceSet = new HashSet<TWebServiceConnection>();
	
	/**
	 * ��ȡһ������ServiceConnection
	 * Note:���������ʱ�һ�����
	 * @param timeoutMills ȡ���ӵĳ�ʱʱ��
	 * @return null �޿�������
	 */
	public @Nullable TWebServiceConnection requireWebServiceConnection(long timeoutMills) {
		boolean acquired = false;
		try {
			acquired = mConnectionLatch.tryAcquire(timeoutMills, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			TWebLogUtil.d(e);
		}
		if(!acquired) {
			TWebLogUtil.v("��ȡ���ӳ�ʱ");
			return null;
		}
		synchronized (mServiceSet) {
			cleanConnectionSet(mServiceSet);
			if(!mServiceSet.isEmpty()) {
				Iterator<TWebServiceConnection> iter = mServiceSet.iterator();
				if(iter != null && iter.hasNext()) {
					TWebServiceConnection connection = iter.next();
					mServiceSet.remove(connection);
					TWebLogUtil.v("ȡ�����ӣ�"+connection.toString());
					return connection;
				}
			}
		}
		//û�����ӣ��½�һ��
		TWebServiceConnection serviceCon = new TWebServiceConnection(mClient);
		if(serviceCon.isAlive()) {
			TWebLogUtil.v("�½����ӣ�"+serviceCon.toString());
			return serviceCon;
		} else {
			TWebLogUtil.v("�������ӳ�ʱ");
			return null;	
		}
	}
	
	/**
	 * ��ȡһ����������
	 * @see #requireWebServiceConnection(long)
	 */
	public @Nullable TWebServiceConnection requireWebServiceConnection() {
		return requireWebServiceConnection(DefaultLatchTimeout);
	}
	
	
	/**
	 * ��һ�����ӷŻ����ӳ�
	 * @param connection �Ż����ӳص�����
	 */
	public void returnWebServiceConnection(TWebServiceConnection connection) {
		if(connection != null && connection.isAlive()) {
			synchronized (mServiceSet) {
				mServiceSet.add(connection);
			}
		}
		mConnectionLatch.release();
	}
	
	private void cleanConnectionSet(Set<TWebServiceConnection> connectionSet) {
		ArrayList<TWebServiceConnection> cleanAry = new ArrayList<TWebServiceConnection>();
		for(TWebServiceConnection connection : connectionSet) {
			if(!connection.isAlive()) {
				cleanAry.add(connection);
			}
		}
		connectionSet.removeAll(cleanAry);
	}
	
}
