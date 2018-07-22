/**
 * Created by win on 2015/6/25.
 */
var websocket = null;
function connection(websocketUrl, sockJSUrl) {
    if (!websocketUrl || !sockJSUrl) {
        alert("链接地址不能为空");
    }
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://" + websocketUrl);
    } else if ('MozWebSocket' in window) {
        websocket = new MozWebSocket("ws://" + websocketUrl);
    } else {
        websocket = new SockJS("ws://" + sockJSUrl);
    }
    websocket.onopen = function (evnt) {
        $("#tou-msg").html("链接服务器成功");
        $("#photo").attr("src","/assets/common/image/defeatPhoto.jpg");
    };
    websocket.onmessage = function (evnt) {
        console.log(evnt);
        var data = evnt.data;
        data = jQuery.parseJSON(data);
        console.log(data);
        if(data.msgType == 1){

        }else if(data.msgType == 2){  //聊天室 信息
            var time = new Date(data.timeOfArrive).Format("hh:mm:ss");
            var msg = '';

            var class_msgTitle_left ="msgTitle_left  img-circle";
            var class_msgPanel_left ="msgPanel_left";
            var class_triangle_left ="triangle_left";
            var class_article_left ="article_left";
            if(data.msgUserInfo.userId == 0){
                class_msgTitle_left = "msgTitle_left sysMsgTitle img-circle";
                class_msgPanel_left ="sysMsgPanel";
                class_triangle_left ="triangle_left sysTriangle";
                class_article_left ="sysArticle";
            }

            if(data.msgUserInfo.userId == $("#uuUserId").val()){ //自己
             msg =
                 '<div class="msg_bianKuangWw">'+
                    '<img class="msgIcon_right img-circle" src="' + data.msgUserInfo.icons + '">'+
                    '<div class="msgTitle_right">' + data.msgUserInfo.nickname + '  ' + time + ' </div>' +
                    '<div class="msgPanel_right">' +
                        '<span class="triangle_right"></span>' +
                        '<div class="article_right">' + data.msgBody + '</div>' +
                    '</div>' +
                 '</div>';
            }else {
                $('#chatAudio')[0].play(); //播放声音
              msg =  '<div class="msg_bianKuangWw">' +
                    '<img class="msgIcon_left img-circle" src="' + data.msgUserInfo.icons + '">' +
                    '<div class="' + class_msgTitle_left + '">' + data.msgUserInfo.nickname + '  ' + time + ' </div>' +
                    '<div class="' + class_msgPanel_left + '">' +
                        '<span class="' + class_triangle_left + '"></span>' +
                        '<div class="' + class_article_left + '">' + data.msgBody + '</div>' +
                    '</div>' +
                '</div>';
            }
            $("#msg").html($("#msg").html() + msg);
            document.getElementById('msg').scrollTop = document.getElementById('msg').scrollHeight;
        }else if (data.msgType == 3){  //服务器推送的用户信息，需要保存在本地
           var uu =  $("#userInfo");
            uu.empty();
            uu.append('<input id="uuUserId" type="hidden" value="' + data.msgUserInfo.userId + '"/>');
            uu.append('<input id="uuNickname" type="hidden" value="' + data.msgUserInfo.nickname + '"/>');
            uu.append('<input id="uuIcons" type="hidden" value="' + data.msgUserInfo.icons + '"/>');
            uu.append('<input id="uuUserType" type="hidden" value="' + data.msgUserInfo.userType + '"/>');
            $("#photo").attr("src",data.msgUserInfo.icons);
        }else if (data.msgType == 4){  //更新在线列表 和人数
            $("#left-right-down-right-onlineListGJ").html("在线人数:" + data.onlieNum)
            $("#onlineList").empty();
            console.log(data.onlieList);
            $.each(data.onlieList,function(i,o){
               var objLits = ' <li class="list-group-item">' +
                    '<img class="img-circle" src="' + o.icons + '" style="width: 20px;height: 20px"/>'+ o.nickname +
                   '</li>';
                $("#onlineList").append(objLits);
            });
        }else if(data.msgType == 5){
            //qq抖动效果
            var left=document.getElementById('left');
            convertStyle(left);
            shake(left);
        }
    };
    websocket.onerror = function (evnt) {
        $("#tou-msg").html("出现了一个错误");
    };
    websocket.onclose = function (evnt) {
        $("#tou-msg").html("链接服务器失败");
        grayscale($("#photo"));
    }
   /* var stompClient = Stomp.over(new SockJS("/hello"));
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (string) {
            console.log(string);
            alert(string.body);
        });
    });
    $('#ceshi').bind('click', function () {
        stompClient.subscribe("/app/pull", function (string) {
            alert(string);
        });
    });
    $('#dingyue').bind('click', function () {
        stompClient.send("/app/hello", {}, "{x:x}");
    });*/
}

function send(message,msgType) {
    if (websocket) {
        var msgClient = {
            "targetId":0,
            "msgType":msgType,
            "msgBody":message
        };
        websocket.send(JSON.stringify(msgClient));

    } else {
        $("#msg").html($("#msg").html()  + "发送失败");
    }
}