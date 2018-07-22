
var baseURL = function() {
	var x = window.location.href.split("/");
	return x[0] + "//" + x[2] + "/" + x[3] + "/";
};
var CMBAjax = {
	parseJSON : function(jsonString) { // FROM: json.org,json.js
		return eval('(' + jsonString + ')');
	},

	toJSONString : function(obj) { // FROM: json.org,json.js
		var m = {
			'\b' : '\\b',
			'\t' : '\\t',
			'\n' : '\\n',
			'\f' : '\\f',
			'\r' : '\\r',
			'"' : '\\"',
			'\\' : '\\\\'
		};
		var s = {
			array : function(x) {
				var a = ['['], b, f, i, l = x.length, v;
				if (l == 0) {
					var isNull = true;
					for (var i in x) {
						// Fenet's code begin
						if (i == "remove" || i == "indexOf") {
							delete x[i];
							continue;
						}
						if (x.toJSON)
							return x.toJSON();
						// Fenet's code end
						isNull = false;
						break;
					}
					if (!isNull) {
						// throw null;
						var a = ['{'], b, f, i, v;
						for (i in x) {
							v = x[i];
							f = s[typeof v];
							if (f) {
								v = f(v);
								if (typeof v == 'string') {
									if (b) {
										a[a.length] = ',';
									}
									a.push(s.string(i), ':', v);
									b = true;
								}
							}
						}
						a[a.length] = '}';
						return a.join('');
					}
				} else {
					for (i = 0; i < l; i += 1) {
						v = x[i];
						f = s[typeof v];
						if (f) {
							v = f(v);
							if (typeof v == 'string') {
								if (b) {
									a[a.length] = ',';
								}
								a[a.length] = v;
								b = true;
							}
						}
					}
				}
				a[a.length] = ']';
				return a.join('');
			},
			'boolean' : function(x) {
				return String(x);
			},
			'null' : function(x) {
				return "null";
			},
			number : function(x) {
				return isFinite(x) ? String(x) : 'null';
			},
			object : function(x) {
				if (x) {
					if (x instanceof Array) {
						return s.array(x);
					}
					var a = ['{'], b, f, i, v;
					for (i in x) {
						v = x[i];
						f = s[typeof v];
						if (f) {
							v = f(v);
							if (typeof v == 'string') {
								if (b) {
									a[a.length] = ',';
								}
								a.push(s.string(i), ':', v);
								b = true;
							}
						}
					}
					a[a.length] = '}';
					return a.join('');
				}
				return 'null';
			},
			string : function(x) {
				if (/["\\\x00-\x1f]/.test(x)) {
					x = x.replace(/([\x00-\x1f\\"])/g, function(a, b) {
								var c = m[b];
								if (c) {
									return c;
								}
								c = b.charCodeAt();
								return '\\u00'
										+ Math.floor(c / 16).toString(16)
										+ (c % 16).toString(16);
							});
				}
				return '"' + x + '"';
			}
		};
		return s[typeof obj](obj);
	},

	//
	// XMLHttpRequest state constant
	//
	id_counter : 0,
	XHR_UNINITIALIZED : 0,
	XHR_OPEN : 1,
	XHR_SENT : 2,
	XHR_RECEIVING : 3,
	XHR_LOADED : 4,

	doGet : function(url, notUseGBKJSP) {
		var xhr = null;
		if (window.XMLHttpRequest) {
			xhr = new XMLHttpRequest();
		} else {
			xhr = this.createMSXmlHttp();
		}
		try {
			xhr.open("GET", url, false);

			xhr.send("");
			var ret = xhr.responseText;
			cache[url] = ret;
			return ret;
		} catch (ex) {
			throw new Error(ex.message + ": " + url);
		}
	},

	doPost : function(url, data, callback, errorHandler, scope) {
		var xhr = null;
		if (window.XMLHttpRequest) {
			xhr = new XMLHttpRequest();
		} else {
			xhr = this.createMSXmlHttp();
		}
		if (callback)
			xhr.onreadystatechange = function() {
				if (CMBAjax.XHR_LOADED != xhr.readyState)
					return;
				if (xhr.status != 200) {
					if (!errorHandler){
						if (scope)
							callback.call(scope, null, xhr.status);
						else
							callback(null, xhr.status);
					}else{
						if (scope)
							errorHandler.call(scope, xhr);
						else
							errorHandler(xhr);
					}
				} else {
					var ret = xhr.responseText;
					xhr.abort();
					if (scope)
						callback.call(scope, ret);
					else
						callback(ret);
				}
				xhr = null;
			};
		for (var xx = 0; xx < 5; xx++) {// IE6某些版本XMLHTTP对象不稳定；我们在以往的经验中发现过类似问题，例如服务器启动GZIP压缩后，XMLHTTP的错误率很高（广发问题）
			try {

				var fullURL = url;
				var _baseURL = baseURL();
				if (_baseURL != "")
					fullURL = _baseURL + fullURL;

				xhr.open("POST", fullURL, callback ? true : false);
				// TODO: set headers
				xhr.setRequestHeader('Content-Type',
						'application/x-www-form-urlencoded;charset=UTF-8');
				xhr.send(data ? data : "");
				if (callback)
					return null;
				if (xhr.status != 200)
					throw new Error(xhr);
				return xhr.responseText;
			} catch (ex) {
				// if(xx == 4)
				throw new Error(ex.message + ": " + url);
			}
		}
	},

	remoteInvokeEx : function(config, callback, err) {
		return this.remoteInvoke(config, callback, err);
	},

	remoteInvoke : function(config, callback, err, scope) {

		CMBCaller = function(config) {
			var cif = {
				PRCCOD : '',
				WEBCOD : '',
				ISUDAT : '',
				ISUTIM : '',
				TARSVR : '',
				DALCOD : '',
				RTNLVL : '',
				RTNCOD : '',
				ERRMSG : '',
				INFBDY : {}
			};
			this.data = '';
			var initConfig = config || {};
			CMBCaller.apply(this, initConfig);
			CMBCaller.apply(cif, initConfig);
			this.data = CMBAjax.toJSONString(cif);
		};

		CMBCaller.apply = function(o, c) {
			if (o && c && typeof c == 'object') {
				for (var p in c) {
					if (o.hasOwnProperty(p.toString().toUpperCase()))
						o[p.toString().toUpperCase()] = c[p];
					if (o.hasOwnProperty(p))
						o[p] = c[p];
				}
			}
		};

		var cmbCaller = new CMBCaller(config);
		var url = 'rmi.do';
		if (callback) {
			CMBAjax.doPost(url, cmbCaller.data, callback, err, scope);
		} else {
			try {
				return CMBAjax.doPost(url, cmbCaller.data);
			} catch (ex) {
				alert("错误信息："+ex);
			}
		}
	},
	
	remoteInvoke2 : function(config, callback, err, scope) {

		CMBCaller = function(config) {
			var cif = {
				PRCCOD : '',
				WEBCOD : '',
				ISUDAT : '',
				ISUTIM : '',
				TARSVR : '',
				DALCOD : '',
				RTNLVL : '',
				RTNCOD : '',
				ERRMSG : '',
				INFBDY : {}
			};
			this.data = '';
			var initConfig = config || {};
			CMBCaller.apply(this, initConfig);
			CMBCaller.apply(cif, initConfig);
			this.data = CMBAjax.toJSONString(cif);
		};

		CMBCaller.apply = function(o, c) {
			if (o && c && typeof c == 'object') {
				for (var p in c) {
					if (o.hasOwnProperty(p.toString().toUpperCase()))
						o[p.toString().toUpperCase()] = c[p];
					if (o.hasOwnProperty(p))
						o[p] = c[p];
				}
			}
		};

		var cmbCaller = new CMBCaller(config);
		var url = 'rmi.mvc';
		if (callback) {
			CMBAjax.doPost(url, cmbCaller.data, callback, err, scope);
		} else {
			try {
				return CMBAjax.doPost(url, cmbCaller.data);
			} catch (ex) {
				alert("错误信息："+ex);
			}
		}
	},

	/**
	 * 创建remoteInvoke的callback函数
	 * 
	 * @param {}
	 *            o 配置项：<br>
	 *            	success -- 处理成功的回调函数。两个参数1为报文json对象， 参数2为报文中的INFBDY对象<br>
	 *            	fail -- 处理失败的回调函数。两个参数1为报文json对象， 参数2为报文中的错误信息ERRMSG<br>
	 *            	scope -- success和fail的scope<br>
	 *            
	 *            scp scope 为可选
	 * 
	 * @return {} callback函数
	 * 
	 * <pre>
	 * 
	 * 调用例子 : callback = CMBAjax.createCallback({
	 * 			success : function(message, messageBody) {
	 * 				if (messageBody &amp;&amp; messageBody.P1PTLADTZ1) {
	 * 					Ext.Msg.alert('提示', &quot;注册成功!&quot;, function() {
	 * 								this.hide();
	 * 							}, this);
	 * 				}
	 * 			},
	 * 			fail : function(message, errMessage) {
	 * 				Ext.Msg.alert('提示', &quot;注册失败! [ERRMSG:&quot; + errMessage + &quot;]&quot;,
	 * 						function() {
	 * 							this.hide();
	 * 						}, this);
	 * 			},
	 * 			scope : this
	 * 		});
	 * </pre>
	 * 
	 */
	createCallback : function(o, scp) {
		var success = o.success || this.emptyFn();
		var fail = o.fail || this.emptyFn();
		var scope = scp || o.scope || this;
		var me = this;

		return function(ret, xhrStatus) {
			if(typeof(xhrStatus) != "undefined" && xhrStatus != 200){
				var msg = {ERRMSG : "服务器无响应!"};
				fail.call(scope, msg, msg.ERRMSG);
			}else{
				var msg = CMBAjax.parseJSON(ret);
				if (me.isSuccessful(msg)) {
					success.call(scope, msg, msg.INFBDY);
				} else {
					fail.call(scope, msg, msg.ERRMSG);
				}
			}
		};
	},

	/**
	 * 判断操作是否成功
	 * @param {} msg 报文对象
	 * @return {} 布尔值
	 */
	isSuccessful : function(msg) {
		return msg.RTNCOD == 'SUC0000';
	},

	/**
	 * 解析和包装Ajax调用返回报文
	 * @param {} ret 报文
	 * @return {} 包装后对象
	 */
	wrapReturn : function(ret) {
		var msg = CMBAjax.parseJSON(ret);
		if (!msg) {
			return {
				success : false,
				errorMsg : '返回数据为空!'
			};
		}
		return {
			retCode : msg.RTNCOD,
			success : this.isSuccessful(msg),
			body : msg.INFBDY,
			errorMsg : msg.ERRMSG
		};
	},

	/**
	 * 空函数
	 */
	emptyFn : function() {
	}

};

