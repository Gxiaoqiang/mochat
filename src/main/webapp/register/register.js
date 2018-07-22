;(function($){
	$.fn.register = {
			init:function(){
				$.fn.register.verificationImage();
				$.fn.register.bindEvent();
			},
			bindEvent:function(){
				$(".email").mailAutoComplete({
					boxClass: "out_box", //外部box样式
					listClass: "list_box", //默认的列表样式
					focusClass: "focus_box", //列表选样式中
					markCalss: "mark_box", //高亮样式
					autoClass: false,
					textHint: true //提示文字自动隐藏
				});
				$(".verification").click(function(){
					$.fn.register.verificationImage();
				});
				$(".register_button").click(function(){
					var userName = $(".userName").val();
					if($.trim(userName) == ""){
						layer.open({
    		                content: "用户名称不能为空",
    		                btn: ["确定"],
    		                yes: function(index) {
    		                    layer.close(index);
    		                    return;
    		                }
    		            });
		            	return;
					}
					var email = $(".email").val();
					if($.trim(email) == ""){
						layer.open({
    		                content: "邮箱不能为空",
    		                btn: ["确定"],
    		                yes: function(index) {
    		                    layer.close(index);
    		                    return;
    		                }
    		            });
		            	return;
					}
					if(mochat.utils.checkMail(email) == false){
						alert('请输入正确的邮箱格式');
						return false;
					}
					var passWord = $(".passWord").val();
					if($.trim(passWord)==""){
						layer.open({
    		                content: "密码不能为空",
    		                btn: ["确定"],
    		                yes: function(index) {
    		                    layer.close(index);
    		                    return;
    		                }
    		            });
		            	return;
					}
					var rePassWord = $(".rePassWord").val();
					if($.trim(passWord)==""){
						layer.open({
    		                content: "确认密码不能为空",
    		                btn: ["确定"],
    		                yes: function(index) {
    		                    layer.close(index);
    		                    return;
    		                }
    		            });
		            	return;
					}
					if(rePassWord != passWord){
						layer.open({
    		                content: "确认密码和密码不一致",
    		                btn: ["确定"],
    		                yes: function(index) {
    		                    layer.close(index);
    		                    return;
    		                }
    		            });
		            	return;
					}
					var vCode = $(".vdcode").val();
					if($.trim(vCode) == ""){
						layer.open({
    		                content: "验证码不能为空",
    		                btn: ["确定"],
    		                yes: function(index) {
    		                    layer.close(index);
    		                    return;
    		                }
    		            });
		            	return;
					}
					var data ={
						"userName":userName,
						"email":email,
						"userPassWord":passWord,
						"rePassWord":rePassWord,
						"vCode":vCode,
						"srand":$(".verification").attr("srand")
					};
					var url = "/login/register";
					var config = {
						"PARAM":data,
						"URL":url,
						"METHOD":"post",
						"dataType":"json",
						"CONTYPE":"application/json"
					};
					HTTPUtil.remoteInvoke(config,function(result){
						layer.open({
    		                content: "注册成功，请登录！",
    		                btn: ["确定","取消"],
    		                yes: function(index) {
    		                    layer.close(index);
    		                    window.location.href = mochat.utils.baseURL()+"/login.html";
    		                    return;
    		                },
    		                no:function(index){
    		                	layer.close(index);
    		                    return;
    		                }
    		            });
					});
				});
			},
			verificationImage : function() {
				var url = "/login/getVerificationImage";
				var config = {
					"URL" : url,
					"METHOD" : "get"
				};
				HTTPUtil.remoteInvoke(config, function(result) {
					var $verification = $(".verification");
					$verification.attr("src", result.image);
					$verification.attr("srand", result.sRand);
				});

			}
	}
	
})(jQuery,window,undefined);