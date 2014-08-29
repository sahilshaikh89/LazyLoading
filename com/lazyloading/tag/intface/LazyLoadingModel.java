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

package com.lazyloading.tag.intface;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
/**
 * 
 * @author Sahil Shaikh
 *
 */
public abstract class LazyLoadingModel {
	protected Long lazyPageNo = 1L;
	private String searchablePageNo;
	private Long $index;
	
	
	public Long get$index() {
		return $index;
	}
	public void set$index(Long $index) {
		this.$index = $index;
	}
	public String getSearchablePageNo() {
		return searchablePageNo;
	}
	public void setSearchablePageNo(String searchablePageNo) {
		this.searchablePageNo = searchablePageNo;
	}
	/**
	 * Extension of this method in modelClass wil be called on per page loading by
	 * the Lazy Handler
	 * 
	 * 
	 * @param begin Start Record limit
	 * @param end   End record Limit
	 * @param request HttpServletRequest
	 * @param requestAttr Map of the comma separated Attr=Value pair applied in requestAttr
	 * 			request attributed specified are encrypted and so not easiy be tampered
	 * @return List<?>  List of ? Objects
	 * @throws Exception
	 */
	public abstract List<?> lazyLoad(Long begin, Long end, HttpServletRequest request, HashMap<String, String> requestAttr) throws Exception;
	
	/**
	 * Extension of this method will be called during page load only
	 * to count the number of records which can be loaded  lazilly or on demand.
	 * 
	 * @param request HttpServletRequest
	 * @param requestAttr Map of the comma separated Attr=Value pair applied in requestAttr
	 * 			request attributed specified are encrypted and so not easiy be tampered
	 * @return Long total number of records
	 * @throws Exception
	 */
	public abstract Long lazyLoadTotalCount(HttpServletRequest request, HashMap<String, String> requestAttr) throws Exception;

	public Long getLazyPageNo() {
		return lazyPageNo;
	}

	public void setLazyPageNo(Long lazyPageNo) {
		this.lazyPageNo = lazyPageNo;
	}
	
	/**
	 * This method need not tobe overriden as this is required by 
	 * lazy validator to check if attribute exists in model class.
	 * 
	 * @param fieldName  Name of the attribut in model class
	 * @throws JspException
	 */
	public void checkIfFieldExists(String fieldName) throws JspException {
	try {
			
			//CHECK IF THE FIELD NAME BELONGS TO THE MODEL CLASS SPECIFIED
			try{
				
				Field f = this.getClass().getDeclaredField(fieldName);
				
				
			}catch(NoSuchFieldException e){
				throw new JspException("No attribute named \"" + fieldName + "\" exists for Model Class " + this.getClass().getName());
			}
					
		} catch (Exception e) {
			throw new JspException(e);
		}
		
	}
	
	
}
