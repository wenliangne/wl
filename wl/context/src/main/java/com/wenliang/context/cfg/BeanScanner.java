package com.wenliang.context.cfg;

import com.wenliang.context.annotations.*;
import com.wenliang.controller.annotation.Controller;
import com.wenliang.core.log.Log;
import com.wenliang.core.util.ClassUtils;
import com.wenliang.core.util.StringUtils;
import org.dom4j.Element;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wenliang
 * @date 2019-08-02
 * 简介：
 */
public class BeanScanner {
    public void scan(Element root) {
        loadBeanXml(root);
    }

    public void scan(String[] packageNames) {
        loadBeanAnnotation(packageNames);
    }

    /**
     * 加载配置文件
     * @param root
     */
    private void loadBeanXml(Element root) {
        Element beans = null;
        Bean bean = null;
        Property property = null;
        try {
            beans = (Element) root.selectNodes("beans").get(0);
        } catch (Exception e) {
            Log.WARN("配置文件中未找到beans标签！",e);
            return;
        }
        List<Element> beanList = beans.selectNodes("bean");
        for (Element element : beanList) {
            bean = new Bean();
            String beanName = element.attributeValue("id");
            bean.setBeanName(beanName);
            bean.setType(Component.class);
            String className = element.attributeValue("value");
            try {
                bean.setBeanClass(Class.forName(className));
            } catch (Exception e) {
                Log.ERROR("class cannot be found！");
                throw new RuntimeException(e);
            }
            List<Element> propertyList = element.selectNodes("property");
            for (Element element1 : propertyList) {
                property = new Property();
                String fieldName = element1.attributeValue("name");
                property.setFieldName(fieldName);
                String value = element1.attributeValue("value");
                if (value != null) {
                    property.setValue(value);
                    property.setType(Value.class);
                }
                String refBeanName = element1.attributeValue("ref");
                if (refBeanName != null) {
                    property.setRefBeanName(refBeanName);
                    property.setType(Resource.class);
                }
                bean.addProperty(property);
            }
            DefaultBeanApplicationContext.getContextConfiguration().addBean(bean);
        }

    }


    /**
     * 加载注解
     * @param  packageNames
     */
    private void loadBeanAnnotation(String[] packageNames) {
            Set<Class<?>> components = ClassUtils.getClassWithAnnotation(packageNames,Component.class);
            Set<Class<?>> controllers = ClassUtils.getClassWithAnnotation(packageNames,Controller.class);
            Set<Class<?>> services = ClassUtils.getClassWithAnnotation(packageNames,Service.class);
            Set<Class<?>> repositorys = ClassUtils.getClassWithAnnotation(packageNames,Repository.class);
            geneBeanToConfiguration(controllers);
            geneBeanToConfiguration(components);
            geneBeanToConfiguration(services);
            geneBeanToConfiguration(repositorys);
    }

    /**
     * 遍历集合中的class文件生成bean
     * @param components
     */
    public void geneBeanToConfiguration(Set<Class<?>> components) {
        Bean bean = null;
        Property property = null;
        for (Class<?> component : components) {
            if (component.isInterface()) {
                continue;
            }
            bean = new Bean();
            bean.setBeanClass(component);
            String beanName = getComponentValue(component);
            if ("".equals(beanName)) {
                beanName = StringUtils.convertFirstCharToLowerCase(component.getSimpleName());
            }
            bean.setBeanName(beanName);
            bean.setType(getComponentAnnotationClass(component));

            Set<Field> resources = getFieldToAnnotation(component,Resource.class);
            for (Field resource : resources) {
                property = new Property();
                property.setFieldName(resource.getName());
                String refBeanName = resource.getAnnotationsByType(Resource.class)[0].value();
                if ("".equals(refBeanName)) {
                    refBeanName = resource.getName();
                }
                property.setRefBeanName(refBeanName);
                property.setType(Resource.class);
                bean.addProperty(property);
            }

            Set<Field> autowireds = getFieldToAnnotation(component,Autowired.class);
            for (Field autowired : autowireds) {
                property = new Property();
                property.setFieldName(autowired.getName());
                property.setRefBeanName(autowired.getName());
                property.setRefBeanClass(autowired.getType());
                property.setType(Autowired.class);
                bean.addProperty(property);
            }
            Set<Field> values = getFieldToAnnotation(component,Value.class);
            for (Field value : values) {
                property = new Property();
                property.setFieldName(value.getName());
                property.setValue(value.getAnnotationsByType(Value.class)[0].value());
                property.setType(Value.class);
                bean.addProperty(property);
            }
            DefaultBeanApplicationContext.getContextConfiguration().addBean(bean);
            ClassUtils.putValueToInterfaceAndSuper(bean.getBeanClass(),bean.getBeanName());

        }
    }

    /**
     * 获取容器中的值
     * @param component
     * @return
     */
    private String getComponentValue(Class<?> component) {
        String tempStr = "";
        if (component.isAnnotationPresent(Controller.class)) {
            tempStr = component.getAnnotationsByType(Controller.class)[0].value();
        } else if (component.isAnnotationPresent(Service.class)) {
            tempStr = component.getAnnotationsByType(Service.class)[0].value();
        } else if (component.isAnnotationPresent(Repository.class)) {
            tempStr = component.getAnnotationsByType(Repository.class)[0].value();
        } else if (component.isAnnotationPresent(Component.class)) {
            tempStr = component.getAnnotationsByType(Component.class)[0].value();
        }
        return tempStr;
    }

    /**
     * 获取bean的容器类型的注解
     * @param component
     * @return
     */
    private Class<?> getComponentAnnotationClass(Class<?> component) {
        if (component.isAnnotationPresent(Controller.class)) {
            return Controller.class;
        } else if (component.isAnnotationPresent(Service.class)) {
            return Service.class;
        } else if (component.isAnnotationPresent(Repository.class)) {
            return Repository.class;
        } else if (component.isAnnotationPresent(Component.class)) {
            return Component.class;
        }
        return null;
    }

    /**
     * 获取带有某个注解的字段
     * @param component
     * @param annotation
     * @return
     */
    private Set<Field> getFieldToAnnotation(Class<?> component,Class<? extends Annotation> annotation) {
        Field[] fields = component.getDeclaredFields();
        HashSet<Field> f = new HashSet<>();
        for (Field field : fields) {
            Annotation a = field.getAnnotation(annotation);
            if (a != null) {
                f.add(field);
            }
        }
        return f;
    }

}
