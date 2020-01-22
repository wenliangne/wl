package com.wenliang.context.cfg;

import com.wenliang.context.annotations.Autowired;
import com.wenliang.context.annotations.Value;
import com.wenliang.context.proxy.ExecutorCommonAspect;
import com.wenliang.context.proxy.ProxyBeanFactory;
import com.wenliang.controller.annotation.Controller;
import com.wenliang.controller.cfg.defaults.DefaultControllerApplicationContext;
import com.wenliang.core.container.DefaultBeanNameMap;
import com.wenliang.core.log.Log;
import com.wenliang.core.util.ClassUtils;
import com.wenliang.core.util.FieldUtils;
import com.wenliang.core.util.StringUtils;
import com.wenliang.mapper.cfg.DefaultRepositoryApplicationContext;
import com.wenliang.mapper.cfg.MapperConfiguration;
import com.wenliang.mapper.sqlsession.SqlSession;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author wenliang
 * @date 2019-08-02
 * 简介：
 */
public class BeanFactory {
    /**
     * 创建bean
     * @param contextConfiguration
     * @return
     */
    public void createBean(ContextConfiguration contextConfiguration) {
        Map<String, Bean> beans = contextConfiguration.getBeans();
        Bean bean = null;
        if (beans == null) {
            Log.INFO("No beans are configured!");
            return;
        } else {
            Log.INFO("Start creating beans!");
        }
        ProxyBeanFactory proxyBeanFactory = new ProxyBeanFactory();
        List<ExecutorCommonAspect> executorCommonAspectList = DefaultBeanApplicationContext.getExecutorCommonAspectList();
        for (Map.Entry<String, Bean> entry : beans.entrySet()) {
            bean = entry.getValue();
            Object domain = null;
            try {
                if (bean.getType().isAssignableFrom(Controller.class)) {
                    domain = DefaultControllerApplicationContext.getBeans().get(bean.getBeanName());
                }else {
                    domain = proxyBeanFactory.createProxyBean(bean.getBeanClass(), executorCommonAspectList);
                    if (domain == null) {
                        domain = bean.getBeanClass().newInstance();
                    }
                }
                DefaultBeanApplicationContext.put(bean.getBeanName(), domain);
            } catch (Exception e) {
                Log.WARN("Failed to create bean for " + bean.getBeanClass().getName(), e);
            }
        }
        createConfigurationClassBean(contextConfiguration);
        initDefaultRepositoryApplicationContext();
        DefaultBeanApplicationContext.putAll(DefaultRepositoryApplicationContext.getContext());
        Log.INFO("End creating beans!");
    }

    private void createConfigurationClassBean(ContextConfiguration contextConfiguration) {
        List<ConfigurationClassBean> configurationClassBeans = contextConfiguration.getConfigurationClassBeans();
        Properties properties = DefaultBeanApplicationContext.getProperties();
        Map context = DefaultBeanApplicationContext.getContext();
        if (configurationClassBeans == null) {
            return;
        }
        for (ConfigurationClassBean configurationClassBean : configurationClassBeans) {
            Object configureBean = configurationClassBean.getConfigureBean();
            //为配置类的value注解注入
            injectValueForConfigurationClass(configureBean,properties);

            List<BeanToMethod> beanToMethods = configurationClassBean.getBeanToMethods();
            if (beanToMethods != null) {
                for (BeanToMethod beanToMethod : beanToMethods) {
                    createBeanToMethod(configureBean,beanToMethod,properties,context);
                }
            }
        }
    }
    private void injectValueForConfigurationClass(Object configureBean,Properties properties) {
        Field[] declaredFields = configureBean.getClass().getDeclaredFields();
        if (declaredFields != null) {
            for (Field declaredField : declaredFields) {
                Value annotation = declaredField.getAnnotation(Value.class);
                if (annotation != null) {
                    String value = StringUtils.convertValueToSymbol(annotation.value(), "#{", "}", properties);
                    declaredField.setAccessible(true);
                    try {
                        declaredField.set(configureBean, value);
                    } catch (IllegalAccessException e) {
                        try {
                            FieldUtils.injectNumber(declaredField,configureBean,value);
                        } catch (IllegalAccessException e1) {
                            Log.ERROR("为配置类"+configureBean.getClass().getName()+"注入："+value+"失败！",e);
                        }
                    }
                }
            }
        }
    }

