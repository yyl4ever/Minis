package com.minis.core;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.minis.beans.factory.config.BeanDefinition;

/**
 * 解析 xml
 */
public class ClassPathXmlResource implements Resource {
	Document document;
	Element rootElement;
	Iterator<Element> elementIterator;
	
	public ClassPathXmlResource(String fileName) {
        SAXReader saxReader=new SAXReader();
		// 将配置文件装载进来 -- 类加载器获取资源
        URL xmlPath=this.getClass().getClassLoader().getResource(fileName);
        try {
			this.document = saxReader.read(xmlPath);
			// 获取根元素
			this.rootElement=document.getRootElement();
			// 生成一个迭代器，用于遍历
			this.elementIterator=this.rootElement.elementIterator();
		} catch (DocumentException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public boolean hasNext() {
		return this.elementIterator.hasNext();
	}

	@Override
	public Object next() {
		return this.elementIterator.next();
	}


}
