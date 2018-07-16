<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page session="false"%>
<%-- <%@ session.invalidate (); %> --%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <meta charset="UTF-8">
  <title>聊天室</title>
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport"
        content="width=device-width, initial-scale=1">
  <meta name="format-detection" content="telephone=no">
  <meta name="renderer" content="webkit">
  <meta http-equiv="Cache-Control" content="no-siteapp"/>
  <link rel="alternate icon" type="image/png" href="assets/i/favicon.png">
  <link rel="stylesheet" href="assets/css/amazeui.min.css"/>
  
<script src="assets/js/jquery.min.js"></script>
<script src="assets/js/amazeui.min.js"></script>
<!-- UM相关资源 -->
<link href="assets/umeditor/themes/default/css/umeditor.css" type="text/css" rel="stylesheet">
<script type="text/javascript" charset="utf-8" src="assets/umeditor/umeditor.config.js"></script>
<script type="text/javascript" charset="utf-8" src="assets/umeditor/umeditor.min.js"></script>
<script type="text/javascript" src="assets/umeditor/lang/zh-cn/zh-cn.js"></script>
</head>
<body>
<header class="am-topbar am-topbar-fixed-top">
	  <div class="am-container">
	    <h1 class="am-topbar-brand">
	      <a href="#">聊天室</a>
	    </h1>
	    <div class="am-collapse am-topbar-collapse" id="collapse-head">
	      <ul class="am-nav am-nav-pills am-topbar-nav">
	        <li class="am-active"><a href="#">首页</a></li>
	        <li><a href="#">项目</a></li>
	      </ul>
	
	      <div class="am-topbar-right">
	        <label class="am-btn am-btn-secondary am-topbar-btn am-btn-sm online"><span>当前在线人数：</span><font>0</font></label>
	        <label class="am-btn am-btn-secondary am-topbar-btn am-btn-sm chat"><span>当前正在聊天人数：</span><font>0</font></label>
	       <!--  <button class="am-btn am-btn-secondary am-topbar-btn am-btn-sm"><span class="am-icon-pencil"></span> 注册</button> -->
	      </div>
	
	      <!-- <div class="am-topbar-right">
	        <button class="am-btn am-btn-primary am-topbar-btn am-btn-sm"><span class="am-icon-user"></span> 登录</button>
	      </div> -->
	    </div>
	  </div>
	</header>
	
	<div id="main">
		<!-- 聊天内容展示区域 -->
		<li id="msgtmp" class="am-comment" style="display:none;">
			    <a href="#">
			        <img class="am-comment-avatar" src="assets/images/other.jpg" alt=""/>
			    </a>
			    <div class="am-comment-main" >
			        <header class="am-comment-hd">
			            <div class="am-comment-meta">
			              <a ff="nickname" href="#link-to-user" class="am-comment-author"></a>
			              <time ff="msgdate" datetime="" title=""></time>
			            </div>
			        </header>
			     <div ff="content" class="am-comment-bd">此处是消息内容</div>
			    </div>
		</li>
	<div id="ChatBox" class="am-g am-g-fixed" >
	  <div class="am-u-lg-12" style="height:400px;border:1px solid #999;overflow-y:scroll;">
		<ul id="chatContent" class="am-comments-list am-comments-list-flip">
		</ul>
	  </div>
	</div>
	<!-- 聊天内容发送区域 -->
	<div id="EditBox" class="am-g am-g-fixed">
	<!--style给定宽度可以影响编辑器的最终宽度-->
	  <script type="text/plain" id="myEditor" style="width:100%;height:140px;"></script>
	<div>
	<button id="send" type="button" disabled="disabled" class=" " style="width:10%;float:right;border-radius: 5px;margin-left:10px;" value="发送">发送</button>
	<button id="connect" type="button" class="" style="width: 10%;border-radius: 5px;float:right;margin-right:1%;" >连接</button>
	</div>
	</div>
  
</div>
<script type="text/javascript">

