package com.minis.beans.factory.xml;

import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;
import com.minis.beans.factory.support.AbstractBeanFactory;
import com.minis.core.Resource;

/**
 * 将解析好的 xml 转换为 BeanDefinitionReader
 */
public class XmlBeanDefinitionReader {
	AbstractBeanFactory bf;
	public XmlBeanDefinitionReader(AbstractBeanFactory bf) {
		this.bf = bf;
	}
	public void loadBeanDefinitions(Resource res) {
		// 对配置文件中的每个 <bean> 处理
        while (res.hasNext()) {
        	Element element = (Element)res.next();
			// 获取 bean 的基本信息
            String beanID=element.attributeValue("id");
            String beanClassName=element.attributeValue("class");
            String initMethod = element.attributeValue("init-method");

			// 构建 beanDefinition
            BeanDefinition beanDefinition=new BeanDefinition(beanID,beanClassName);
            beanDefinition.setInitMethodName(initMethod);
                    	
        	//get constructor
        	List<Element> constructorElements = element.elements("constructor-arg");       	
        	ConstructorArgumentValues AVS = new ConstructorArgumentValues();
        	for (Element e : constructorElements) {
            	String pType = e.attributeValue("type");
            	String pName = e.attributeValue("name");
            	String pValue = e.attributeValue("value");
        		AVS.addArgumentValue(new ConstructorArgumentValue(pType,pName,pValue));
        	}
    		beanDefinition.setConstructorArgumentValues(AVS);
        	//end of handle constructor
    		
            //handle properties
            List<Element> propertyElements = element.elements("property");
            PropertyValues PVS = new PropertyValues();
            List<String> refs = new ArrayList<>();
            for (Element e : propertyElements) {
            	String pType = e.attributeValue("type");
            	String pName = e.attributeValue("name");
            	String pValue = e.attributeValue("value");
            	String pRef = e.attributeValue("ref");
            	String pV = "";
            	boolean isRef = false;
            	if (pValue != null && !pValue.equals("")) {
            		isRef = false;
            		pV = pValue;
            	} else if (pRef != null && !pRef.equals("")) {
            		isRef = true;
            		pV = pRef;
            		refs.add(pRef);
            	}
            	PVS.addPropertyValue(new PropertyValue(pType, pName, pV, isRef));
            }
        	beanDefinition.setPropertyValues(PVS);
        	String[] refArray = refs.toArray(new String[0]);
        	beanDefinition.setDependsOn(refArray);
        	//end of handle properties

            this.bf.registerBeanDefinition(beanID, beanDefinition);
        }
	}
	


}
