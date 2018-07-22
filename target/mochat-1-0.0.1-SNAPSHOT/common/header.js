(function($){
	$.fn.header = {
			init:function(){
				var userInfo = $.fn.header.check();
				$.fn.header.initEvent(userInfo);
				$.fn.header.bindEvent();
			},
			check:function(){
				var random = mochat.utils.getQueryString("random");
				var baseURL = mochat.utils.baseURL();
					if(random == null||random==undefined||$.trim(random)==""){
						window.location.href = baseURL+"/login.html";
					}
			    var userInfoStr = sessionStorage.getItem("userInfo_"+random);
					if(userInfoStr == null){
						window.location.href = baseURL+"/login.html";
					}
					var userInfo = JSON.parse(userInfoStr);	
					return userInfo;
			},
			bindEvent:function(){
				$(".header").on("click",".quit",function(){
					layer.confirm('确定退出系统？', {
						  btn: ['确定','取消'] //按钮
						}, function(){
							var url = "/login/loginOut";//退出
		                	var config = {
		                			"URL":url
		                	};
		                	HTTPUtil.remoteInvoke(config,function(result){
		                		var random = mochat.utils.getQueryString("random");
		                		sessionStorage.removeItem("userInfo_"+random);
		                		window.location.href = mochat.utils.baseURL()+"login.html";
		                	});
						}, function(){
						  
						});
				});
				$(".header").on("click",".menu",function(){
					layer.confirm('确定返回主页面？', {
						  btn: ['确定','取消'] //按钮
						}, function(){
							var random = mochat.utils.getQueryString("random");
	                		window.location.href = mochat.utils.baseURL()+"menu.html?random="+random;
						}, function(){
						  
						});
				});
			},
			initEvent:function(userInfo){
				$(".header .userName span").text(userInfo.userName);
			}
	}
})(jQuery,window,undefined)