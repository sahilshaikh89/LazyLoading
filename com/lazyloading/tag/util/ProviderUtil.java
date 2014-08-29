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

package com.lazyloading.tag.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.lazyloading.tag.context.LazyApplicationContextAware;

/**
 *  
 * 
 * @author Sahil Shaikh
 *
 */

@Component
public class ProviderUtil {
 
    @Autowired
    private ApplicationContext ctx;
 
    /**
     * Util function to get the object created in SpringBeanFactory
     * This can largely be used to obtain objects of DataAccess Layer 
     * 
     * 
     * @param claz   Singleton class whose object needs t0 be fetced from SpringBeanFactory
     * @return <T> Instance of the class
     * @throws UnsupportedOperationException
     * @throws BeansException
     */
    public <T> T getBeanByType(final Class<T> claz) throws UnsupportedOperationException, BeansException {
    	ctx = LazyApplicationContextAware.getApplicationContext();
    	
        Map beansOfType = ctx.getBeansOfType(claz);
        final int size = beansOfType.size();
        switch (size) {
            case 0:
                throw new UnsupportedOperationException("No bean found of type" + claz);
            case 1:
                String name = (String) beansOfType.keySet().iterator().next();
                return claz.cast(ctx.getBean(name, claz));
            default:
                throw new UnsupportedOperationException("Ambigious beans found of type" + claz);
        }
    }
}
