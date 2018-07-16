/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package com.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionDataHelper {
	private Map<String, Object> sessionDataMap = new HashMap<String,Object>();

	private HttpServletRequest request = null;
	private HttpSession session = null;
	private boolean invalidate = false;

	public SessionDataHelper(HttpServletRequest request) {
		this.request = request;
		this.session = request.getSession(false);
	}

	private Object getSessionAttribute(String key) {
		if (null == this.session) {
			return null;
		}
		return this.session.getAttribute(key);
	}

	private void setSessionAttribute(String key, Object value) {
		if (null == this.session) {
			this.session = this.request.getSession();
		}
		this.session.setAttribute(key, value);
	}

	public void put(String key, Object obj) {
		if (this.invalidate) {
			return;
		}

		this.sessionDataMap.put(key, obj);
	}

	public Object get(String key) {
		if (this.invalidate) {
			return null;
		}

		if (this.sessionDataMap.containsKey(key)) {
			return this.sessionDataMap.get(key);
		}
		return getSessionAttribute(key);
	}

	public void terminateSession() {
		this.invalidate = true;
		this.sessionDataMap.clear();
	}

	public void commit() {
		if (this.invalidate) {
			if (null != this.session) {
				this.session.invalidate();
			}
			return;
		}

		if (this.sessionDataMap.isEmpty()) {
			return;
		}

		for (Map.Entry en : this.sessionDataMap.entrySet()) {
			String key = (String) en.getKey();
			Object value = en.getValue();

			setSessionAttribute(key, value);
		}

		this.sessionDataMap.clear();
	}
}