/**
 * Created by win on 2015/6/25.
 * 特效js
 */
/**随意拖动
 * 使用方法：只能单个图片移动。整个移动可用easyui 的 $( "#cir" ).draggable();
 * window.onload = function(){
        var obj = document.getElementById('draggable');
	rDrag.init(obj);
}
 * */
var rDrag = {
    o: null,

    init: function (o) {
        o.onmousedown = this.start;
    },
    start: function (e) {
        var o;
        e = rDrag.fixEvent(e);
        e.preventDefault && e.preventDefault();
        rDrag.o = o = this;
        o.x = e.clientX - rDrag.o.offsetLeft;
        o.y = e.clientY - rDrag.o.offsetTop;
        document.onmousemove = rDrag.move;
        document.onmouseup = rDrag.end;
    },
    move: function (e) {
        e = rDrag.fixEvent(e);
        var oLeft, oTop;
        oLeft = e.clientX - rDrag.o.x;
        oTop = e.clientY - rDrag.o.y;
        rDrag.o.style.left = oLeft + 'px';
        rDrag.o.style.top = oTop + 'px';
    },
    end: function (e) {
        e = rDrag.fixEvent(e);
        rDrag.o = document.onmousemove = document.onmouseup = null;
    },
    fixEvent: function (e) {
        if (!e) {
            e = window.event;
            e.target = e.srcElement;
            e.layerX = e.offsetX;
            e.layerY = e.offsetY;
        }
        return e;
    }
}

// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function(fmt)
{ //author: meizz
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
    return fmt;
}

/**
 * 其他的滚动
 * @param tarig
 */
function scrollTT(tarig){
    var defaultTop = tarig.offset().top;	//对象的默认top
    //核心scroll事件
    $(window).bind("scroll", function () {
        var offsetTop = $(window).scrollTop() + "px";
        tarig.animate({
                top: offsetTop
            },
            {
                duration: 500,
                queue: false    //此动画将不进入动画队列
            });
    });
}
/**
 * 圈圈滚动
 * @param tarig
 */
function scrollQQ(tarig){
    var defaultTop = tarig.offset().top;	//对象的默认top
    //核心scroll事件
    $(window).bind("scroll", function () {
        var offsetTop = defaultTop + $(window).scrollTop() + "px";
        tarig.animate({
                top: offsetTop
            },
            {
                duration: 500,
                queue: false    //此动画将不进入动画队列
            });
    });
}
