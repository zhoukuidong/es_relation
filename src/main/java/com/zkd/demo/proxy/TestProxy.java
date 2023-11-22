package com.zkd.demo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestProxy {

    public static void main(String[] args) {
        Fruit o = (Fruit)Proxy.newProxyInstance(Apple.class.getClassLoader(), new Class[]{Fruit.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("====before=====");
                Object result = method.invoke(new Apple(),args);
                System.out.println("====after=====");
                return result;
            }
        });
        System.out.println(o.price());
    }
}
