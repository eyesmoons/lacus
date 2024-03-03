package com.lacus.script;

/**
 * 自定义类加载器
 */
public class FsClassLoader extends ClassLoader {

    private String fullyName;

    private byte[] data;

    public FsClassLoader() {

    }

    public FsClassLoader(ClassLoader parentClassLoader, String name, byte[] data) {
        super(parentClassLoader);
        this.fullyName = name;
        this.data = data;
    }

    @Override
    public Class<?> findClass(String name) {
        return defineClass(fullyName, data, 0, data.length);
    }

}