$(function(){

	var baseUrl = function(flag) {
		var x = window.location.href.split("/");
		if(flag != null||flag != undefined){
			return  x[2] + "/" + x[3] + "/";
		}
		return x[0] + "//" + x[2] + "/" + x[3] + "/";
	};
    
	//实例化编辑器
    var um = UM.getEditor('myEditor',{
    	/* initialContent:"请输入聊天信息...", */
    	autoHeightEnabled:false,
    	toolbar:[
            'source | undo redo | bold italic underline strikethrough | superscript subscript | forecolor backcolor | removeformat |',
            'insertorderedlist insertunorderedlist | selectall cleardoc paragraph | fontfamily fontsize' ,
            '| justifyleft justifycenter justifyright justifyjustify |',
            'link unlink | emotion image video'
        ]
    });
     var userId = null;
	 var toUserId = null;
	 var websocket = null;
    var sendMsg = function(){
		 if (!um.hasContents()) {  // 判断消息输入框是否为空
	            // 消息输入框获取焦点
	            um.focus();
	            // 添加抖动效果
	            $('.edui-container').addClass('am-animation-shake');
	            setTimeout("$('.edui-container').removeClass('am-animation-shake')", 1000);
	        } else {
	            var txt = um.getContent(); 
	            var url = "/mochat/connection/sendMsg";
	            $.ajax({
	    				type : "post",
	    				url :  url,
	    				dataType : "json",	
	    				data : {"msg":txt,"userId":userId,"toUserId":toUserId},
	    				success : function(result){
	    					$("#myEditor p").text("");
	    					if(result.success == false){
	    						alert("你已经掉线，请刷新浏览器");
	    						return;
	    					}
	    					if(result==null||result.data==null||
	    							result.data.disConnectFlag == true){
	    						alert("你已经失去和对方的联系，请点击连接按键！");
	    						$("#send").attr("disabled","disabled");
	    						$("#connect").text("连接");
	    						toUserId = null;
	    						return;
	    					}
	    					result.data.isSelf = false;
	    					 addMessage(result.data)
	    				},
	    				fail : function(data){
	    					errorHandler ? errorHandler(data) : "";
	    				}
	            });
	            
	        }
	 };
	 var getUserInfo = function(){
		 var url = "/mochat/connection/getAllUserInfo";
		 $.ajax({
			 "type":"get",
			 "url":url,
			 "dataType":"json",
			 "success":function(result){
				 var data = result.data;
				 $(".online font").text(data.onLine);
				 $(".chat font").text(data.chat);
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
			        		}, 35000);
			        	}
			        }, this.timeout)
			    }
			};
	 var initWebSocket = function(param){
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
		    		$("#chatContent").children().remove();
		    		$("#send").removeAttr("disabled");
		    		$("#connect").text("断开");
		    		toUserId = obj.toUserId;
		    		alert("你已经和别人建立连接");
		    		return;
		    	}
		    	if(obj.disConnectFlag == true){
					$("#chatContent").children().remove();
					$("#send").attr("disabled","disabled");
					$("#connect").text("连接");
					toUserId = null;
					alert("对方已经和你断开，");
					return;
				}
		    	obj.isSelf = true;
		    	addMessage(obj);
		    };
		    websocket.onclose = function (event) {
		   };
		   websocket.onopen = function () {
			   heartCheck.start();
			};
	 };
	 
	 $("#connect").click(function(){
		 $(this).attr("disabled","disabled");
		 if($(this).text()=="连接"){
			 var url = "/mochat/connection/find";
			 $.ajax({
				 "type":"post",
				 "url":url,
				 "dataType":"json",
				 "data":{"userId":userId},
				 "success":function(data){
					 $("#connect").removeAttr("disabled");
					 userId = data.data.userId;
					 toUserId = data.data.toUserId;
					// var param = "userId="+userId+"&toUserId="+toUserId;
					if (typeof WebSocket == 'undefined') {
                        alert("浏览器版本过低");
                        return;
                    }
                    if (!window.WebSocket || !window.WebSocket.prototype.send){
                    	alert("浏览器版本过低");
                        return;
                     }
                    var param = "userId="+data.data.userId;
                	initWebSocket(param);
                   	
				   if(data.data.toUserId==null){
						alert("没有连接，请过会再次尝试");
						$("#send").attr("disabled","disabled");
						$("#connect").text("连接");
					}else{
						$("#send").removeAttr("disabled");
						$("#connect").text("断开");
						alert("创建连接成功");
					}
				   $("#chatContent").children().remove();
				 }
			 });
		 }
		 if($(this).text()=="断开"){
			 var url = "/mochat/connection/disconnect";
			 $.ajax({
				 "type":"post",
				 "url":url,
				 "dataType":"json",
				 "data":{"userId":userId},
				 "success":function(data){
					 $("#chatContent").children().remove();
					 $("#connect").removeAttr("disabled");
					 $("#connect").text("连接");
					 $("#send").attr("disabled","disabled");
				 }
			 });
		 }
	 });
	 $(document).keyup(function(event){  
         if(event.keyCode ==13){  
        	 sendMsg();
         }  
       });
    $("#send").click(function(){
    	sendMsg();
    });
    getUserInfo();
    setInterval(function(){
    	getUserInfo();
	}, 90000);
 });
 
 function clear(){
	 
 }
	//人名nickname，时间date，是否自己isSelf，内容content
	function addMessage(msg){
		var box = $("#msgtmp").clone(); 	//复制一份模板，取名为box
		box.show();							//设置box状态为显示
		box.appendTo("#chatContent");		//把box追加到聊天面板中
		box.find('[ff="nickname"]').html(msg.userId); //在box中设置昵称
		box.find('[ff="msgdate"]').html(msg.msgDate); 		//在box中设置时间
		box.find('[ff="content"]').html(msg.msgContent); 	//在box中设置内容
		box.addClass(msg.isSelf? 'am-comment-flip':'');	//右侧显示
		box.addClass(msg.isSelf? 'am-comment-warning':'am-comment-success');//颜色
		box.css((msg.isSelf? 'margin-left':'margin-right'),"20%");//外边距
		$("#ChatBox div:eq(0)").scrollTop(999999); 	//滚动条移动至最底部
	}
</script>
</body>
</html>
