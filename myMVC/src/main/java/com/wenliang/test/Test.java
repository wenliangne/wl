package com.wenliang.test;



import com.wenliang.context.proxy.AspectMatcher;
import com.wenliang.context.proxy.CglibProxy;
import com.wenliang.context.proxy.ExecutorAspect;
import com.wenliang.core.io.Resources;
import com.wenliang.core.javassist.MethodParameterNamesScannerForJavassist;
import com.wenliang.core.log.Log;
import com.wenliang.core.util.MethodUtils;
import com.wenliang.core.util.StringUtils;
import com.wenliang.mapper.sqlsession.SqlSession;
import com.wenliang.mapper.sqlsession.SqlSessionFactory;

import com.wenliang.mapper.sqlsession.defaults.DefaultSqlSessionFactoryBuilder;
import com.wenliang.test.controller.UserController;
import com.wenliang.test.dao.UserDao;
import com.wenliang.test.domain.User;
import com.wenliang.test.proxy.TestAspect;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;




import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;

public class Test {

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException {
        Properties p = new Properties();
        p.setProperty("name", "wenliang");
        p.setProperty("password", "123456");
        String str = "qwe#{name}qw#{password}er";
        String s = StringUtils.convertValueToSymbol(str, "#{", "}", p);
        System.out.println(s);
    }


