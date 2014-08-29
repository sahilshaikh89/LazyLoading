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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.jasper.JasperException;

import com.lazyloading.tag.form.Header;
import com.lazyloading.tag.intface.LazyLoadingModel;
import com.lazyloading.tag.util.EncDec;
/**
 * 
 * @author Sahil Shaikh
 *
 */
public class LazyLoading extends TagSupport {
	
	public int lazyId;	
	private String modelClass;
	private Integer recordsPerPage;
	public List<Header> headerList = new ArrayList<Header>();
	private Integer paginateDisplayRange;
	private String perPageSelection = "5,10,15";
	private String width = "99%";
	private String height;
	private Boolean stickyHeader = false;
	private String loadingImg; 
	private String id;
	private String cssStyle="";
	private String cssClass="";
	private String idTr = "";
	private String cssStyleTr = "";
	private String cssClassTr = "";	
	private String evalExpression = "";
	private String requestAttr = "";		
	
	
	
	public List<Header> getHeaderList() {
		return headerList;
	}

	public void setHeaderList(List<Header> headerList) {
		this.headerList = headerList;
	}

	public String getRequestAttr() {
		return requestAttr;
	}

	public void setRequestAttr(String requestAttr) {
		this.requestAttr = requestAttr;
	}

	public String getEvalExpression() {
		return evalExpression;
	}

	public void setEvalExpression(String evalExpression) {
		this.evalExpression = evalExpression;
	}
	public String getIdTr() {
		return idTr;
	}

	public void setIdTr(String idTr) {
		this.idTr = idTr;
	}

	public String getCssStyleTr() {
		return cssStyleTr;
	}

	public void setCssStyleTr(String cssStyleTr) {
		this.cssStyleTr = cssStyleTr;
	}

	public String getCssClassTr() {
		return cssClassTr;
	}