    private void createBeanToMethod(Object configurationClassDomain, BeanToMethod beanToMethod, Properties properties, Map context) {
        Method method = beanToMethod.getMethod();
        if (context.get(method.getName()) != null) {
            return;
        }
        String[] parameterName = beanToMethod.getParameterName();
        Class<?>[] parameterType = beanToMethod.getParameterType();
        Class<?> resultType = beanToMethod.getResultType();
        if (parameterType != null) {
            Object[] args = new Object[parameterType.length];
            for (int i = 0; i < parameterType.length; i++) {
                if (ClassUtils.isStringAndNumber(parameterType[i])) {
                    String value = properties.getProperty(parameterName[i]);
                    if (value != null) {
                        args[i] = StringUtils.convertValueToNumber(value, parameterType[i]);
                    }
                } else {
                    Object refDomain = context.get(parameterName[i]);
                    if (refDomain != null) {
                        args[i] = refDomain;
                    } else {
                        List<ConfigurationClassBean> configurationClassBeans = DefaultBeanApplicationContext.getContextConfiguration().getConfigurationClassBeans();
                        for (ConfigurationClassBean configurationClassBean : configurationClassBeans) {
                            List<BeanToMethod> beanToMethods = configurationClassBean.getBeanToMethods();
                            if (beanToMethods != null) {
                                for (BeanToMethod toMethod : beanToMethods) {
                                    if (parameterName[i].equals(toMethod.getMethod().getName())) {
                                        createBeanToMethod(configurationClassBean.getConfigureBean(), toMethod, properties, context);
                                        args[i] = context.get(parameterName[i]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            try {
                Object invoke = method.invoke(configurationClassDomain, args);
                context.put(method.getName(), invoke);
                ClassUtils.putValueToInterfaceAndSuper(resultType, method.getName());
            } catch (Exception e) {
                Log.ERROR("创建bean：" + method.getName() + "失败！", e);
            }
        }

    }


    /**
     * 初始化持久层，生成mapper接口代理对象。
     */
    private void initDefaultRepositoryApplicationContext() {
        setDataSource();
        MapperConfiguration mapperConfiguration = DefaultRepositoryApplicationContext.getMapperConfiguration();
        List<Class<?>> interfaceList = mapperConfiguration.getInterfaceList();
        SqlSession sqlSession = DefaultRepositoryApplicationContext.getSqlSession();
        for (Class<?> aClass : interfaceList) {
            sqlSession.getMapper(aClass);
        }
    }

    /**
     * 自定义数据库连接池，此对象必须再mapper代理对象创建前创建，因此需要特殊处理。
     */
    private void setDataSource() {
        DataSource dataSource = (DataSource) DefaultBeanApplicationContext.get("dataSource");
        if (dataSource!=null) {
            Bean bean = DefaultBeanApplicationContext.getContextConfiguration().getBeans().get("dataSource");
            if (bean != null) {
                Map<String, Property> properties = bean.getProperties();
                if (properties != null) {
                    for (Map.Entry<String, Property> entry : properties.entrySet()) {
                        Property property = entry.getValue();
                        Field field = null;
                        try {
                            String beanName = property.getRefBeanName();
                            Object refDomain = null;
                            if (beanName != null) {
                                refDomain = DefaultBeanApplicationContext.get(beanName);
                            } else {
                                refDomain = property.getValue();
                            }
                            try {
                                field = dataSource.getClass().getDeclaredField(property.getFieldName());
                                field.setAccessible(true);
                                field.set(dataSource, refDomain);
                            } catch (Exception e) {
                                String methodName = "set" + StringUtils.convertFirstCharToUpperCase(property.getFieldName());
                                Method method = dataSource.getClass().getDeclaredMethod(methodName, refDomain.getClass());
                                method.invoke(dataSource, refDomain);
                            }
                        } catch (Exception e) {
                            Log.WARN("Dependency injection failure for " + bean.getBeanClass() + "." + property.getFieldName(), e);
                        }
                    }

                }

            }
            DefaultRepositoryApplicationContext.getSqlSession().setDs(dataSource);
        }
    }

    /**
     * 初始化bean（依赖注入）
     * @param contextConfiguration
     */
    public void initBean (ContextConfiguration contextConfiguration){
        Map<String, Bean> beans = contextConfiguration.getBeans();
        if (beans == null) {
            Log.INFO("No beans are configured!");
            return;
        } else {
            Log.INFO("Start dependency injection!");
        }
        for (Map.Entry<String, Bean> entry : beans.entrySet()) {
            Bean bean = entry.getValue();
            Object domain = DefaultBeanApplicationContext.get(bean.getBeanName());
            Map<String, Property> properties = bean.getProperties();
            if (properties == null) {
                continue;
            }
            for (Map.Entry<String, Property> pEntry : properties.entrySet()) {
                Property property = pEntry.getValue();
                Field field = null;
                try {
                    String beanName = property.getRefBeanName();
                    Object refDomain = null;
                    if (beanName != null) {
                        if (property.getType().isAssignableFrom(Autowired.class)) {
                            List<String> beanNameList = DefaultBeanNameMap.get(property.getRefBeanClass().getName());
                            if (beanNameList!=null&&beanNameList.size() == 1) {
                                beanName = beanNameList.get(0);
                            }
                        }
                        refDomain = DefaultBeanApplicationContext.get(beanName);
                    } else {
                        refDomain = property.getValue();
                    }
                    try {
                        if (domain.toString().contains("ByCGLIB")) {
                            field = domain.getClass().getSuperclass().getDeclaredField(property.getFieldName());
                        } else {
                            field = domain.getClass().getDeclaredField(property.getFieldName());
                        }
                        field.setAccessible(true);
                        field.set(domain, refDomain);
                    } catch (Exception e) {
                        try {
                            FieldUtils.injectNumber(field,domain,refDomain);
                        } catch (Exception e1) {
                            try {
                                Method method = domain.getClass().getDeclaredMethod("set" + StringUtils.convertFirstCharToUpperCase(property.getFieldName()), refDomain.getClass());
                                method.invoke(domain, refDomain);
                            } catch (Exception e2) {
                                throw e;
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.WARN("Dependency injection failure for " + bean.getBeanClass() + "." + property.getFieldName(), e);
                }
            }
        }
        Log.INFO("End dependency injection!");
    }

}