    public static void testJavassist2() {
        Method[] methods=null;
        try {
            methods = UserController.class.getDeclaredMethods();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long l1 = System.currentTimeMillis();
        for (int i = 0; i < methods.length; i++) {
            String[] parameterNames = MethodParameterNamesScannerForJavassist.getParameterNames(methods[i]);
            System.out.println(Arrays.toString(parameterNames));
        }
        long l2 = System.currentTimeMillis();
        System.out.println(l2-l1);
        for (int i = 0; i < methods.length; i++) {
            String[] parameterNames = MethodParameterNamesScannerForJavassist.getParameterNames(methods[i]);
            System.out.println(Arrays.toString(parameterNames));
        }
        long l3 = System.currentTimeMillis();
        System.out.println(l3-l2);
        for (int i = 0; i < methods.length; i++) {
            String[] parameterNames = MethodParameterNamesScannerForJavassist.getParameterNames(methods[i]);
            System.out.println(Arrays.toString(parameterNames));
        }
        long l4 = System.currentTimeMillis();
        System.out.println(l4-l3);
    }
    public static void testJavassist() {
        try {
            //获取要操作的类对象
            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.get("com.wenliang.test.controller.UserController");
            //获取要操作的方法参数类型数组，为获取该方法代表的CtMethod做准备
            Method method = UserController.class.getMethod("findByUsernameAndPassword2", String.class, String.class);
            int count = method.getParameterCount();
            Class<?>[] paramTypes = method.getParameterTypes();
            CtClass[] ctParams = new CtClass[count];
            for (int i = 0; i < count; i++) {
                ctParams[i] = pool.getCtClass(paramTypes[i].getName());
            }

            CtMethod ctMethod = ctClass.getDeclaredMethod("findByUsernameAndPassword2", ctParams);
            //得到该方法信息类
            MethodInfo methodInfo = ctMethod.getMethodInfo();

            //获取属性变量相关
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

            //获取方法本地变量信息，包括方法声明和方法体内的变量
            //需注意，若方法为非静态方法，则第一个变量名为this
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            int pos = Modifier.isStatic(method.getModifiers()) ? 0 : 1;

            for (int i = 0; i < count; i++) {
                System.out.println(attr.variableName(i + pos));

            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }



    private static void testAsm() {
        Method[] methods=null;
        try {
            methods = UserController.class.getDeclaredMethods();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long l1 = System.currentTimeMillis();
        for (int i = 0; i < methods.length; i++) {
            String[] parameterNames = MethodUtils.getParameterNames(methods[i]);
            System.out.println(Arrays.toString(parameterNames));
        }
        long l2 = System.currentTimeMillis();
        System.out.println(l2-l1);
        for (int i = 0; i < methods.length; i++) {
            String[] parameterNames = MethodUtils.getParameterNames(methods[i]);
            System.out.println(Arrays.toString(parameterNames));
        }
        long l3 = System.currentTimeMillis();
        System.out.println(l3-l2);
        for (int i = 0; i < methods.length; i++) {
            String[] parameterNames = MethodUtils.getParameterNames(methods[i]);
            System.out.println(Arrays.toString(parameterNames));
        }
        long l4 = System.currentTimeMillis();
        System.out.println(l4-l3);
    }

    private static void testAspectMather() {
        AspectMatcher aspectMatcher = new AspectMatcher("* com..*All(*)");
        Method[] declaredMethods = Test.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (aspectMatcher.isMatchMethod(declaredMethod)) {
                System.out.println("匹配到：" + declaredMethod.getName());
            } else {
                System.out.println("未匹配到：" + declaredMethod.getName());
            }
        }
    }

    public String aaa(String username,String password) {
        return "aaa";
    }

    private final static void testCglibProxy() throws NoSuchMethodException {
        CglibProxy cglibProxy = new CglibProxy();
        HashMap<String, List<ExecutorAspect>> map = new HashMap<>();
        ArrayList<ExecutorAspect> list = new ArrayList<>();
        ExecutorAspect executorAspect = new ExecutorAspect();
        TestAspect testAspect = new TestAspect();
        executorAspect.setBean(testAspect);
        executorAspect.setMethod(testAspect.getClass().getDeclaredMethod("testAspectAaa"));
        ArrayList<String> methods = new ArrayList<>();
        methods.add("Aaaa");
        executorAspect.setMatcherMethod(methods);
        list.add(executorAspect);
        map.put("AspectAfter", list);

        Object proxy = cglibProxy.getProxy(Aaa.class,map);
        Object proxybbb = cglibProxy.getProxy(Bbb.class, map);

        String lalala = ((Aaa) proxy).Aaaa("username","passowrd");
        String lll = ((Aaa) proxy).Aaaac();
        String bbbb = ((Bbb) proxybbb).Bbbb();
        String bbbbc = ((Bbb) proxybbb).Bbbbc();
        System.out.println(lalala);
        System.out.println(lll);
        System.out.println(bbbb);
        System.out.println(bbbbc);
    }


    private static void testGeneSQL() {
        String sql = "select * from user where username=${username} and password=${password}";
        String[] sqlParameter = getSQLParameter(sql);

        String s = geneSQL(sql, sqlParameter);

        System.out.println(s);
    }

    /**
     * 通过原始sql和参数数组生成数据库需要的sql
     * @param sql
     * @param sqlParameter
     * @return
     */
    private static String geneSQL(String sql, String[] sqlParameter) {
        for (int i = 0; i < sqlParameter.length; i++) {
            sql = sql.replace(sqlParameter[i], "?");
        }
        return sql;
    }

    /**
     * 获取原始sql中的参数并生成数组
     * @param sql
     * @return
     */
    private static String[] getSQLParameter(String sql) {
        List<String> paras = new ArrayList<>();
        int start = -1;
        int end = -1;
        while ((start = sql.indexOf("${")) != -1) {
            end = sql.indexOf("}");
            String para = sql.substring(start, end+1);
            paras.add(para);
            sql = sql.substring(end+1);
        }
        return paras.toArray(new String[paras.size()]);
    }

//    private static void testGetResultType() {
//        try {
//            Method findAll = Test.class.getDeclaredMethod("findAll");
//            ParameterizedTypeImpl genericReturnType = (ParameterizedTypeImpl)findAll.getGenericReturnType();
//            Type[] types = genericReturnType.getActualTypeArguments();
//            Class domainClass = (Class)types[0];
//            System.out.println(domainClass.getName());
//            System.out.println(types[0].getTypeName());
//        } catch (NoSuchMethodException e) { }
//    }

    public List<User> findAll(){
        return null;};


    private static void testSelect() {
        //1.读取配置文件
        InputStream in = Resources.getResourceAsStream("SqlMapConfig.xml");
        //2.创建SqlSessionFactory工厂
        DefaultSqlSessionFactoryBuilder builder = new DefaultSqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        //3.使用工厂生产SqlSession对象
        SqlSession session = factory.openSession();
        //4.使用SqlSession创建Dao接口的代理对象
        UserDao userDao = session.getMapper(UserDao.class);
        UserDao userDao2 = session.getMapper(UserDao.class);
        System.out.println(userDao==userDao2);
        //5.使用代理对象执行方法
        User u = new User();
        u.setUsername("wl");
        u.setPassword("wwwwwwwww");
        u.setBirthday(new Date());
        u.setStatus(0);
        u.setGender(1);
        u.setEmail("qwe@qq.com");
        User u1 = userDao.findByUsernameAndPassword2("wl", "wwwwwwwww");
        System.out.println(u1);
//        if (users.size() == 0) {
//            Log.INFO("无符合条件的数据！");
//        }
//        for(User user : users){
//            System.out.println(user);
//        }
        //6.释放资源
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
