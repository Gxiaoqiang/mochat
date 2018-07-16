/**
 * 
 * websocket独立js,所有的聊天室和单独聊天都是用同一个websocket
 * 
 * @param $
 * @returns
 */

$(function($){
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
			return  x[2] + "/" + x[3] + "/";
		}
		return x[0] + "//" + x[2] + "/" + x[3] + "/";
	};
	initWebSocket = function(param){
		 if(param == null||param == undefined){
			 param = "";
		 }
		 initWebSocketParam = param;
	   if ('WebSocket' in window) {
	        websocket = new WebSocket("ws://"+baseUrl(1)+"websocket/socketServer.do?"+param);
	    }else if ('MozWebSocket' in window) {
	        websocket = new MozWebSocket("ws://"+baseUrl(1)+"websocket/socketServer.do?"+param);
	    } 
	    else {
	        websocket = new SockJS(baseUrl()+"sockjs/socketServer.do?"+param);
	    }
	   //接收服务器的消息
	    websocket.onmessage=function(ev){
	    	 heartCheck.reset();
	    	var obj = eval('('+ev.data+')');
	    	if(obj.firstConnect == true){
	    		 connectSuccess(1);
	    		 $("#connectButton").val("断开");
	    		toUserId = obj.toUserId;
	    		return;
	    	}
	    	if(obj.disConnectFlag == true){
	    		 var sys = '<div class="system text "><div class="conversation_divider" >- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - </div >对方已经和你断开连接！</div>';
                 $("#messages").append(sys);
                 $("#connectButton").val("连接");
                 $("#chat_sendButton").attr("cmd", 0);
				 toUserId = null;
				return;
			}
            Stranger_Message(othermessage.replace("@message", messageaction(obj.msgContent)).replace("@time", new Date().getTime()), 1)
	    };
	    websocket.onclose = function (event) {
	    	//disConnection();
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
            var param = "userId="+data.data.userId;
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
})(jQuery,undefined,window)