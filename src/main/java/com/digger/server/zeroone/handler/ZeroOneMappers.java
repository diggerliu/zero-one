package com.digger.server.zeroone.handler;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digger.server.zeroone.ZeroOne;
import com.digger.server.zeroone.annotation.Commond;
import com.digger.server.zeroone.annotation.Module;
import com.digger.server.zeroone.utils.StringUtils;


@Module
@Component
public class ZeroOneMappers {

	@Autowired
	private ZeroOne zeroOne;
    private static final Logger log = LoggerFactory.getLogger(ZeroOneMappers.class);
    
    public Map<String, Meta> mappers = new HashMap<String, ZeroOneMappers.Meta>();
    
    
    public void init(){
    	Map<String,Object> commondMappers = zeroOne.context.getBeansWithAnnotation(Module.class);
    	for(Entry<String,Object> e : commondMappers.entrySet()){
    		Object obj = e.getValue();
    		Class<?> clazz = obj.getClass();
    		Module module = clazz.getAnnotation(Module.class);
    		String moduleName = module.name();
    		if(StringUtils.isEmpty(moduleName)){
    			moduleName = "/"+clazz.getCanonicalName();
    		}
    		Method[] methods = clazz.getMethods();
    		for(Method m :methods){
    			Commond comAnno = m.getAnnotation(Commond.class);
    			if(comAnno!=null){
    				String commondName = comAnno.name();
    				if(StringUtils.isEmpty(commondName)){
    					commondName = "/"+m.getName();
    				}
    				Meta commond = new Meta(obj,m,commondName,moduleName);
    				mappers.put(moduleName+commondName, commond);
    				System.out.println(commond);
    			}
    		}
    	}
    }
    
    
    @Commond(name="/say")
    public void say(){
    	System.out.println("hello world!");
    }
    
    public static class Meta {
    	public Object commond;
    	public Method method;
    	public String commondName;
    	public String moduleName;
		public Meta(Object commond, Method method, String commondName,String moduleName) {
			this.commond = commond;
			this.method = method;
			this.commondName = commondName;
			this.moduleName = moduleName;
		}
		@Override
		public String toString() {
			return moduleName+commondName;
		}
    	
    }
}
