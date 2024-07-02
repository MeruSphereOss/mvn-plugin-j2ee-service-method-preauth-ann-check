package com.merusphere.devops.mvnplugin;

public class ClazzMethod {
	private String clazz;
	private String method;

	public ClazzMethod(String clazz, String method) {
		this.method = method;
		this.clazz = clazz;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

}