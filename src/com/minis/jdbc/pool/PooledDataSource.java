package com.minis.jdbc.pool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class PooledDataSource implements DataSource{
	/**
	 * TODO 拓展：
	 * 线程安全：
	 * 提供两个队列，一个用于忙的连接，一个用于空闲连接：
	 * private BlockingQueue<PooledConnection> busy;
	 * private BlockingQueue<PooledConnection> idle;
	 * 获取数据库连接就从idle队列中获取：
	 * while (true) {
	 * // 死等一个空闲连接。然后加入忙队列
	 * conn = idle.poll();
	 * }
	 *
	 * 还应当判断连接数是否到了最大，如果没有，则要先创建一个新的连接。创建的时候要小心了，因为是多线程的，所以要再次校验是否超过最大连接数，如使用CAS技术：
	 * if (size.get() < getPoolProperties().getMaxActive()) {
	 * // 如果超过，则表明在并发环境下有其他线程也同时增加了连接数，导致总数超过了设定的最大值
	 *       if (size.addAndGet(1) > getPoolProperties().getMaxActive()) {
	 *       // 若确实超过最大限制，则需要回滚这次尝试增加的操作，即调用 size.decrementAndGet() 减少一个连接计数
	 *         size.decrementAndGet();
	 *       } else {
	 *         return createConnection(now, con, username, password);
	 *       }
	 *     }
	 *
	 * 还应当设置一个timeout，如果在规定的时间内还没有拿到一个连接，就要抛出一个异常
	 * if ((System.currentTimeMillis() - now) >= maxWait) {
	 *         throw new PoolExhaustedException(
	 *           "Timeout: Unable to fetch a connection in " + (maxWait / 1000) +
	 *           " seconds.");
	 *     } else {
	 *         continue;
	 *     }
	 *
	 * 关闭连接，也就是从busy队列移除，然后加入到idle队列中
	 *
	 */
	private List<PooledConnection> connections = null;
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private int initialSize = 2;
	private Properties connectionProperties;	


	public PooledDataSource() {
	}
	
	private void initPool() {
		this.connections = new ArrayList<>(initialSize);
		try {
			for(int i = 0; i < initialSize; i++){
				Connection connect = DriverManager.getConnection(url, username, password);
				PooledConnection pooledConnection = new PooledConnection(connect, false);
				this.connections.add(pooledConnection);
System.out.println("********add connection pool*********");				
			}
		} catch (Exception e) {		
			e.printStackTrace();
		}
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return getConnectionFromDriver(getUsername(), getPassword());
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnectionFromDriver(username, password);
	}
	
	protected Connection getConnectionFromDriver(String username, String password) throws SQLException {
		Properties mergedProps = new Properties();
		Properties connProps = getConnectionProperties();
		if (connProps != null) {
			mergedProps.putAll(connProps);
		}
		if (username != null) {
			mergedProps.setProperty("user", username);
		}
		if (password != null) {
			mergedProps.setProperty("password", password);
		}

		if (this.connections == null) {
			initPool();
		}

		PooledConnection pooledConnection= getAvailableConnection();
		
		while(pooledConnection == null){
			pooledConnection = getAvailableConnection();
			
			if(pooledConnection == null){
				try {
					TimeUnit.MILLISECONDS.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		return pooledConnection;
	}
	
	private PooledConnection getAvailableConnection() throws SQLException{
		for(PooledConnection pooledConnection : this.connections){
			if (!pooledConnection.isActive()){
				pooledConnection.setActive(true);
				return pooledConnection;
			}
		}

		return null;
	}

	protected Connection getConnectionFromDriverManager(String url, Properties props) throws SQLException {
		return DriverManager.getConnection(url, props);
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
		try {
			Class.forName(this.driverClassName);
		}
		catch (ClassNotFoundException ex) {
			throw new IllegalStateException("Could not load JDBC driver class [" + driverClassName + "]", ex);
		}
		
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}
	
	public Properties getConnectionProperties() {
		return connectionProperties;
	}
	public void setConnectionProperties(Properties connectionProperties) {
		this.connectionProperties = connectionProperties;
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter arg0) throws SQLException {
		
	}

	@Override
	public void setLoginTimeout(int arg0) throws SQLException {
		
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return null;
	}




}
