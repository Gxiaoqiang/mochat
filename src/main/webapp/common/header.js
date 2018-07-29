(function($){
	$.fn.header = {
			init:function(fn){
				var userInfo = $.fn.header.check();
				$.fn.header.initEvent(userInfo);
				$.fn.header.bindEvent(fn);
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
			loginOut:function(flag){
				var url = "/login/loginOut";//退出
            	var config = {
            			"URL":url
            	};
            	HTTPUtil.remoteInvoke(config,function(result){
            		var random = mochat.utils.getQueryString("random");
            		sessionStorage.removeItem("userInfo_"+random);
            		if(flag == undefined){
            			window.location.href = mochat.utils.baseURL()+"login.html";
            		}
            	});
			},
			bindEvent:function(fn){
				/**
				 * 启动监听
				 */
				setInterval(function(){
					var url = "/login/checkLogin";
					var config = {
							"URL":url
					};
					HTTPUtil.remoteInvoke(config,function(result){
						if(result ==  false){
							$.fn.header.loginOut(true);
							fn&&fn();
							layer.confirm('你在别处登陆，被强制下线？', {
								  btn: ['确定'] //按钮
								}, function(){
									window.location.href = mochat.utils.baseURL()+"login.html";
									layer.close();
								});
							
						}
					});
					 
				}, 3000);
				$(".header").on("click",".quit",function(){
					layer.confirm('确定退出系统？', {
						  btn: ['确定','取消'] //按钮
						}, function(){
							$.fn.header.loginOut();
							layer.close();
						}, function(){
						  
					});
				});
				$(".header").on("click",".menu",function(){
					layer.confirm('确定返回主页面？', {
						  btn: ['确定','取消'] //按钮
						}, function(){
							layer.close();
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