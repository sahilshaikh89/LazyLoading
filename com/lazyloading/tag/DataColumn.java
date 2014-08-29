/*
 * Copyright 2014 the original author or authors.
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.lazyloading.tag;

import java.awt.List;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.jasper.JasperException;

import com.lazyloading.tag.form.Header;


/**
 * @author Sahil Shaikh
 *
 */
public class DataColumn extends BodyTagSupport {

	private String attributeName;
	private String headerText;
	private String evalExpression = "";
	private String cssStyleTd = "";
	private String cssClassTd = "";
	private String id = "";
	private String cssClassTh = "";
	private String cssStyleTh = "";
	private boolean isCollection = false;	
	
	public String getCssClassTh() {
		return cssClassTh;
	}
	public void setCssClassTh(String cssClassTh) {
		this.cssClassTh = cssClassTh;
	}
	public String getCssStyleTh() {
		return cssStyleTh;
	}
	public void setCssStyleTh(String cssStyleTh) {
		this.cssStyleTh = cssStyleTh;
	}
	public String getCssStyleTd() {
		return cssStyleTd;
	}
	public void setCssStyleTd(String cssStyleTd) {
		this.cssStyleTd = cssStyleTd;
	}
	public String getCssClassTd() {
		return cssClassTd;
	}
	public void setCssClassTd(String cssClassTd) {
		this.cssClassTd = cssClassTd;
	}
	public String getEvalExpression() {
		return evalExpression;
	}
	public void setEvalExpression(String evalExpression) {
		this.evalExpression = evalExpression;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getHeaderText() {
		return headerText;
	}
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@SuppressWarnings("deprecation")
	@Override
	public int doStartTag() throws JspException {
		
		try {
			 isCollection = false;
			LazyLoading l = (LazyLoading) TagSupport.findAncestorWithClass(this, LazyLoading.class);
			
			this.validateAttributeName(l.getModelClass());
				
			Header header = new Header();
			header.setHeaderText(this.headerText);
			header.setCssClassTh(this.cssClassTh);
			header.setCssStyleTh(this.cssStyleTh);
			
			l.headerList.add(header);
			
			JspWriter out = pageContext.getOut();			
			StringBuilder sb = new StringBuilder();			
			sb.append("<td "+ evalExpression +" style='"+ cssStyleTd +"' class='"+ cssClassTd +"' id="+ this.id +">" + 
					(!isCollection?"{{lazyObj."+ this.attributeName+"}}":""));
			
			out.println(sb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return EVAL_BODY_INCLUDE;
	}
	
	
	 /**
     * Validated whether the attribute specified in tag exists in the class
     *
     * @param String	represents Lazy Class
     * @return void 
     */
	private void validateAttributeName(String modelClass) throws JspException, NoSuchFieldException{
		try {
			if(this.attributeName == "" || this.attributeName.length() == 0){
				return;
			}
			
			/*Object modelObject = Class.forName(modelClass).newInstance();				
			
			Method lazyMethod = modelObject.getClass().getMethod("checkIfFieldExists", String.class);
			
			lazyMethod.invoke(modelObject, this.attributeName);*/
			
			String methodName = "";	
			String className = modelClass;
			Method lazyMethod = null;
			try{
					String attributeName = this.attributeName;
		
					Object modelObject = Class.forName(modelClass).newInstance();			
					
					
					methodName = "";	
					className = modelClass;
					
					String[] attrArray = attributeName.split("\\.");
				
					for(String attribute : attrArray){
												
						modelObject = Class.forName(className).newInstance();	
						
						try {
							//CHECK FOR STRING OR OTHER FIELDS
							methodName = "get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1, attribute.length());
							lazyMethod = modelObject.getClass().getMethod(methodName);
							
						} catch (Exception e) {
							try {
								//CHECK FOR BOOLEAN FIELD
								methodName = "is" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1, attribute.length());
								lazyMethod = modelObject.getClass().getMethod(methodName);
							} catch (Exception e2) {
								throw new JspException("Attribute getter/setters " + lazyMethod + " does not exists for class " + className);
							}
						}	
						//System.out.println("CLASS NAME :: " + lazyMethod.getGenericReturnType().toString().split(" ")[1]);
						
						if(lazyMethod.getGenericReturnType()== null){
							throw new JspException("Attribute getter/setters " + lazyMethod + " does not exists for class " + className);
						}
						else{
							
							int length = lazyMethod.getGenericReturnType().toString().split(" ").length;
							if(lazyMethod.getGenericReturnType().toString().split(" ").length > 0){
								className = lazyMethod.getGenericReturnType().toString().split(" ")[length - 1];
								
								
								if(className.indexOf("<") > 0){
									
									//CHECK FOR COLLECTION
									String collectionName = className.substring(0, className.indexOf("<"));
									if(collectionName.contains("java.util.")) isCollection = true;
																		
									className = className.substring(className.indexOf("<") + 1, className.indexOf(">"));									
								}
							}
						}
						
						//modelObject = lazyMethod.invoke(modelObject).getClass().newInstance();
						//System.out.println("::" + modelObject.getClass());
					}
			}catch(Exception e){
				throw new JspException("Attribute getter/setters " + lazyMethod + " does not exists for class " + className);
			}
			
					
		} catch (Exception e) {
			throw new JspException(e);
		}
		
	}
	@Override
	public int doAfterBody() throws JspException {
		
		//LazyLoading l = (LazyLoading) TagSupport.findAncestorWithClass(this, LazyLoading.class);
			
		JspWriter out = pageContext.getOut();			
		StringBuilder sb = new StringBuilder();			
		sb.append("</td>");
		
		try {
			out.println(sb);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//l.headerList.add(this.headerText);
		
		return super.doAfterBody();
	}
}
