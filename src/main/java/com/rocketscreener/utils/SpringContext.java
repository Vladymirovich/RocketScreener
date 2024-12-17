package com.rocketscreener.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/*
  SpringContext:
  - Provides static access to Spring Beans if needed.
  - Used by CompositeFilterEvaluator to get FilterService.
  - No placeholders, fully functional.
*/

@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        context = ac;
    }

    public static <T> T getBean(Class<T> clazz){
        return context.getBean(clazz);
    }
}
