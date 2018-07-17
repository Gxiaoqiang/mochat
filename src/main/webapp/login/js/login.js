(function($) {

	$.fn.login = {

		init : function() {

			$.fn.login.verificationImage();

			$.fn.login.bindEvent();

		},

		verificationImage : function() {

			var url = "/login/getVerificationImage";

			var config = {

				"URL" : url,

				"METHOD" : "get"

			};

			HTTPUtil.remoteInvoke(config, function(result) {

				var $verification = $(".login .verification");

				$verification.attr("src", result.image);

				$verification.attr("srand", result.sRand);

			});

		},

		bindEvent : function() {

			$(".content .con_right .left").click(
					function(e) {

						$(this).css({
							"color" : "#333333",
							"border-bottom" : "2px solid #2e558e"
						});

						$(".content .con_right .right").css({
							"color" : "#999999",
							"border-bottom" : "2px solid #dedede"
						});

						$(".content .con_right ul .con_r_left").css("display",
								"block");

						$(".content .con_right ul .con_r_right").css("display",
								"none");

						if (fluCodeInterval == null
								|| fluCheckCodeInterval == null) {

							show();

							flushQRCode();

							checkQRCodeStatus();

						}

					});

			$(".content .con_right .right").click(
					function(e) {

						$(this).css({
							"color" : "#333333",
							"border-bottom" : "2px solid #2e558e"
						});

						$(".content .con_right .left").css({
							"color" : "#999999",
							"border-bottom" : "2px solid #dedede"
						});

						$(".content .con_right ul .con_r_right").css("display",
								"block");

						$(".content .con_right ul .con_r_left").css("display",
								"none");

					});

			$('#btn_Login')
					.click(
							function() {

								var email = $('#userid').val();
								var passWord = $('#pwd').val();
								var vdcode = $("#vdcode").val();
								if ($.trim(email) == '') {
									alert('请输入您的用户名');
									return false;
								}
								if ($.trim(passWord) == '') {
									alert('请输入密码');
									return false;
								}
								if ($.trim(vdcode) == '') {
									alert("请输入验证码！");
									return false;
								}
								{
									var data = {
										"email" : email,
										"passWord" : passWord,
										"vdcode" : vdcode,
										"vdcodeId" : $(".verification").attr(
												"srand")

									};

									$
											.ajax({
												data : data,
												url : "login/loginIn",
												type : "post",
												dataType : "json",
												success : function(result) {
													if (result.success == true) {
														var userInfo = result.data;
														if (userInfo != null) {
															var randomId = mochat.utils
																	.randomStr(33);
															$("#user_form")
																	.attr(
																			"action",
																			"menu.html");

															$("#random").val(
																	randomId);

															document
																	.getElementById(
																			"user_form")
																	.submit();

															var userInfoStr = JSON
																	.stringify(userInfo);

															sessionStorage
																	.setItem(
																			"userInfo_"
																					+ randomId,
																			userInfoStr);

														} else {

															alert(result.msg);

														}

													} else {

														alert(result.msg);

													}

												},

												fail : function(data) {

													errorHandler ? errorHandler(data)
															: "";

												}

											});

								}

							});

			$("#flushLoginValiCode1,#flushLoginValiCode2").click(
					function() {

						$("#loginImgCode").attr("src",
								"/handler/GetLoginCode.ashx?" + Math.random());

					});

			$(".login .verification").click(function() {

				$.fn.login.verificationImage();

			});

		}

	}

})(jQuery, window, undefined)
