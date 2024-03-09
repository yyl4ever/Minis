package com.minis.batis;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.jdbc.core.JdbcTemplate;

public class DefaultSqlSessionFactory implements SqlSessionFactory{
	@Autowired
	JdbcTemplate jdbcTemplate;
	/**
	 * 「mapper/」，示例通过 applicationContext.xml 注入
	 */
	String mapperLocations;
	public String getMapperLocations() {
		return mapperLocations;
	}
	public void setMapperLocations(String mapperLocations) {
		this.mapperLocations = mapperLocations;
	}

	/**
	 * 缓存 {sqlId, MapperNode}
	 */
	Map<String,MapperNode> mapperNodeMap = new HashMap<>();
	public Map<String, MapperNode> getMapperNodeMap() {
		return mapperNodeMap;
	}

//	private DataSource dataSource;
//	
//	public void setDataSource(DataSource dataSource) {
//		this.dataSource = dataSource;
//	}
//
//	public DataSource getDataSource() {
//		return this.dataSource;
//	}

	public DefaultSqlSessionFactory() {
	}

	/**
	 * Bean 初始化会触发 init 方法，初始化过程中，会扫描整个 mapper 路径，将具体的 sql 存入到 mapperNodeMap 中
	 */
	public void init() {
	    scanLocation(this.mapperLocations);
	    for (Map.Entry<String, MapperNode> entry : this.mapperNodeMap.entrySet()) {
	    	System.out.println(entry.getKey() + " : " + entry.getValue());
	    }
	}

	@Override
	public SqlSession openSession() {	
		SqlSession newSqlSession = new DefaultSqlSession();
		// 临时设置 jdbcTemplate，这样后续执行 sql 时可以替换，使动态数据源成为可能，读写分离时特别有用
		newSqlSession.setJdbcTemplate(jdbcTemplate);
		newSqlSession.setSqlSessionFactory(this);
		
		return newSqlSession;
	}

	
    private void scanLocation(String location) {
		// 获取当前类的类加载器（ClassLoader），并使用该加载器查找相对于类路径根目录（即 classpath 的起点）的资源
		/**
		 * this.getClass() 获取的是当前对象所对应的类对象。
		 * .getClassLoader() 调用类对象的方法来获取加载这个类的类加载器。
		 * getResource("") 是调用类加载器的方法，传入空字符串表示查找类路径的根目录。这个方法返回一个 java.net.URL 对象，它指向类路径根下资源的实际位置。
		 * .getPath() 则进一步从返回的 URL 对象中提取出资源的路径部分，将其转换为字符串格式。这个路径通常是绝对路径，取决于运行时环境和类路径的具体配置。
		 */
    	String sLocationPath = this.getClass().getClassLoader().getResource("").getPath()+location;
        //URL url  =this.getClass().getClassLoader().getResource("/"+location);
    	System.out.println("mapper location : "+sLocationPath);
        File dir = new File(sLocationPath);
        for (File file : dir.listFiles()) {
            if(file.isDirectory()){
            	scanLocation(location+"/"+file.getName());
            }else{
				// 举例：mapper/User_Mapper.xml
                buildMapperNodes(location+"/"+file.getName());
            }
        }
    }

	private Map<String, MapperNode> buildMapperNodes(String filePath) {
		System.out.println(filePath);
        SAXReader saxReader=new SAXReader();
		/**
		 * this.getClass() 获取当前类的对象引用。
		 * .getClassLoader() 从该类对象获取用于加载该类的类加载器。
		 * getResource(filePath) 是类加载器的方法，它接收一个字符串参数，代表相对于类路径根目录的资源路径。这个方法会在类路径下查找指定的资源，并返回一个表示该资源位置的URL对象。
		 * 这样做的目的是为了在不同的环境和打包方式下（例如JAR、WAR或者开发环境中的源码目录）都能灵活地定位到类路径中的资源文件。
		 */
        URL xmlPath=this.getClass().getClassLoader().getResource(filePath);
        try {
			// 解析具体的 mapper.xml 文件
			Document document = saxReader.read(xmlPath);
			Element rootElement=document.getRootElement();

			String namespace = rootElement.attributeValue("namespace");

	        Iterator<Element> nodes = rootElement.elementIterator();;
	        while (nodes.hasNext()) {
	        	Element node = nodes.next();
	            String id = node.attributeValue("id");
	            String parameterType = node.attributeValue("parameterType");
	            String resultType = node.attributeValue("resultType");
	            String sql = node.getText();

				// 封装 sql 为 java 对象
	            MapperNode selectnode = new MapperNode();
	            selectnode.setNamespace(namespace);
	            selectnode.setId(id);
	            selectnode.setParameterType(parameterType);
	            selectnode.setResultType(resultType);
	            selectnode.setSql(sql);
	            selectnode.setParameter("");

				// 缓存到 map 中
	            this.mapperNodeMap.put(namespace + "." + id, selectnode);
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    return this.mapperNodeMap;
	}

	@Override
	public MapperNode getMapperNode(String name) {
		return this.mapperNodeMap.get(name);
	}

}
