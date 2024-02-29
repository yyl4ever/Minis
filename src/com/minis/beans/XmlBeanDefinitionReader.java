package com.minis.beans;

import java.net.URL;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.minis.core.Resource;

public class XmlBeanDefinitionReader {
	SimpleBeanFactory bf;

	/**
	 * @param bf 注入具体实现，缺乏扩展性
	 */
	public XmlBeanDefinitionReader(SimpleBeanFactory bf) {
		this.bf = bf;
	}
	public void loadBeanDefinitions(Resource res) {
        while (res.hasNext()) {
        	Element element = (Element)res.next();
            String beanID=element.attributeValue("id");
            String beanClassName=element.attributeValue("class");

            BeanDefinition beanDefinition=new BeanDefinition(beanID,beanClassName);
            
            //handle properties
            List<Element> propertyElements = element.elements("property");
            PropertyValues PVS = new PropertyValues();
			// 遍历属性
            for (Element e : propertyElements) {
            	String pType = e.attributeValue("type");
            	String pName = e.attributeValue("name");
            	String pValue = e.attributeValue("value");
				// 将属性封装到 PropertyValue 中
            	PVS.addPropertyValue(new PropertyValue(pType, pName, pValue));
            }
        	beanDefinition.setPropertyValues(PVS);
        	//end of handle properties
        	
        	//get constructor
        	List<Element> constructorElements = element.elements("constructor-arg");       	
        	ArgumentValues AVS = new ArgumentValues();
			// 遍历构造器参数
        	for (Element e : constructorElements) {
            	String pType = e.attributeValue("type");
            	String pName = e.attributeValue("name");
            	String pValue = e.attributeValue("value");
				// 将参数封装到 ArgumentValue 中
        		AVS.addArgumentValue(new ArgumentValue(pType,pName,pValue));
        	}
    		beanDefinition.setConstructorArgumentValues(AVS);
        	//end of handle constructor

			// 注册的时候会创建非懒加载 bean, 所以启动的时候就创建了所有的非懒加载 bean
            this.bf.registerBeanDefinition(beanID,beanDefinition);
        }
		
	}
	


}
