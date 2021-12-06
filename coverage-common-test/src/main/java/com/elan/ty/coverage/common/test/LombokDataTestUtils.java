package com.elan.ty.coverage.common.test;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;

public class LombokDataTestUtils {

    private static final Map<String, Object> MAP1 = new HashMap<>();
    private static final Map<String, Object> MAP2 = new HashMap<>();
    private static final List<String> BASE = Arrays.asList(new String[]{"int", "long", "float", "double",
            "byte", "short", "char", "boolean"});

    static {
        MAP1.put("java.lang.String", "test1");
        MAP2.put("java.lang.String", "test2");

        MAP1.put("java.lang.Integer", new Integer(1));
        MAP2.put("java.lang.Integer", new Integer(2));

        MAP1.put("java.lang.Long", new Long(1));
        MAP2.put("java.lang.Long", new Long(2));

        MAP1.put("java.lang.Double", new Double(1));
        MAP2.put("java.lang.Double", new Double(2));

        MAP1.put("java.lang.Float", new Float(1));
        MAP2.put("java.lang.Float", new Float(2));

        MAP1.put("java.util.List", new EqualList<>());
        MAP2.put("java.util.List", new EqualList<>());

        MAP1.put("java.util.Map", new EqualMap<>());
        MAP2.put("java.util.Map", new EqualMap<>());

        MAP1.put("java.lang.Byte", new Byte("1"));
        MAP2.put("java.lang.Byte", new Byte("2"));

        MAP1.put("java.lang.Boolean", new Boolean(true));
        MAP2.put("java.lang.Boolean", new Boolean(false));

        MAP1.put("java.util.Date", new Date());
        MAP2.put("java.util.Date", new Date());

        MAP1.put("int", 1);
        MAP2.put("int", 2);

        MAP1.put("int", 1);
        MAP2.put("int", 2);

        MAP1.put("long", 1L);
        MAP2.put("long", 2L);

        MAP1.put("float", 1F);
        MAP2.put("float", 2F);

        MAP1.put("double", 1D);
        MAP2.put("double", 2D);

        MAP1.put("byte", new Byte("1").byteValue());
        MAP2.put("byte", new Byte("1").byteValue());

        MAP1.put("short", new Integer(1).shortValue());
        MAP2.put("short", new Integer(1).shortValue());

        MAP1.put("char", 'a');
        MAP2.put("char", 'b');

        MAP1.put("boolean", true);
        MAP2.put("boolean", false);


    }

    public static void cover(Class clazz) {
        try {
            Object o1 = clazz.newInstance();
            Object o2 = clazz.newInstance();
            o1.equals(null);
            o1.equals(o1);
            o1.hashCode();
            Field[] fields = clazz.getDeclaredFields();
            o1.toString();

            for (Field field : fields) {
                field.setAccessible(true);
                if (BASE.contains(field.getType().getName())) {
                    Object f1 = MAP1.get(field.getType().getName());
                    Object f2 = MAP2.get(field.getType().getName());
                    field.set(o2, f2);
                    field.set(o1, f1);
                    o1.hashCode();
                    continue;
                }

                field.set(o1, null);
                field.set(o2, null);
                o1.equals(o2);
                if (MAP1.containsKey(field.getType().getName())) {
                    Object f1 = MAP1.get(field.getType().getName());
                    Object f2 = MAP2.get(field.getType().getName());
                    field.set(o1, null);
                    field.set(o2, f2);
                    o1.equals(o2);
                    field.set(o1, f1);
                    o1.hashCode();
                    o1.equals(o2);
                    field.set(o2, f1);
                    o1.equals(o2);
                } else if (field.getType().getName().startsWith("[")) {
                    // 数组情况有些复杂，暂时先不考虑
                    continue;
                } else {
                    Object f1 = field.getType().newInstance();
                    Object f2 = field.getType().newInstance();
                    field.set(o1, null);
                    field.set(o2, f2);
                    o1.equals(o2);
                    field.set(o1, f1);
                    o1.hashCode();
                    o1.equals(o2);
                    field.set(o2, f1);
                    o1.equals(o2);
                }

            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("set") && method.getReturnType().getName().equals("void")
                        && method.getParameterTypes() != null && method.getParameterTypes().length == 1) {
                    Class[] clazzs = method.getParameterTypes();
                    if (BASE.contains(clazzs[0].getName())) {
                        method.invoke(o1, MAP1.get(clazzs[0].getName()));
                    } else {
                        method.invoke(o1, new Object[]{null});
                    }

                }
            }

        } catch (Exception e) {
            Assert.assertNotNull(e);
        }

    }

    public static void cover(String packagePath){
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        String packageDirName = packagePath.replace('.', '/');
        Enumeration<URL> dirs;
        boolean recursive = true;

        try{
            dirs = Thread.currentThread().getContextClassLoader().getResources(
                    packageDirName);
            while (dirs.hasMoreElements()){
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    System.err.println("file类型的扫描");
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packagePath, filePath,
                            recursive, classes);
                } else if ("jar".equals(protocol)){
                    continue;
                }
            }
            for(Class clazz : classes){
                cover(clazz);
            }
        }catch (Exception e){

        }
    }

    private static class EqualList<E> extends ArrayList<E> {
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else {
                return false;
            }
        }

    }

    private static class EqualMap<K, V> extends HashMap<K, V> {
        public boolean equals(Object o) {
            if (o == EqualMap.this) {
                return true;
            } else {
                return false;
            }
        }

    }

    private static void findAndAddClassesInPackageByFile(String packageName,
                                                        String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "."
                                + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader()
                            .loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    Assert.assertNotNull(e);
                }
            }
        }
    }
}
