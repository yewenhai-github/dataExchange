package com.easy.app.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.easy.utility.LogUtil;

public class ServiceProxy implements InvocationHandler{
	
	private Object target;
	/**
	 * 返回代理工厂类
	 * */
	public static ServiceProxy getInstance(){
		return new ServiceProxy();
	}
	/** 
	* 绑定委托对象并返回一个代理类 
	* @param target 
	* @return 
	*/ 
	public Object newProxyInstance(Object target) {
		try {
			this.target = target;
			return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(), this);
		} catch (IllegalArgumentException e) {
			LogUtil.printLog(e.getMessage(),LogUtil.INFO);
		}  
		return null;
	}
	
	/** 
	* 代理类调用的方法明细 
	* @param target 
	* @return 
	*/
	public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
		Object o = null;
		long start = System.currentTimeMillis();
		try {
			o = method.invoke(target, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
		LogUtil.printLog(target.getClass().getName()+"执行完毕,耗时 " + (System.currentTimeMillis() - start) + " ms!",LogUtil.INFO); 
		return o;
	}
	
}
