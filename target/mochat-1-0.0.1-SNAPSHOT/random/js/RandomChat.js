(function($,undefined){
	var sml;
	var userId = null;
	var toUserId = null;
	var websocket = null;
	var initWebSocket = null;
	var initWebSocketParam = null;
    var selfmessage = '<div class="me text" title="我的信息"><span class="hidden_text">我：</span>@message<div class="me comment"><span class="read">已读<br></span><span class="hidden_text"> (</span><time class="timeago" datetime="@time">刚刚</time><span class="hidden_text">)<br></span></div></div>';
    var othermessage = ' <div class="stranger text" title="陌生人信息"><span class="hidden_text">陌生人：</span>@message<div class="stranger comment"><span class="hidden_text"> (</span><time class="timeago" datetime="@time">刚刚</time><span class="hidden_text">)<br></span></div></div>';
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
    var baseUrl = function(flag) {
		var x = window.location.href.split("/");
		if(flag != null||flag != undefined){
			return  x[2] ;
		}
		return x[0] + "//" + x[2] + "/" + x[3] + "/";
	};
    var StrangerOut	= function() {
        var sys = "";
        var sys = '<div class="system  "><div class="conversation_divider" > - - - - - - - - - - - - - - - - - - - - - - - - - - -</div >对方已经断开连接！</div>';
        $("#connectButton").val("连接");
        //Stranger_Message(sys);
        $("#sendButton").attr("cmd", "0")
      };
      var Stranger_stips = function(tips) {
          $("#stips").html(tips);
          sml = window.setTimeout(tipclear, 2000)
      }
      var tipclear = function() {
          $("#stips").html("&nbsp;");
          window.clearTimeout(sml)
      }
      var Stranger_clear = function() {
          $("#TextBox_send").val("");
          $("#TextBox_send").focus()
      }
      var Stranger_Message = function(message) {
          timeAgo("timeago");
          $("#messages").append(message);
          var div = document.getElementById("messageshow");
          div.scrollTop = div.scrollHeight;
          $("#messages").height(div.scrollHeight)
      }
    var sendMsg = function(){
		            var txt = $("#TextBox_send").val();
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
		            var msgData = {"msg":txt,"userId":userId};
		            var url = "/mochat/connection/sendMsg";
		            $.ajax({
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
		    					}
		    					msgData["type"] = "random";
		    					websocket.send(JSON.stringify(msgData));
		    					Stranger_Message(selfmessage.replace("@message", $("#TextBox_send").val()).replace("@time", new Date().getTime()), 1);
		    					Stranger_clear();
		    				},
		    				fail : function(data){
		    					errorHandler ? errorHandler(data) : "";
		    				}
		            });
		 };
		 var disConnection = function(){
			 var url = "/mochat/connection/disconnect";
			 $.ajax({
				 "type":"post",
				 "url":url,
				 "dataType":"json",
				 "data":{"userId":userId},
				 "success":function(data){
                     var sys = '<div class="system  "><div class="conversation_divider" > - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -</div >你已经断开连接！</div>';
                     $("#messages").append(sys);
                     $("#connectButton").val("连接");
				 }
			 });
		 };
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
		 var connectSuccess = function(cmd) {
		        $("#loading").hide();
		        $("#messages").fadeIn(1000);
		        $("#messageaction").fadeIn(600);
		        $("#sendButton").attr("cmd", cmd);
		        $("#messages").height($("#messageshow").height());
		        var sys = '<div class="logoMessage"><img onclick="go()" class="logoimg" width="60px" src="' + $(".logoimg")[0].src + '"></div>';
		        sys += '<div class="systemMessage"><span class="systemName">系统公告：</span>礼貌聊天，文明交友。请遵守有关法律法规，不要撒播谣言，传播色情信息，以及发送垃圾信息干扰用户正常使用！</div>';
		        sys += "<div class='connectSuccessMessage'>您已经和一个陌生朋友连接上,问个好吧！</div>"
		        sys += '<div class="conversation_divider">- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - </div>';
		        $("#messages").html(sys);
		    };
		 var connectFail = function(cmd){
			    $("#loading").hide();
		        $("#messages").fadeIn(1000);
		        $("#messageaction").fadeIn(600);
		        $("#sendButton").attr("cmd", cmd);
		        $("#messages").height($("#messageshow").height());
		        var sys = '<div class="logoMessage"><img onclick="go()" class="logoimg" width="60px" src="' + $(".logoimg")[0].src + '"></div>';
		        sys += "<div class='connectSuccessMessage'>没有匹配成功，请再次尝试！</div>"
		        /*sys += '<div class="conversation_divider">- - - - - - - - - - - - - - - - - - - - - - - - - - -</div>';*/
		        $("#messages").html(sys);
		 };
		 initWebSocket = function(param){
				//var  param = "chatType="+type;
				 initWebSocketParam = param;
			   /*if ('WebSocket' in window) {
			        //websocket = new WebSocket("ws://"+baseUrl(1)+"websocket/socketServer.do?"+param);
			       websocket = new WebSocket("ws://"+baseUrl(1).split(":")[0]+":7397/mochat/"+"websocketNetty/socketServer.do?"+param);
			    }else if ('MozWebSocket' in window) {
			        websocket = new MozWebSocket("ws://"+baseUrl(1)+"websocketNetty/socketServer.do?"+param);
			    } 
			    else {
			        websocket = new SockJS(baseUrl()+"sockjs/socketServer.do?"+param);
			    }*/
				 if ('WebSocket' in window) {
				        websocket = new WebSocket("ws://"+baseUrl(1).split(":")[0]+"/mochat/"+"websocketNetty/socketServer.do?"+param);
				    }else if ('MozWebSocket' in window) {
				        websocket = new MozWebSocket("ws://"+baseUrl(1).split(":")[0]+"websocketNetty/socketServer.do?"+param);
				    } else {
				        websocket = new SockJS(baseUrl()+"sockjs/socketServer.do?"+param);
				    }
			   //接收服务器的消息
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
			    	if(obj.firstConnect == true){
			    		 connectSuccess(1);
			    		 $("#connectButton").val("断开");
			    		toUserId = obj.toUserId;
			    		return;
			    	}
			    	if(obj.disConnectFlag == true){
			    		 var sys = '<div class="system  "><div class="conversation_divider" >- - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - </div >对方已经和你断开连接！</div>';
	                     $("#messages").append(sys);
	                     $("#connectButton").val("连接");
	                     $("#chat_sendButton").attr("cmd", 0);
						 toUserId = null;
						return;
					}
		            Stranger_Message(othermessage.replace("@message", messageaction(obj.msg)).replace("@time", new Date().getTime()), 1)
			    };
			    websocket.onclose = function (event) {
			    	if(websocket == null||websocket.readyState>1){
			    		initWebSocket(initWebSocketParam);
			    	}
			   };
			   websocket.onopen = function () {
				   heartCheck.start();
				};
		 };
		 var initConnection = function(){
			 $("#messageshow").hide();
			 $("#messageaction").hide();
			 $("#loading").show();
			 var url = "/mochat/connection/find";
			 $.ajax({
				 "type":"post",
				 "url":url,
				 "dataType":"json",
				 "data":{"userId":userId},
				 "success":function(data){
					 userId = data.data.userId;
					 toUserId = data.data.toUserId;
					if (typeof WebSocket == 'undefined') {
                        alert("浏览器版本过低");
                        return;
                    }
                    if (!window.WebSocket || !window.WebSocket.prototype.send){
                    	alert("浏览器版本过低");
                        return;
                     }
                    if(data.data.firstConnect == false){
                    	alert("你正在和别人聊天，请断开再重新连接!");
                    	var random = mochat.utils.getQueryString("random");
                    	window.location.href = "../menu.html?random="+random;
                    	return;
                    }
                    var param = "chatType=random";
                    if(websocket==null||websocket.readyState!=1){
                    	initWebSocket(param);
                    }
				   if(data.data.toUserId==null){
						//alert("没有连接，请过会再次尝试");
					    connectFail(0);
						$("#connectButton").val("连接");
					}else{
						$("#connectButton").val("断开");
						connectSuccess(1);//连接成功
					}
				   $("#messageshow").show();
				   $("#messageaction").show();
				   $("#loading").hide();
				 }
			 }); 
		 };
		 var randomChat = function(flag){
			 if(flag == false){//断开连接
				 disConnection();
			 }
			 if(flag == true){
				 initConnection();
			 }
		 };
		 $.fn.chat = {
    		init:function(){
    			$.fn.chat.bind();
    			$("#connectButton").trigger("click");
				$(".slogan").show();
    		},
    		bind:function(){
    		  $("#connectButton").click(function() {
    		        if ($("#connectButton").val() == "断开") {
    		            var txt = "是否要结束本次对话？";
    		            layer.open({
    		                content: txt,
    		                btn: ["断开", "取消"],
    		                yes: function(index) {
    		                	randomChat(false);
    		                    layer.close(index);
    		                    $("#connectButton").val("连接")
    		                }
    		            });
    		        } else {
    		            randomChat(true);
    		            $("#connectButton").val("断开");
    		        }
               });
    		  $("#sendButton").click(function(){
    			  var cmd = $(this).attr("cmd");
    			  if(cmd == 1){
    				  sendMsg();
    			  }
  	           });
    		  $(window).resize(function() {
    			    $("#messages").height($("#messageshow").height());
    			    var div = document.getElementById("messageshow");
    			    div.scrollTop = div.scrollHeight;
    			    $("#messages").height(div.scrollHeight)
    			});
    			$(document).keyup(function(event) {
    			    if (event.keyCode == 13) {
    			        $("#sendButton").trigger("click")
    			    }
    			});
    			$(".show .find").click(function(){
    				 $(this).hide();
    				 $("#connectButton").trigger("click");
    				 $(".slogan").show();
    			});
    		}
 };
})(jQuery,undefined,window)