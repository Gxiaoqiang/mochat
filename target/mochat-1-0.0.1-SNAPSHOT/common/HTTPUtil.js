var HTTPUtil = {
		
	METHOD_TYPE_POST : "post",
	METHOD_TYPE_GET : "get",
	CONTENT_TYPE_HTML: "text/html",
	CONTENT_TYPE_JSON: "application/json",
	CONTENT_TYPE_FORM: "application/x-www-form-urlencoded",
		
	/**
	 * 
	 * @param config
	 *   METHOD：请求方式，非必须，默认值：post
	 *   URL：请求URL，必须
	 *   CONTYP：contentType，非必须，默认值：application/json
	 *   PARAM：参数，非必须
	 *   ASYNC: 是否异步，默认值：true
	 * @param callback 请求成功回调函数
	 * @param errorHandler 请求失败回调函数
	 */
	remoteInvoke: function(config, callback, errorHandler){
		
		var methodType = config.METHOD || this.METHOD_TYPE_POST;
		var url = config.URL;
		var contentType = config.CONTYP || this.CONTENT_TYPE_JSON;
		var param = config.PARAM || "";
		var async = config.ASYNC == false ?  false : true;
		if (contentType.toLowerCase() == this.CONTENT_TYPE_JSON
				&& methodType.toLowerCase() != this.METHOD_TYPE_GET) {
			param = JSON.stringify(param) || {};
		}
		if(this.isEmpty(url)){
			alert("请求URL为空，请输入正确URL");
			return;
		}
		$.ajax({
			type : methodType,
			url : this.baseURL() + url,
			contentType : contentType,
			data : param,
			dataType:"json",
			async : async,
			success : function(data){
				if(data.success){
					callback ? callback(data.data) : "";
				}else{
					data = (typeof data =="string")?$.parseJSON(data):data;
					alert(data.msg);
				}
			},
			fail : function(data){
				errorHandler ? errorHandler(data) : alert("服务器未响应，请联系管理员处理！");
			}
		});
	},
	
	/**
	 * 文件下载
	 * @param config
	 *   URL：请求URL，必须
	 *   PARAM：参数，非必须
	 */
	remoteDownload: function(config){
		
		var url = config.URL + ".mvc";
		var param = config.PARAM || {};
		var target = config.TARGER || "";
		var paramModel = config.PARAMMODEL || "param";
		
		var form=$("<form>");
		form.attr("style","display:none");
		form.attr("target",target);
		form.attr("method","post");
		form.attr("action", this.baseURL() + url);
		var input1=$("<input>");
		input1.attr("type","hidden");
		input1.attr("name","exportData");
		input1.attr("value",(new Date()).getMilliseconds());
		form.append(input1);
		$("body").append(form);
		this.initDownloadParam(form, param, paramModel);
		form.submit();
	},
	
	initDownloadParam: function(form, param, paramModel){
		if($.isArray(param)){
			for(var i=0 ;i<param.length; i++){
				var paramJson = param[i];
				for(var c in paramJson){
					var input = $("<input>");
					input.attr("type", "text");
					input.attr("name", paramModel + "[" + i + "]." + c);
					input.attr("value", paramJson[c]);
					form.append(input);
				}
			}
		}else{
			for(var c in param){
				var input = $("<input>");
				input.attr("type", "hidden");
				input.attr("name", c);
				input.attr("value", param[c]);
				form.append(input);
			}
		}
	},


	isEmpty: function(str){
		return str==undefined || str=="" || str==null;
	},
	
	baseURL: function() {
		var x = window.location.href.split("/");
		return x[0] + "//" + x[2] + "/" + x[3];
	}

}