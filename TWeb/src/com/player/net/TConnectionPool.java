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
	 * 获取一个存活的ServiceConnection
	 * Note:这个方法耗时且会阻塞
	 * @param timeoutMills 取连接的超时时间
	 * @return null 无可用连接
	 */
	public @Nullable TWebServiceConnection requireWebServiceConnection(long timeoutMills) {
		boolean acquired = false;
		try {
			acquired = mConnectionLatch.tryAcquire(timeoutMills, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			TWebLogUtil.d(e);
		}
		if(!acquired) {
			TWebLogUtil.v("获取连接超时");
			return null;
		}
		synchronized (mServiceSet) {
			cleanConnectionSet(mServiceSet);
			if(!mServiceSet.isEmpty()) {
				Iterator<TWebServiceConnection> iter = mServiceSet.iterator();
				if(iter != null && iter.hasNext()) {
					TWebServiceConnection connection = iter.next();
					mServiceSet.remove(connection);
					TWebLogUtil.v("取出连接："+connection.toString());
					return connection;
				}
			}
		}
		//没有连接，新建一个
		TWebServiceConnection serviceCon = new TWebServiceConnection(mClient);
		if(serviceCon.isAlive()) {
			TWebLogUtil.v("新建连接："+serviceCon.toString());
			return serviceCon;
		} else {
			TWebLogUtil.v("建立连接超时");
			return null;	
		}
	}
	
	/**
	 * 获取一个存活的连接
	 * @see #requireWebServiceConnection(long)
	 */
	public @Nullable TWebServiceConnection requireWebServiceConnection() {
		return requireWebServiceConnection(DefaultLatchTimeout);
	}
	
	
	/**
	 * 将一个连接放回连接池
	 * @param connection 放回连接池的连接
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
