(function($,undefined){
	var userId;
	var websocket = null;
	var initWebSocket = null;
	var initWebSocketParam = null;
	var random = mochat.utils.getQueryString("random");
	var roomId = mochat.utils.getQueryString("room");
	var othermessage = '<div class="message-body" style="display: flex;padding-bottom: 6px;">'+
	                      '<img class="am-comment-avatar" src="./image/nan.jpg" style="display:none;float: left; width: 45px; height: 48px; border-radius: 50%; border: 1px solid transparent;">'+
	                      '<div style="font-size:13px;line-height: 1.3;max-width:95%;">' +
	                      '<label  style=" float: left;padding-bottom: 3px;padding-left: 5px;margin-left: 8px;color: aliceblue;">@userNickName：</label>'+
	                      '<time class="timeago" style="color: #AAA;" datetime="@time">刚刚</time>'+
	                      '<div class="stranger text" title="陌生人信息" style="float: left;">'+
		                      '@message'+
		                       '<div class="stranger comment">'+
			                     '<span class="hidden_text"> (</span>'+
			                     '<span class="hidden_text">)<br></span>'+
		                       '</div>'+
                          '</div>'+
                          '</div>'+
                        '</div>';
	
	var selfmessage = '<div class="message-body" style="display: flex;flex-direction: row-reverse;padding-bottom: 6px;">'+
                       '<img class="am-comment-avatar" src="./image/nan.jpg" style="display:none;float: left; width: 32px; height: 32px; border-radius: 50%; border: 1px solid transparent;">'+
                       '<div class="me text" title="我的信息" style="font-size:13px;max-width:88%;line-height:1.3">'+
                          '@message'+
                          '<div class="me comment">'+
                            '<span class="read">已读<br></span><span class="hidden_text"> ('+
                            '</span><time class="timeago" datetime="@time">刚刚</time><span class="hidden_text">)<br>'+
                            '</span>'+
                           '</div>'+
                          '</div>'+
                         '</div>';
	var  messageaction = function (message) {
        if (message.indexOf("[hongbao:") != -1) {
            return "对方给你发送一个<a target='_blank' href='https://www.baidu.com'>【红包】</a>,此版本不支持领取将自动退回！"
        } else {
            if (message.indexOf("[photo:") != -1) {
                return "对方给你发送一张<a target='_blank' href='https://www.baidu.com'>【图片】</a>,此版本不支持查看请升级！"
            } else {
                if (message.indexOf("[yuyin:") != -1) {
                    return "对方给你发送一段<a target='_blank' href='https://www.baidu.com'>【语音】</a>,此版本不支持查看请升级！"
                } else {
                    return message
                }
            }
        }
    };
	var Stranger_stips = function(tips) {
        $("#stips").html(tips);
        sml = window.setTimeout(tipclear, 2000)
    }
    var tipclear = function() {
        $("#stips").html("&nbsp;");
        window.clearTimeout(sml)
    }
	 var heartCheck = {
			    timeout: 20000,//60ms
			    timeoutObj: null,
			    reset: function(){
			       clearTimeout(this.timeoutObj);
			　　　　 this.start();
			    },
			    start: function(){
			        this.timeoutObj = setTimeout(function(){
			        	if(websocket != null){
			        	setInterval(function(){
			        		 if(websocket.readyState == 1){
			                       websocket.send("HeartBeat");
		                          }
			        		}, 15000);
			        	}
			        }, this.timeout)
			    }
      };
	 function timeAgo(selector) {
	        var templates = {
	            prefix: "",
	            suffix: "",
	            justnow: "刚刚",
	            seconds: "%d" + "秒前",
	            minute: "1" + "分前",
	            minutes: "%d" + "分前",
	            hour: "1" + "小时前",
	            hours: "%d" + "小时前",
	            day: "1" + "天前",
	            days: "%d" + "天前",
	            month: "1" + "月前",
	            months: "%d" + "月前",
	            year: "1" + "年前",
	            years: "%d" + "年前",
	        };
	        var template = function(t, n) {
	            return templates[t] && templates[t].replace(/%d/i, Math.abs(Math.round(n)))
	        };
	        var timer = function(time) {
	            if (!time) {
	                return
	            }
	            var now = new Date();
	            var seconds = ((now.getTime() - time) * 0.001) >> 0;
	            var minutes = seconds / 60;
	            var hours = minutes / 60;
	            var days = hours / 24;
	            var years = days / 365;
	            return templates.prefix + (seconds < 10 && template("justnow", 10) || seconds < 60 && template("seconds", seconds) || minutes < 2 && template("minute", 1) || minutes < 60 && template("minutes", minutes) || hours < 2 && template("hour", 1) || hours < 24 && template("hours", hours) || days < 2 && template("day", 1) || days < 30 && template("days", days) || days < 45 && template("month", 1) || days < 365 && template("months", days / 30) || years < 1.5 && template("year", 1) || template("years", years)) + templates.suffix
	        };
	        var elements = document.getElementsByClassName("timeago");
	        for (var i in elements) {
	            var $this = elements[i];
	            if (typeof $this === "object") {
	                $this.innerHTML = timer($this.getAttribute("datetime"))
	            }
	        }
	    }
	    setInterval(timeAgo, 60000);
	    var baseUrl = function(flag) {
			var x = window.location.href.split("/");
			if(flag != null||flag != undefined){
				return  x[2];
			}
			return x[0] + "//" + x[2] + "/" + x[3] + "/";
		};
		var Stranger_clear = function() {
	          $("#chat_textarea").val("");
	          $("#chat_textarea").focus()
	      }
		 initWebSocket = function(param){
			 if(param == null||param == undefined){
				 param = "";
			 }
			 initWebSocketParam = param;
			 if ('WebSocket' in window) {
			       websocket = new WebSocket("ws://"+baseUrl(1).split(":")[0]+"/mochat/websocketNetty/socketServer.do?"+param);

			       // websocket = new ReconnectingWebSocket("ws://"+baseUrl(1).split(":")[0]+"/mochat/websocketNetty/socketServer.do?"+param,null,{ debug: true, reconnectInterval: 4000 });
			    }else if ('MozWebSocket' in window) {
			        websocket = new MozWebSocket("ws://"+baseUrl(1).split(":")[0]+"/mochat/websocketNetty/socketServer.do?"+param);
			    } else {
			        websocket = new SockJS(baseUrl()+"/websocketNetty/sockjs/socketServer.do?"+param);
			    }
		   // 接收服务器的消息
		    websocket.onmessage=function(ev){
		    	var data = ev.data;
		    	if(data=="validate failed"){
		    		layer.open({
		                content: '登陆已经过期，请重新登陆！',
		                btn: ["确定"],
		                yes: function(index) {
		                    layer.close(index);
		                    window.location.href = "../login.html";
		                }
		            });
		    		return;
		    	}
		    	heartCheck.reset();
		    	var obj = eval('('+ev.data+')');
		    	if(userId == obj.userId){
					Stranger_Message(selfmessage.replace("@message", obj.msgContent).replace("@time", new Date().getTime()), 1);
		    	}else{
		    		var userInfo = JSON.parse(obj.userInfo);
		            Stranger_Message(othermessage.replace("@message", messageaction(obj.msgContent)).replace("@time", new Date().getTime())
		            		.replace("@userNickName",userInfo.userName), 1);
		    	}
		    };
		    websocket.onclose = function (event) {
		    	/*if(websocket == null||websocket.readyState>1){
		    		initWebSocket("room");
		    	}*/
		   };
		   websocket.onopen = function () {
			   heartCheck.start();
			};
	 };
	 var Stranger_Message = function(message) {
         timeAgo("timeago");
         $("#messages").append(message);
      }
	    var sendMsg = function(){
            var txt = $("#chat_textarea").val();
            if($.trim(txt)==""){
            	layer.open({
	                content: "请填写发送内容",
	                btn: ["确定"],
	                yes: function(index) {
	                    layer.close(index);
	                    return;
	                }
	            });
            	return;
            }
            var msgData = {"msgContent":txt,"userId":userId,"roomId":roomId};
            var url = "/mochat/connection/sendMsg";
            /*$.ajax({
    				type : "post",
    				url :  url,
    				dataType : "json",	
    				data : msgData,
    				success : function(result){
    					$("#myEditor p").text("");
    					if(result.success == false){
    						alert("你已经掉线，请刷新浏览器");
    						return;
    					}
    					if(result==null||result.data==null||result.data.disConnectFlag == true){
    						StrangerOut();
    						toUserId = null;
    						return;
    					}*/
    					msgData["type"] = "room";
    					websocket.send(JSON.stringify(msgData));
    					//Stranger_Message(selfmessage.replace("@message", $("#TextBox_send").val()).replace("@time", new Date().getTime()), 1);
    					Stranger_clear();
    				/*},
    				fail : function(data){
    					errorHandler ? errorHandler(data) : "";
    				}
            });*/
    };
    var getOnLine = function(){
    	var url = "../roomChat/getOnLine";
    	$.ajax({
    		url:url,
    		dataType:"json",
    		success:function(result){
    			if(result.success==true){
    				$(".list_item .member_nick").text("当前在线人数："+result.data);
    			}else{
    				alert(result.msg);
    			}
    			
    		}
    	})
    };
	 $.fn.roomchat = {
		init:function(){
			$.fn.roomchat.joinRoom(function(){
				var userInfo = $.fn.roomchat.getUserInfo();
				$("#endit_userinfo").text(userInfo.userName);
				userId = userInfo["userId"];
				var param = "chatType=room";
				initWebSocket(param);// 创建链接
				getOnLine();
				setInterval(getOnLine, 6000);
			});
			$.fn.roomchat.bindEvent();
		},
		close:function(){
			if(websocket != null){
				websocket.close(1000,"otherLogin");
			}
		},
		getUserInfo:function(){
			var userInfoStr = sessionStorage.getItem("userInfo_"+random);
			if(userInfoStr == null){
				window.location.href = mochat.utils.baseURL()+"/login.html";
			}
			var userInfo = JSON.parse(userInfoStr);	
			return userInfo;
		},
		bindEvent:function(){
			window.addEventListener("beforeunload", function (e) {
				  websocket.close();
				});
			window.onunload = function(){
				websocket.close();
			}
			$("#send_chat_btn").click(function(e){
				var cmd = $(this).attr("cmd");
				if(cmd == 0){
					return;
				}
				if(cmd == 1){
					sendMsg();
				}
			});
			$(document).keyup(function(event) {
			    if (event.keyCode == 13) {
			        $("#send_chat_btn").trigger("click")
			    }
			});
			$("#close_window").click(function(e){
				//询问框
				layer.confirm('是否要关闭当前网页？', {
				  btn: ['确定','取消'] //按钮
				}, function(){
					var url = mochat.utils.baseURL()+"/roomChat/quitRoom";
					 if(websocket == null||websocket == undefined){
						 $.ajax({
							 url:url,
							 success:function(result){
								 window.opener=null;
								 window.location.href=mochat.utils.baseURL()+"/menu.html?random="+random;
								 window.close();
							 }
						 });
					 }else{
						 window.opener=null;
						 window.close();
						 window.location.href=mochat.utils.baseURL()+"/menu.html?random="+random;
					 }
				}, function(){
				  
				});
			});
		},
		joinRoom:function(fn){
			var url = "/roomChat/joinRoom?fresh="+(new Date()).getTime();
			var config = {
					"URL":url,
					"PARAM":{"roomId":roomId},
					"METHOD":"get",
					"DATA":"json"
			};
			HTTPUtil.remoteInvoke(config,function(result){
				$("#send_chat_btn").attr("cmd","1");
				fn&&fn()
			});
		}
	 };
})(jQuery,undefined,window)