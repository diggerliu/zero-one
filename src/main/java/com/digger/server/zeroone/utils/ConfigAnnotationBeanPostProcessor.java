package com.digger.server.zeroone.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.springframework.beans.BeansException;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.digger.server.zeroone.annotation.Config;

@Component
public class ConfigAnnotationBeanPostProcessor extends
     InstantiationAwareBeanPostProcessorAdapter {
  @Autowired 
  private ExtendedPropertyPlaceholderConfigurer propertyConfigurer;

  private SimpleTypeConverter typeConverter = new SimpleTypeConverter();

  @Override
  public boolean postProcessAfterInstantiation(final Object bean, String beanName) 
        throws BeansException {
     ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
        public void doWith(Field field) throws IllegalArgumentException, 
              IllegalAccessException {
        	Config cfg = field.getAnnotation(Config.class);
           if (cfg != null) {
              if (Modifier.isStatic(field.getModifiers())) {
                 throw new IllegalStateException("@Config annotation is not supported  on static fields");
              }
           String key = cfg.name().length() == 0 ? field.getName() : cfg.name();
           Object value = propertyConfigurer.getProperty(key);
           if (value != null) {
              Object _value = typeConverter.convertIfNecessary(value, field.getType());
              ReflectionUtils.makeAccessible(field);
              field.set(bean, _value);
           }
        }
     }
  });

  return true;
  }
}