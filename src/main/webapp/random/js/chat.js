$(window).resize(function() {
    $("#messages").height($("#messageshow").height());
    var div = document.getElementById("messageshow");
    div.scrollTop = div.scrollHeight;
    $("#messages").height(div.scrollHeight)
});
$(document).keyup(function(event) {
    if (event.keyCode == 13) {
        $("#moliao_sendButton").trigger("click")
    }
});
$(document).ready(function() {
    var chatHub = $.connection.chatHub;
    chatHub.client.UserConnect = UserConnect;
    chatHub.client.Stranger = Stranger;
    chatHub.client.StrangerOut = StrangerOut;
    chatHub.client.Welcome = Welcome;
    chatHub.client.Message = Message;
    chatHub.client.SYSMessage = SYSMessage;
    var lastmessage = "";
    $.connection.hub.start().done(function() {
        $("#moliao_sendButton").click(function() {
            var cmd = $(this).attr("cmd");
            var send = $("#T1").val();
            var message = $("#TextBox_send").val().trim();
            if (message.length > 0 && cmd != 0) {
                if (lastmessage == send + message) {
                    SYSMessage(1);
                    Stranger_clear()
                } else {
                    lastmessage = send + message;
                    if (message.length > 100) {
                        message = message.substring(0, 100)
                    }
                    chatHub.server.send(send, cmd, 1, message).fail(function(err) {
                        SYSMessage(0)
                    });
                    sendmessage()
                }
            } else {
                Stranger_clear()
            }
        })
    });
    function UserConnect() {
        chatHub.server.adduser($("#T1").val(), $("#T2").val(), $("#T3").val())
    }
    function Welcome() {
        moliao(true)
    }
    function moliao(action) {
        if (action) {
            chatHub.server.moliao($("#T1").val(), 0)
        } else {
            chatHub.server.moliao($("#T1").val(), 1)
        }
    }
    var sytime;
    function Stranger(cmd, city, sex) {
        $("#loading").hide();
        $("#messages").fadeIn(1000);
        $("#messageaction").fadeIn(600);
        $("#moliao_sendButton").attr("cmd", cmd);
        $("#messages").height($("#messageshow").height());
        var sys = '<div class="logoMessage"><img onclick="go()" class="logoimg" width="60px" src="' + $(".logoimg")[0].src + '"></div>';
        sys += '<div class="systemMessage"><span class="systemName">系统公告：</span>礼貌聊天，文明交友。请遵守当地有关法律法规，不要撒播谣言，传播色情信息，以及发送垃圾信息干扰用户正常使用！</div>';
        if (city != "YS") {
            sys += "<div class='connectSuccessMessage'>您已经和一个来自<b>【" + city + "】</b>陌生朋友连接上!</div><div class='connectSuccessMessage'>对方性别 <b>【" + sex + "】</b>，问个好吧。</div>"
        } else {
            sys += "<div class='connectSuccessMessage'>您已经和一个陌生朋友连接上,问个好吧！</div>"
        }
        sys += '<div class="conversation_divider">- - - - - - - - - - - - - - - - - - - - - - - - - - -</div>';
        $("#messages").html(sys);
        if (cmd == "2A14AC1E97430202C79CF1A47805471E56C12EC8CBF61D194BB30BBD3E807500") {
            var sysj = Math.floor(Math.random() * 10 + 1) + 5;
            sytime = setTimeout(function() {
                chatHub.server.moliao($("#T1").val(), 2);
                StrangerOut()
            }, sysj * 1000)
        }
    }
    function StrangerOut() {
        clearTimeout(sytime);
        var sys = "";
        var sys = '<div class="system text "><div class="conversation_divider" > - - - - - - - - - - - - - - - - - - - - - - - - - - -</div >对方已经断开连接！</div>';
        $("#moliao_connectButton").val("连接");
        Stranger_Message(sys);
        $("#moliao_sendButton").attr("cmd", "0")
    }
    function Message(send, cmd, type, name, img, message) {
        if (type == 1) {
            Stranger_Message(othermessage.replace("@message", messageaction(message)).replace("@time", new Date().getTime()), 1)
        }
    }
    function SYSMessage(type) {
        if (type == 0) {
            var sys = "系统提示：信息发送失败！";
            $("#chat_textarea").val("").focus();
            Stranger_stips(sys)
        } else {
            if (type == 1) {
                var sys = "反垃圾系统提示：请勿发送重复信息！";
                $("#chat_textarea").val("").focus();
                Stranger_stips(sys)
            } else {
                if (type == 2) {
                    $("#loading").show();
                    $("#messages").hide();
                    $("#messageaction").hide()
                } else {
                    if (type == 3) {
                        clearTimeout(sytime);
                        var sys = '<div class="system text "><div class="conversation_divider" > - - - - - - - - - - - - - - - - - - - - - - - - - - -</div >你已经断开连接！</div>';
                        $("#moliao_sendButton").attr("cmd", "0");
                        Stranger_Message(sys)
                    } else {
                        if (type == 100) {
                            layer.open({
                                content: '<div><span style="color:yellow;" >系统提示</span><br><br>系统拒绝为你提供服务！</div>',
                                style: "overflow:hidden;line-height: 30px; background-color: #393D49; color: #fff; font-weight: 300;",
                                btn: "立即退出！",
                                shadeClose: false,
                                time: 3,
                                yes: function() {
                                    if (window.location.pathname.toLowerCase().indexOf("jiaruai") > 0) {
                                        window.location.href = "http://www.jiaruai.com"
                                    } else {
                                        window.location.href = "/user/login"
                                    }
                                },
                                end: function() {
                                    if (window.location.pathname.toLowerCase().indexOf("jiaruai") > 0) {
                                        window.location.href = "http://www.jiaruai.com"
                                    } else {
                                        window.location.href = "/user/login"
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    }
    $("#moliao_connectButton").click(function() {
        if ($("#moliao_connectButton").val() == "断开") {
            var txt = "是否要结束本次对话？";
            layer.open({
                content: txt,
                btn: ["断开", "取消"],
                yes: function(index) {
                    moliao(false);
                    layer.close(index);
                    $("#moliao_connectButton").val("连接")
                }
            })
        } else {
            $("#moliao_connectButton").val("断开");
            moliao(true)
        }
    });
    var sml;
    function Stranger_stips(tips) {
        $("#stips").html(tips);
        sml = window.setTimeout(tipclear, 2000)
    }
    function tipclear() {
        $("#stips").html("&nbsp;");
        window.clearTimeout(sml)
    }
    function Stranger_clear() {
        $("#TextBox_send").val("");
        $("#TextBox_send").focus()
    }
    function messageaction(message) {
        if (message.indexOf("[hongbao:") != -1) {
            return "对方给你发送一个<a target='_blank' href='https://www.quouyu.com'>【红包】</a>,此版本不支持领取将自动退回！"
        } else {
            if (message.indexOf("[photo:") != -1) {
                return "对方给你发送一张<a target='_blank' href='https://www.quouyu.com'>【图片】</a>,此版本不支持查看请升级！"
            } else {
                if (message.indexOf("[yuyin:") != -1) {
                    return "对方给你发送一段<a target='_blank' href='https://www.quouyu.com'>【语音】</a>,此版本不支持查看请升级！"
                } else {
                    return message
                }
            }
        }
    }
    function Stranger_Message(message) {
        timeAgo("timeago");
        $("#messages").append(message);
        var div = document.getElementById("messageshow");
        div.scrollTop = div.scrollHeight;
        $("#messages").height(div.scrollHeight)
    }
    var selfmessage = '<div class="me text"><span class="hidden_text">我：</span>@message<div class="me comment"><span class="read">已读<br></span><span class="hidden_text"> (</span><time class="timeago" datetime="@time">刚刚</time><span class="hidden_text">)<br></span></div></div>';
    var othermessage = ' <div class="stranger text"><span class="hidden_text">陌生人：</span>@message<div class="stranger comment"><span class="hidden_text"> (</span><time class="timeago" datetime="@time">刚刚</time><span class="hidden_text">)<br></span></div></div>';
    function sendmessage() {
        if ($("#TextBox_send").val() != "") {
            Stranger_Message(selfmessage.replace("@message", $("#TextBox_send").val()).replace("@time", new Date().getTime()), 1);
            Stranger_clear()
        }
    }
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
    setInterval(timeAgo, 60000)
});
(function() {
    var hm = document.createElement("script");
    hm.src = "https://hm.baidu.com/hm.js?b3adc1457c905d07654a2b5431e2d485";
    var s = document.getElementsByTagName("script")[0];
    s.parentNode.insertBefore(hm, s)
}
)();