	public void setCssClassTr(String cssClassTr) {
		this.cssClassTr = cssClassTr;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	

	public String getCssStyle() {
		return cssStyle;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public String getLoadingImg() {
		return loadingImg;
	}

	public void setLoadingImg(String loadingImg) {
		this.loadingImg = loadingImg;
	}

	public Boolean getStickyHeader() {
		return stickyHeader;
	}

	public void setStickyHeader(Boolean stickyHeader) {
		this.stickyHeader = stickyHeader;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getPerPageSelection() {
		return perPageSelection;
	}

	public void setPerPageSelection(String perPageSelection) {
		this.perPageSelection = perPageSelection;
	}
	public Integer getPaginateDisplayRange() {
		return paginateDisplayRange;
	}

	public void setPaginateDisplayRange(Integer paginateDisplayRange) {
		this.paginateDisplayRange = paginateDisplayRange;
	}

	
	public String getModelClass() {
		return modelClass;
	}

	public void setModelClass(String modelClass) {
		this.modelClass = modelClass;
	}

	public Integer getRecordsPerPage() {
		return recordsPerPage;
	}

	public void setRecordsPerPage(Integer recordsPerPage) {
		this.recordsPerPage = recordsPerPage;
	}

	@Override
	public int doStartTag() throws JspException {
		
		try {
			
			Random randomno = new Random();
			lazyId = randomno.nextInt(20000 - 1);			
			
			String style;
			
			this.validateModelClass();
			this.validateRecordsPerPage();
			this.validatePerPageSelection();
			
			Long[] pageSlectionStr = new Long[this.perPageSelection.split(",").length];
			int counter = 0;
			for(String p : this.perPageSelection.split(",")){
				pageSlectionStr[counter] = new Long(p.trim());
				counter++;
			}
			if(this.height != null){
				style = "style='width:100%; display:block; height: "+ height +"; overflow-y : scroll; overflow-x:scroll; "+ cssStyle +"';";
			}else{ style = "style='width:100%;"+ cssStyle +"'"; }
			this.paginateDisplayRange = (this.paginateDisplayRange == null || paginateDisplayRange == 0) ? 5 : this.paginateDisplayRange;
			
			
			
			lazyId += 1;
			
			headerList = new ArrayList<Header>();
			
			JspWriter out = pageContext.getOut();
			
			StringBuilder sb = new StringBuilder();
			
			//GET NUMBER OF ROWS
			Object modelObject = Class.forName(modelClass).newInstance();	
			Method lazyMethod = modelObject.getClass().getMethod("lazyLoadTotalCount", HttpServletRequest.class, HashMap.class);	
			
			//CONVERT INTO HASHMAP
			HashMap<String, String> requestMap = new HashMap<String, String>();
			try{
				for(String keyVal : this.requestAttr.split(",")){
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
			
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			
			Long noOfRows = (Long) lazyMethod.invoke(modelObject, request,requestMap);
			
			
			sb.append("<div id='ng-app_"+ lazyId + "'>");
			sb.append("<div xmlns:ng='http://angularjs.org' id='ng-app' ng-app='lazyLoading_"+ this.lazyId +"'>");
			sb.append("<div id='"+id+"'  class='panel' style='margin: 3px;width:"+ width +";' ng-controller='LazyLoadingController'>");			
						
			sb.append("<div style='diplay:none'>");		
			sb.append("<input type='hidden' ng-model='$scope.pagination.recordsPerPage' ng-init='pagination.recordsPerPage=\""+ this.recordsPerPage +"\"'/>");
			sb.append("<input type='hidden' ng-model='$scope.lazyTemplate.modelClass' ng-init='lazyTemplate.modelClass=\""+ this.modelClass +"\"'/>");
			sb.append("<input type='hidden' ng-model='$scope.pagination.current' ng-value='pagination.current' ng-init='pagination.current=1'/>");
					
			String encryptedRequestAttr = EncDec.encrypt("L!A@Z#Y$L%O^A*D(I)N_G", this.requestAttr);
						
			sb.append("<input type='hidden' ng-model='$scope.lazyTemplate.requestAttr' ng-init='lazyTemplate.requestAttr=\""+ encryptedRequestAttr +"\"'/>");
						
			sb.append("</div>");
			
			sb.append("<div class='row'>");
			
			sb.append("<div class='small-6 large-3 columns' style='max-width:20%'>");
			//sb.append("<a href='#' data-dropdown='perPageSelection' class='button dropdown'>Records Per Page</a><br>");
			//sb.append("<ul id='perPageSelection' data-dropdown-content class='f-dropdown'>");
			sb.append("<label style='float:left; margin-top: 3%'>Records/Page: </label>"
					+ "<select class='dropdown' style='padding:0px; margin:0px;height:1.9rem; width: 4.5rem' ng-model='changeRecordsPerPage.option' ng-change='updateRecordsPerPage()'>");
			//sb.append("<option value='-1' selected='selected'>Records Per Page</option>");
			for(Long l : pageSlectionStr){
				//sb.append("<li><a href='#'>This is a link</a></li>");
				sb.append("<option value='"+ l.toString() +"'>"+ l.toString() +"</option>");
			}			
			sb.append("</select>");
			sb.append("</div>");
			
			//PAGINATIION
			sb.append("<div class='small-6 large-6 columns' style='max-width: 55%'>");
			sb.append("<div id='paginate_"+ lazyId +"' class='pagination-centered'>");
			sb.append("<div lazy-load-paginate lazy-current-page='1' lazy-total-page='"+ noOfRows +"'  lazy-page-range='"+ this.paginateDisplayRange +"'>");
			
			String template = null;
			try {
				template = this.getScriptsFromFile("pagination.txt");	
				template = template.replace("{lazyId}", String.valueOf(lazyId));
			} catch (Exception e) {
				//e.printStackTrace();
				System.err.println("WARNING: LAZY : Unable to load the pagination template.");
			}
			
			sb.append(template);
			sb.append("</div>");
			
			
			sb.append("</div>"); 
			sb.append("</div>");
			
			sb.append("<div class='small-6 large-3 columns' style='max-width:25%'>");
			sb.append("<input type='text' ng-model='pagination.gotoPage' size='4' style='padding:2px; height: 1.9rem; width: 50%; float: left; margin-bottom:2px' numbers-only />");
			sb.append("&nbsp;&nbsp;&nbsp;"
					+ "<input type='button' style='font-size:13px;padding: 3px;margin-bottom:2px' value='GoTo Page' class='button' ng-disabled='pagination.gotoPage > pagination.last || pagination.gotoPage < 1' ng-click='setCurrent(pagination.gotoPage)' />");
			sb.append("<br />");
			sb.append("<span class='alert-box' style='height:1.2rem; margin:0px' ng-show='pagination.gotoPage > pagination.last || pagination.gotoPage < 1'>Range: 1 to {{pagination.last}}</span>");
			sb.append("</div>");
			sb.append("</div>");
			
			sb.append("<div id='lazy-table_div_"+ lazyId +"' name='lazy-table_div_"+ lazyId +"' style='height:"+ this.height +"'>");
			sb.append("<table "+ style +" id='lazy-table_"+lazyId+"' class='"+ this.cssClass +"'>");
			sb.append("<thead><tr><th style='width:1000px;{{lazyHeaderObj.cssStyleTh}}' class='{{lazyHeaderObj.cssClassTh}}' ng-repeat='lazyHeaderObj in lazyHeadersList'>{{lazyHeaderObj.headerText}}</th></tr></thead>");//</th><th style='display:none'>Lazy Page No</th>\
			sb.append("<tbody>");
			
			//SAMPLE LOADING TR
			// PAGE LOADING			
			sb.append("<tr id='"+ this.idTr +"' style='"+ this.cssStyleTr +"' class='"+ cssClassTr +"' ng-repeat='lazyObj in (filteredResults = (objects|filter:query))' "+ this.evalExpression +">");
			out.println(sb);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return EVAL_BODY_INCLUDE;
	}
	/**
	 * Checks if the page per selection is applied. 
	 * if not specify the default value
	 * @throws JspException
	 */
	private void validatePerPageSelection() throws JspException {
		try {
			this.perPageSelection = (this.perPageSelection == null ? "5,10,15": perPageSelection);
			try {
				String[] pageSlectionStr = this.perPageSelection.split(",");
				for(String p : pageSlectionStr){
					Long l = new Long(p.trim());
				}
			} catch (Exception e) {
			throw new JasperException("provided perPageSelection '" + this.perPageSelection + "' threw Exception when converted to long.");
			}
			
		} catch (Exception e) {
			throw new JspException(e);
		}
		
	}

	public static List<?> lazyLoadData(Long begin, Long end, String modelClass, HttpServletRequest request,HashMap<String, String> requestAttr) throws JspException{
		try {
			
			ArrayList<?> objectList = new ArrayList<Object>();
			Object modelObject = Class.forName(modelClass).newInstance();	
						
			Method lazyMethod = modelObject.getClass().getMethod("lazyLoad", Long.class, Long.class, HttpServletRequest.class, HashMap.class);
			
		    objectList = (ArrayList<?>) lazyMethod.invoke(modelObject, begin, end, request, requestAttr);
		
			return objectList;
		} catch (Exception e) {
			throw new JspException(e);
		}
		
	}

	/**
	 * validates the records per page option.
	 * if not specified default value of 10 will be assigned.
	 * @throws JspException
	 */
	private void validateRecordsPerPage() throws JspException {
		try {
			switch (this.recordsPerPage) {
			case 0:
				this.setRecordsPerPage(10);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			throw new JspException(e);
		}
		
	}

	/**
	 * Validates the model class provided with following checks
	 * 1) class exists 
	 * 2) class extends com.lazyloading.tag.intface.LazyLoadingModel
	 * @throws JspException
	 */
	private void validateModelClass() throws JspException{
		try {
			
			//CHECK IF MODEL CLASS EXISTS
			try{
				Class.forName(modelClass, false, this.getClass().getClassLoader());				
				//IF EXCEPTION IS NOT THROWN THEN CLASS EXISTS
				
				//CHECK IF CLASS IMPLEMENTS LazyLoadingModel
				Class<?> clazz = Class.forName(modelClass, false, this.getClass().getClassLoader());	
				if(!LazyLoadingModel.class.isAssignableFrom(clazz)){
					throw new JspException("Model Class doesn't extend the required com.lazyloading.tag.intface.LazyLoadingModel abstrat class");
				}
				
			}catch(ClassNotFoundException e){
				throw new JspException(e);
			}
			
		} catch (Exception e) {
			throw new JspException(e);
		}
		
	}

	@Override
	public int doAfterBody() throws JspException {
	
		try {
			String headers = "[";
			
			for(Header header : headerList){
				headers += "{\"headerText\": \""+ header.getHeaderText() +"\", \"cssStyleTh\" : \""+ header.getCssStyleTh() +"\","
						+ "\"cssClassTh\" : \""+ header.getCssClassTh() +"\"},";
			}
			headers += "]";
			
			
			JspWriter out = pageContext.getOut();
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("</tr>");			
			sb.append("</tbody></table>");
			
			if(stickyHeader){
				//GET STICKY TEMPLATE
				String template = null;
				try {
					template = this.getScriptsFromFile("StickyHeaderJs.txt");
				} catch (Exception e) {
					//e.printStackTrace();
					System.err.println("WARNING: LAZY : Unable to load the StickHeader template.");
				}
				template = template.replace("{lazyId}", String.valueOf(lazyId)).replace("null", "");
			
				sb.append(template);
			}
			
			//ADD SCRIPTS FOR DATA LOADING NOTIFICATION
			String template = null;
			try {
				template = this.getScriptsFromFile("pleaseWaitJs.txt");
			} catch (Exception e) {
				//e.printStackTrace();
				System.err.println("WARNING: LAZY : Unable to load the pleaseWaitJs template.");
			}
			template = template.replace("{lazyId}", String.valueOf(lazyId)).replace("null", "").replace("{loadingImg}", this.loadingImg);
			sb.append(template);
			sb.append("</div>");
			
			sb.append("<div style=''>");
			sb.append("<input type='hidden' ng-model='$scope.lazyHeadersList' ng-init='lazyHeadersList="+ headers +"' />");
			sb.append("<input type='hidden' ng-model='lazyId' ng-value='$scope.lazyId' ng-init='lazyId="+ lazyId +"' />");
			sb.append("</div>");
			sb.append("</div>");
			sb.append("</div>");
			sb.append("</div>");
		/*	 template = null;
				try {
					template = this.getScriptsFromFile("LazyLoadingJs.txt");
				} catch (Exception e) {
					//e.printStackTrace();
					System.err.println("WARNING: LAZY : Unable to load the LazyLoadingJs template.");
			}
			sb.append(template);
			*/
			
			//CHECK FOR BROWSER SUPPORT
			template = null;
				try {
					template = this.getScriptsFromFile("BrowserSupportJs.txt");
					template = template.replace("{id}", this.id);
					template = template.replace("{lazyId}", String.valueOf(this.lazyId));
					template = template.replace("{height}", this.height);
				} catch (Exception e) {
					//e.printStackTrace();
					System.err.println("WARNING: LAZY : Unable to load the BrowserSupportJs template.");
			}
			sb.append(template);
					
			out.println(sb);
			
			
			try{
				template = "";
				InputStream stream = getClass().getClassLoader().getResourceAsStream("com/lazyloading/tag/templates/LazyLoadingJs.txt");
				InputStreamReader inputReader = new InputStreamReader(stream);
				
				String line = null;
				
				BufferedReader reader = new BufferedReader(inputReader);
				while((line=reader.readLine()) != null){
					template = line;
					template = template.replace("{lazyId}", String.valueOf(this.lazyId));
					template = template.replace("{id}", String.valueOf(this.id));
					out.println(template);
				}
				
			} catch(Exception e) {
				throw new Exception("WARNING: LAZY : Unable to load the LazyLoadingJs template.");		
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.doAfterBody();
	}
	public String getScriptsFromFile(String fileName) throws Exception{
		try{
		String template = "";
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/lazyloading/tag/templates/" + fileName);
		InputStreamReader inputReader = new InputStreamReader(stream);
		
		String line = null;
		
		BufferedReader reader = new BufferedReader(inputReader);
		while((line=reader.readLine()) != null){
			template += line;
		}
		return template;
	} catch(Exception e) {
		throw new Exception(e);		
	}
	}
	
	
}
