package com.wenliang.mapper.utils;

import com.wenliang.core.io.Resources;
import com.wenliang.core.log.Log;
import com.wenliang.mapper.annotations.Delete;
import com.wenliang.mapper.annotations.Insert;
import com.wenliang.mapper.annotations.Select;
import com.wenliang.mapper.annotations.Update;
import com.wenliang.mapper.cfg.DefaultRepositoryApplicationContext;
import com.wenliang.mapper.cfg.MapperConfiguration;
import com.wenliang.mapper.cfg.Mapper;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：用于扫描配置文件或注解
 */
public class XMLConfigBuilder {

    /**
     * 解析主配置文件，把里面的内容填充到SqlSession所需要的地方
     * xml解析使用的技术：dom4j+xpath
     */
    public static MapperConfiguration loadConfiguration(InputStream inputStream){
        try{
            // 定义配置对象，用于保存数据库连接信息和mapper
            MapperConfiguration cfg = DefaultRepositoryApplicationContext.getMapperConfiguration();
            //1.获取SAXReader对象用于操作xml
            SAXReader reader = new SAXReader();
            //2.根据字节输入流获取Document对象
            Document document = reader.read(inputStream);
            //3.获取根节点
            Element root = document.getRootElement();
            //4.使用xpath中选择指定节点的方式，获取所有property节点（不加//代表从当前节点的子节点中查找，加//代表从当前节点的后代节点中查找）
            List<Element> propertyElements = root.selectNodes("//property");
            //5.遍历节点
            for(Element propertyElement : propertyElements){
                //判断节点是连接数据库的哪部分信息
                //取出name属性的值
                String name = propertyElement.attributeValue("name");
                if("driver".equals(name)){
                    //表示驱动
                    String driver = propertyElement.attributeValue("value");
                    cfg.setDriver(driver);
                }
                if("url".equals(name)){
                    //表示连接字符串
                    String url = propertyElement.attributeValue("value");
                    cfg.setUrl(url);
                }
                if("username".equals(name)){
                    //表示用户名
                    String username = propertyElement.attributeValue("value");
                    cfg.setUsername(username);
                }
                if("password".equals(name)){
                    //表示密码
                    String password = propertyElement.attributeValue("value");
                    cfg.setPassword(password);
                }
            }
            //获取environment标签，设置使用的数据库类型
            List<Element> environmentElements = root.selectNodes("//environment");
            if (environmentElements.size() > 0) {
                Element element = environmentElements.get(0);
                String id = element.attributeValue("id");
                cfg.setDatabaseType(id);
            }
            // 获取dataSource标签，判断是否使用连接词
            List<Element> dataSourceElements = root.selectNodes("//dataSource");
            if (dataSourceElements.size() > 0) {
                Element element = dataSourceElements.get(0);
                String type = element.attributeValue("type");
                if ("POOLED".equals(type)) {
                    cfg.setPooled(true);
                    DataSourceUtil.init(cfg);
                } else {
                    cfg.setPooled(false);
                }
            }

            // 获取package标签
            List<Element> packageElements = root.selectNodes("//package");
            //没有package取出mappers中的所有mapper标签，判断他们使用了resource还是class属性
            List<Element> mapperElements = root.selectNodes("//mappers/mapper");
            if (mapperElements.size() > 0) {
                //遍历集合
                for (Element mapperElement : mapperElements) {
                    //判断mapperElement使用的是哪个属性
                    Attribute attribute = mapperElement.attribute("resource");
                    if (attribute != null) {
                        //表示有resource属性，用的是XML
                        Log.INFO("Configure mapper with XML!");
                        //取出属性的值
                        String mapperPath = attribute.getValue();//获取属性的值"com.wenliang.test.dao.UserDao.xml"
                        //把映射配置文件的内容获取出来，封装成一个map
                        loadMapperConfiguration(mapperPath, cfg);
                    } else {
                        //表示没有resource属性，用的是注解
                        Log.INFO("Configure mapper with annotations!");
                        //获取class属性的值
                        String daoClassPath = mapperElement.attributeValue("class");
                        //根据daoClassPath获取封装的必要信息
                        loadMapperAnnotation(daoClassPath, cfg);
                    }
                }
            }
            if (packageElements.size() > 0) {
                for (Element packageElement : packageElements) {
                    String packageName = packageElement.attributeValue("name");
                    if (packageName == null || "".equals(packageName)) {
                        packageName=packageElement.attributeValue("value");
                    }
                    if (packageName == null || "".equals(packageName)) {
                        packageName = packageElement.getText();
                    }
                    loadMapperFile(packageName, cfg);
                }
            } else {
                String[] mapperScans = DefaultRepositoryApplicationContext.getMapperConfiguration().getMapperScan();
                for (int i = 0; i < mapperScans.length; i++) {
                    loadMapperFile(mapperScans[i], cfg);
                }
            }
            //返回Configuration
            return cfg;
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally{
            try {
                inputStream.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    private static void loadMapperFile(String packageName,MapperConfiguration cfg) throws Exception {
        String[] list = new File(XMLConfigBuilder.class.getResource("/").getPath() + packageName.replace(".", "/")).list();
        String[] xmls = getXMLFromArray(list);
        if (xmls.length > 0) {
            Log.INFO("Configure mapper with XML!");
            for (int i = 0; i < xmls.length; i++) {
                //把映射配置文件的内容获取出来，封装成一个map
                loadMapperConfiguration(packageName.replace(".","/")+"/"+xmls[i],cfg);
            }
        } else {
            Log.INFO("Configure mapper with annotations!");
            String[] classs = getClassFromArray(list);
            for (int i = 0; i < classs.length; i++) {
                //把映射配置文件的内容获取出来，封装成一个map
                String classFileName = packageName + "." + classs[i];
                loadMapperAnnotation(classFileName.substring(0,classFileName.length()-6),cfg);
            }
        }
    }

    /**
     * 根据传入的参数，解析XML，并且封装到Map中
     * @param mapperPath    映射配置文件的位置
     * @return  map中包含了获取的唯一标识（key是由dao的全限定类名和方法名组成）
     *          以及执行所需的必要信息（value是一个Mapper对象，里面存放的是执行的SQL语句和一些执行的必要信息）
     */
    private static void loadMapperConfiguration(String mapperPath,MapperConfiguration cfg)throws IOException {
        InputStream in = null;
        try{
            //定义返回值对象
            Map<String,Mapper> mappers = new HashMap<String,Mapper>();
            //1.根据路径获取字节输入流
            in = Resources.getResourceAsStream(mapperPath);
            //2.根据字节输入流获取Document对象
            SAXReader reader = new SAXReader();
            Document document = reader.read(in);
            //3.获取根节点
            Element root = document.getRootElement();
            //4.获取根节点的namespace属性取值
            String namespace = root.attributeValue("namespace");//是组成map中key的部分
            //5.获取所有的相关语句节点
            List<Element> elements = root.selectNodes("//select");
            elements.addAll(root.selectNodes("//update"));
            elements.addAll(root.selectNodes("//delete"));
            elements.addAll(root.selectNodes("//insert"));
            //6.遍历select节点集合
            for(Element element : elements){
                Mapper mapper = processConfiguration(element);
                //取出id属性的值      组成map中key的部分
                String id = element.attributeValue("id");
                //创建Key
                String key = namespace+"."+id;
                //把key和value存入mappers中
                mappers.put(key,mapper);
            }
            Class<?> daoClass = Class.forName(mapperPath.substring(0, mapperPath.length()-4).replace("/","."));
            if (daoClass.isInterface()) {
                cfg.addInterface(daoClass);
                cfg.setMappers(mappers);
            } else {
                Log.WARN("配置"+mapperPath+"失败："+mapperPath+"必须在同路径下存在一个与之对应的接口！");
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally{
            in.close();
        }
    }

    /**
     * 根据传入的参数，得到mapper中所有被指定注解标注的方法。
     * 根据方法名称和类名以及方法上注解，组成Mapper的必要信息
     * @param daoClassPath
     * @return
     */
    private static void loadMapperAnnotation(String daoClassPath,MapperConfiguration cfg)throws Exception{
        //定义返回值对象
        Map<String,Mapper> mappers = new HashMap<String, Mapper>();
        String resultType=null;

        //1.得到dao接口的字节码对象
        Class daoClass = Class.forName(daoClassPath);
        //2.得到dao接口中的方法数组
        Method[] methods = daoClass.getMethods();
        //3.遍历Method数组
        for(Method method : methods){
            //取出每一个方法，判断是否有select注解
                Mapper mapper = processAnnotation(method);
                //取出注解的value属性值
            if (mapper != null) {
                //获取当前方法的返回值，还要求必须带有泛型信息
                Type type = method.getGenericReturnType();//List<User>}
                //判断type是不是参数化的类型
                if (type instanceof ParameterizedType) {
                    //强转
                    ParameterizedType ptype = (ParameterizedType) type;
                    //得到参数化类型中的实际类型参数
                    Type[] types = ptype.getActualTypeArguments();
                    //取出第一个
                    Class domainClass = (Class) types[0];
                    //获取domainClass的类名
                    resultType = domainClass.getName();
                } else {
                    resultType = method.getGenericReturnType().getTypeName();
                }
                //给Mapper赋值
                mapper.setResultType(resultType);
                //获取参数信息
                Class<?>[] parameterTypes = method.getParameterTypes();
                String[] parameterTypesString = new String[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    parameterTypesString[i] = parameterTypes[i].getName();
                }
                mapper.setParameterTypes(parameterTypesString);

                //组装key的信息
                //获取方法的名称
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String key = className + "." + methodName;
                //给map赋值
                mappers.put(key, mapper);
            }
        }
        if (daoClass.isInterface()) {
            cfg.addInterface(daoClass);
            cfg.setMappers(mappers);
        } else {
            Log.WARN("配置"+daoClassPath+"失败："+daoClassPath+"必须是一个接口！");
        }
    }

    /**
     * 将方法中指定注解放入mapper保存
     * @param method
     * @return
     */
    private static Mapper processAnnotation(Method method) {
        Mapper mapper = new Mapper();
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (Select.class.isAssignableFrom(annotation.getClass())) {
                Select annotationObject = method.getAnnotation(Select.class);
                mapper.setSqlString(annotationObject.value());
                mapper.setAnnotation(Select.class);
                return mapper;
            } else if (Update.class.isAssignableFrom(annotation.getClass())) {
                Update annotationObject = method.getAnnotation(Update.class);
                mapper.setSqlString(annotationObject.value());
                mapper.setAnnotation(Update.class);
                return mapper;
            } else if (Insert.class.isAssignableFrom(annotation.getClass())) {
                Insert annotationObject = method.getAnnotation(Insert.class);
                mapper.setSqlString(annotationObject.value());
                mapper.setAnnotation(Insert.class);
                return mapper;
            } else if (Delete.class.isAssignableFrom(annotation.getClass())) {
                Delete annotationObject = method.getAnnotation(Delete.class);
                mapper.setSqlString(annotationObject.value());
                mapper.setAnnotation(Delete.class);
                return mapper;
            }
        }
        return null;
    }

    /**
     * 将xml中指定标签语句放入mapper保存
     * @param element
     * @return
     */
    private static Mapper processConfiguration(Element element) {
        Mapper mapper = new Mapper();
        // 取出代表注解的标签名
        if ("select".equals(element.getName())) {
            mapper.setAnnotation(Select.class);
        } else if ("update".equals(element.getName())) {
            mapper.setAnnotation(Update.class);
        } else if ("delete".equals(element.getName())) {
            mapper.setAnnotation(Delete.class);
        } else if ("insert".equals(element.getName())) {
            mapper.setAnnotation(Insert.class);
        }
        //取出resultType属性的值
        String resultType = element.attributeValue("resultType");
        mapper.setResultType(resultType);
        //取出文本内容
        String sqlString = element.getText();
        mapper.setSqlString(sqlString.trim());
        //取出resultType属性的值
        String parameterType = element.attributeValue("parameterType");
        mapper.setParameterTypes(new String[]{parameterType});
        return mapper;
    }

    /**
     * 判断数组中是否存在xml文件
     * @param list
     * @return
     */
    private static boolean isExistXML(String[] list) {
        for (String s : list) {
            if (".xml".equals(s.substring(s.length()-4).toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取数组中的xml文件
     * @param list
     * @return
     */
    private static String[] getXMLFromArray(String[] list) {
        ArrayList<String> xmls = new ArrayList<>();
        for (String s : list) {
            if (".xml".equals(s.substring(s.length()-4).toLowerCase())) {
                xmls.add(s);
            }
        }
        return xmls.toArray(new String[0]);
    }
    /**
     * 获取数组中的java文件
     * @param list
     * @return
     */
    private static String[] getClassFromArray(String[] list) {
        ArrayList<String> classs = new ArrayList<>();
        for (String s : list) {
            if (".class".equals(s.substring(s.length()-6).toLowerCase())) {
                classs.add(s);
            }
        }
        return classs.toArray(new String[0]);
    }

}
