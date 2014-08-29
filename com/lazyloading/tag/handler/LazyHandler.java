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

package com.lazyloading.tag.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lazyloading.tag.LazyLoading;
import com.lazyloading.tag.form.Template;
import com.lazyloading.tag.util.EncDec;

/**
 * 
 * @author Sahil Shaikh
 *
 */
@Controller
public class LazyHandler {
	/**
	 * Default request handler of the Ajax requests made by the Lazy Loading table
	 * Change the Enc/Dec secret key in production environment
	 * 
	 * @param json    Request parameters which keep track of Lazy Loading table
	 * @param request HttpServletRequest 
	 * @param response HttpServletResponse
	 * @return List<?> List of ? Objects
	 * @throws Exception
	 */
	@RequestMapping(value="/lazyLoading.htm")
	public @ResponseBody List<?> lazyLoading(@RequestBody String json,HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			
			Gson gson = new GsonBuilder().create();
			
			Template template = gson.fromJson(json, Template.class);
			if(template.getGetPage() == null) throw new JspException("Reuested page no cannot be null");

			Long requestedPage = template.getGetPage();
			Long end = new Long(template.getGetPage()) * new Long(template.getRecordsPerPage());
			Long begin = end - new Long(template.getRecordsPerPage());
			
			//DECRYPT THE REQUESTED ATTRS. Change the Secret key in production deployment
			String decryptedReqyestAttr = EncDec.decrypt("L!A@Z#Y$L%O^A*D(I)N_G", template.getRequestAttr());
			
			//CONVERT REUEST ATTRIBUTES IN HASHMAP
			HashMap<String, String> requestMap = new HashMap<String, String>();
			try{
				for(String keyVal : decryptedReqyestAttr.split(",")){
					String[] split = keyVal.split("=");
					if(split.length > 1){
						split[1] = split[1].equals("null")? null : split[1];
						
						requestMap.put(split[0], split[1]);				
					}else{
						requestMap.put(split[0], null);				
					}
					
				}
			}catch (Exception e) {
				throw new JspException("ERROR: lazyLoading 'requestAttrs' should be in attribute=value pair");
			}
			
			
			List<?> requestedList = new ArrayList();
			requestedList = LazyLoading.lazyLoadData(begin, end, template.getModelClass(), request, requestMap);
			
			List<Object> paginatedList = new ArrayList<Object>();
			
			Long indexCounter = 0L;
			try {
				
				for(Object obj : requestedList){

					Method lazyMethod = obj.getClass().getMethod("setLazyPageNo", Long.class);
					lazyMethod.invoke(obj, new Long(template.getGetPage()));	
					
					
					//Searchable page numbers helps Angular Filter to paginate records
					//records are coupled with commas to differentiate between 1, 11, 2, 22, 23 ... so on
					lazyMethod = obj.getClass().getMethod("setSearchablePageNo", String.class);
					lazyMethod.invoke(obj, "," + template.getGetPage() + ",");
					
					//CALCLCULATE INDEX
					Long index = (template.getGetPage() - 1) * new Long(template.getRecordsPerPage()) + indexCounter;
					indexCounter++;
					
					lazyMethod = obj.getClass().getMethod("set$index", Long.class);
					lazyMethod.invoke(obj, index);				
									
					paginatedList.add(obj);


				}
				
			} catch (NullPointerException e) {
				throw new JspException("ERROR: Lazy loading. The load method returned null as a list.");
			}
		


			return (paginatedList) ;



		} catch (Exception e) {
			throw new Exception(e);
		}		
	}

}
