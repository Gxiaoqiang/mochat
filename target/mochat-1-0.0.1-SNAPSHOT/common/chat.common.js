/**
 * 公共函数<br>
 */

/**
 * 命名空间JQUERY工具类
 */
(function($){
	$.namespaceNew = function() {
	    var a=arguments, o=null, i, j, d;
	    for (i=0; i<a.length; i=i+1) {
	        d=a[i].split(".");
	        o=window;
	        for (j=0; j<d.length; j=j+1) {
	            o[d[j]]=o[d[j]] || {};
	            o=o[d[j]];
	        }
	    }
	    return o;
	};
	
	$.namespace = function() {
		for (var i = 0; i < arguments.length; i++) {
			var namespace = arguments[i].split('.');

			if (namespace.length > 0) {
				var current = window[namespace[0]] = window[namespace[0]] || {};

				for (var z = 1; z < namespace.length; z++) {
					current = current[namespace[z]] = current[namespace[z]] || {};
				}
			}
		}
	};

	$.namespaceNew("mochat.utils");

	mochat.utils = (function(){
		
		return {
			getRandom:function(key){
				var count=3000; 
				var originalArray=new Array;//原数组 
				//给原数组originalArray赋值 
				for (var i=0;i<count;i++){ 
				  originalArray[i]=i+1; 
				} 
				originalArray.sort(function(){ return 0.5 - Math.random(); }); 
			},
			checkMail:function(mail){
			 var myReg=/^[a-zA-Z0-9_-]+@([a-zA-Z0-9]+\.)+(com|cn|net|org)$/;
 
               return myReg.test(mail);		 
			},
			createRandomId:function(key) {
	            return (Math.random()*key).toString(16).substr(0,4)+'-'+(new Date()).getTime()+'-'+Math.random().toString().substr(2,5);
	        },
	        randomStr:function(len) {
	        	　　len = len || 32;
	        	　　var $chars = 'ABCDEFGHJK-_MNPQRSTWXYZabcdefhijkmnprstwxyz2345678oOLl9gqVvUuI1';    /****默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1****/
	        	　　var maxPos = $chars.length;
	        	　　var pwd = '';
	        	　　for (i = 0; i < len; i++) {
	        	　　　　pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
	        	　　}
	        	    
	        	　　return pwd+'-'+(new Date()).getTime()+'-'+Math.random().toString().substr(2,5);
	        },
			isObj:function (object) {
			    return object && typeof(object) == 'object' && Object.prototype.toString.call(object).toLowerCase() == "[object object]";
			},
			isArray:function (object) {
			    return object && typeof(object) == 'object' && object.constructor == Array;
			},
			getLength:function(object) {
			    var count = 0;
			    for(var i in object) count++;
			    return count;
			},
			Compare:function (objA, objB) {
			    if(!com.cmb.bip.utils.isObj(objA) || !com.cmb.bip.utils.isObj(objB)) return false; //判断类型是否正确
			    if(com.cmb.bip.utils.getLength(objA) != com.cmb.bip.utils.getLength(objB)) return false; //判断长度是否一致
			    return com.cmb.bip.utils.CompareObj(objA, objB, true); //默认为true
			},
			CompareObj:function (objA, objB, flag) {
			    for(var key in objA) {
			        if(!flag) //跳出整个循环
			            break;
			        if(!objB.hasOwnProperty(key)) {
			            flag = false;
			            break;
			        }
			        if(!mochat.utils.isArray(objA[key])) { //子级不是数组时,比较属性值
			            if(objB[key] != objA[key]) {
			                flag = false;
			                break;
			            }
			        } else {
			            if(!mochat.utils.isArray(objB[key])) {
			                flag = false;
			                break;
			            }
			            var oA = objA[key],
			                oB = objB[key];
			            if(oA.length != oB.length) {
			                flag = false;
			                break;
			            }
			            for(var k in oA) {
			                if(!flag) //这里跳出循环是为了不让递归继续
			                    break;
			                flag = mochat.utils.CompareObj(oA[k], oB[k], flag);
			            }
			        }
			    }
			    return flag;
			},
				baseURL: function() {
					var x = window.location.href.split("/");
					return x[0] + "//" + x[2] + "/" + x[3] + "/";
				},
				getObjectLength: function(obj){
					var i = 0;
					for(var key in obj){
						i++;
					}
					return i;
				},
				getQueryString : function(name) {
					var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
					var r = window.location.search.substr(1).match(reg);
					if (r != null)
						return unescape(r[2]);
					return null;
				},
				math : {

					uuid:function(len, radix){
						var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
						var chars = CHARS, uuid = [], i;
						radix = radix || chars.length;

						if (len){

							for (i = 0; i < len; i++) {
								uuid[i] = chars[0 | Math.random()*radix];
							}

						} else {

							var r;
							uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
							uuid[14] = '4';
							for (i = 0; i < 36; i++) {
								if (!uuid[i]) {
									r = 0 | Math.random()*16;
									uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
								}
							}
						}
						return uuid.join('');
					}	
				},
				
				cookie : {
					/**
					 * 添加Cookie
					 * 
					 * @param key
					 *            索引
					 * @param value
					 *            值
					 * @param expires
					 *            过期时间,单位:秒
					 */
					add : function(key, value, expires) {
						var str = key + '=' + escape(value);
						if (expires && expires > 0) {
							var date = new Date();
							date.setTime(date.getTime() + expires * 1000);
							str += ';expires=' + date.toGMTString();
						}
						document.cookie = str;
					},

					/**
					 * 获取Cookie
					 * 
					 * @param key
					 *            索引
					 * @returns
					 */
					get : function(key) {
						var regex = new RegExp('(^| )' + key + '=([^;]*)(;|$)');
						var matches = document.cookie.match(regex);
						if (!matches)
							return null;
						else
							return unescape(matches[2]);
					},
				delCookie : function(key){
					var exp = new Date();
					exp.setTime(exp.getTime() - 1);
					var cval=mochat.utils.cookie.get(key);
					if(cval!=null)
					  document.cookie= name + "="+cval+";expires="+exp.toGMTString();
					}
				},
				getStrLeng: function(str){ 
				    var realLength = 0; 
				    var len = str.length; 
				    var charCode = -1; 
				    for(var i = 0; i < len; i++){ 
				        charCode = str.charCodeAt(i); 
				        if (charCode >= 0 && charCode <= 128) {  
				            realLength += 1; 
				        }else{  
				            // 如果是中文则长度加3 
				            realLength += 3;
				        } 
				    }  
				    return realLength; 
				}
		};
	})();
	
	/**
	 * 如果浏览器不支持media query,动态加载bootstrap兼容代码包
	 */
	var supportIE8WithBootstrap = function(){
		
		if(window.matchMedia == undefined){	
			
			//加载兼容js
			var $html5shiv = $('<script type="text/javascript" src="../lib/bootstrap-3.1.1/js/html5shiv.min.js"></script>');
			var $respond = $('<script type="text/javascript" src="../lib/bootstrap-3.1.1/js/respond.min.js"></script>');
			$('head').append($html5shiv);
			$('head').append($respond);
			
			$(document.body).ready(function(){
				var $style;
				$style = $('<style type="text/css">:before,:after{content:none !important}<style>');
				$('head').append($style);
				return setTimeout(function() {
					return $style.remove();
				},0);
			});
		}
	};
	supportIE8WithBootstrap();
})(jQuery,window,undefined);
