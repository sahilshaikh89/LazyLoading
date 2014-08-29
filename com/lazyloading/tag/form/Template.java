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

package com.lazyloading.tag.form;

/**
 * @author Sahil Shaikh
 *
 */
public class Template {
	private Long getPage;
	
	private String modelClass;
	private Long currentPage;
	private Long recordsPerPage;
	private String requestAttr;
	
	
	public String getRequestAttr() {
		return requestAttr;
	}

	public void setRequestAttr(String requestAttr) {
		this.requestAttr = requestAttr;
	}
	
	public Long getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Long currentPage) {
		this.currentPage = currentPage;
	}

	public Long getRecordsPerPage() {
		return recordsPerPage;
	}

	public void setRecordsPerPage(Long recordsPerPage) {
		this.recordsPerPage = recordsPerPage;
	}

	public String getModelClass() {
		return modelClass;
	}

	public void setModelClass(String modelClass) {
		this.modelClass = modelClass;
	}

	public Long getGetPage() {
		return getPage;
	}

	public void setGetPage(Long getPage) {
		this.getPage = getPage;
	}
	
}
