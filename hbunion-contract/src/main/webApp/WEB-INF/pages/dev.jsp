<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>TokenString IDE v1.11 BATA</title>
<link href="../jquery-ui-1.12.1/jquery-ui.css" rel="stylesheet">
<script src="../jquery-ui-1.12.1/external/jquery/jquery.js"></script>
<script src="../jquery-ui-1.12.1/jquery-ui.js"></script>
<link rel="stylesheet" type="text/css" href="../css/css2.css" />
<link rel="stylesheet" type="text/css" href="../css/codemirror.css">
<script src="../js/codemirror.js"></script>
<script src="../js/lib/codemirror.js"></script>
<script src="../js/addon/edit/matchbrackets.js"></script>
<script src="../js/addon/comment/continuecomment.js"></script>
<script src="../js/addon/comment/comment.js"></script>

<link rel="stylesheet" href="../js/addon/hint/show-hint.css">
<script src="../js/addon/hint/show-hint.js"></script>
<script src="../js/lib/javascript-hint.js"></script>
<script src="../js/lib/clike.js"></script>
<script src="../js/lib/javascript.js"></script>
<script src="../js/addon/selection/active-line.js"></script>
<!-- 查询替换 -->
<link rel=stylesheet href="../js/doc/docs.css">
<script src="../js/addon/dialog/dialog.js"></script>
<script src="../js/addon/search/searchcursor.js"></script>
<script src="../js/addon/search/search.js"></script>
<script src="../js/addon/scroll/annotatescrollbar.js"></script>
<script src="../js/addon/search/matchesonscrollbar.js"></script>
<script src="../js/addon/search/jump-to-line.js"></script>
<link rel="stylesheet" href="../js/addon/dialog/dialog.css">
<link rel="stylesheet" href="../js/addon/search/matchesonscrollbar.css">
<!--支持代码折叠-->
<link rel="stylesheet" href="../js/addon/fold/foldgutter.css" />
<script src="../js/addon/fold/foldcode.js"></script>
<script src="../js/addon/fold/foldgutter.js"></script>
<script src="../js/addon/fold/brace-fold.js"></script>
<script src="../js/addon/fold/comment-fold.js"></script>
<style type="text/css">
.step {
	height: 630px
}

.step01 table {
	width: 100%
}

.step01 table tr {
	width: 100%;
}

.step01_td0 {
	width: 60%
}

.step01_td0 textarea {
	width: 100%;
	height: 460px;
	resize: none;
	overflow-y: scroll !important;
}

.step01_td1 {
	width: 20%
}

.rightdiv {
	height: 500px;
	padding-left: 20px;
	overflow-y: scroll !important;
	
}

.step01_td1 select, h5 {
	margin-left: 5px;
	margin-top: 5px;
	padding: 5px;
	font-weight: normal;
	font-size: 13px;
}

.cinput {
	margin-left: 5px;
	margin-top: 5px;
	padding: 5px;
	border: 1px solid #ccc;
	border-radius: 4px;
}

.error {
	//margin-top: 40px;
}

.errordiv {
	margin: 0px;
	height: 100px;
	border: 1px solid #ccc;
	padding: 0px 5px 0px 5px;
	overflow-y: scroll;
}

.title {
	width: 99.6%;
	text-align: center;
}

.tabtitle {
	width: 90%;
}

.btnf {
	height: 27px; //
	float: right;
	line-height: 27px;
	//width: 60px;
	font-size: 12px;
	color: #fff;
	border: none;
	background: #ee3939;
	border-radius: 4px;
	margin: 3px 7px;
}

.green {
	background: #0d62d1;
}

#step2Content, #step2ContentSys {
	padding: 20px;
}

#selectfunc {
	margin-left: 5px;
	margin-top: 5px;
	padding: 5px;
	font-weight: normal;
	font-size: 16px;
	cursor: pointer;
	width: 100%;
}

.step2inpit {
	width: 98%;
}

hr {
	border-top: 1px solid #ccc;
}

#step3Content {
	text-align: center;
	padding-top: 100px;
}
</style>
</head>
<body>
	<div id="tabs">
		<ul>
			<li class="title">
				<a href="#tabs-1" class="tabtitle">Create your TOKScript code (WEB BATA)</a>
				<a href="http://139.186.77.248/TokScriptDoc/index.html" class="ui-tabs-anchor" style="cursor: pointer;float: right;" target="_blank">Document</a>
			</li>
		</ul>
		<div id="tabs-1" class="step step01">
			<table>
				<tr>
					<td class="step01_td0">
						<textarea class="form-control" id="java-code"></textarea>
					</td>
					<td class="step01_td1">
						<div class="rightdiv">
							<button class="btnf" onclick="check()">Syntax Check</button>
							<div id="methonArea" style="display:none;">
								<h5>Contract method</h5>
								<select id="func" onchange="funcchange(this)">
								</select>
								<h5>Least gas cost</h5>
								<input id="gas" class="cinput" type="text" placeholder="0.001Gas" value="0.001" />
								<h5>CONTENT_CALL_ADDRESS</h5>
								<input id="calladdress" class="cinput" type="text" placeholder="Please input address" />
								<h5>CONTENT_OWNER_ADDRESS</h5>
								<input id="owneraddress" class="cinput" type="text" placeholder="Please input address"/>
								<div id="params">
									<!-- <h5>参数一</h5>
									<input id="param_01" class="cinput" type="text" placeholder="请输入合约单位" maxlength="60" />
									<h5>参数二</h5>
									<input id="param_02" class="cinput" type="text" placeholder="请输入合约单位" maxlength="60" /> -->
								</div>
								<button class="btnf green" style="margin: 5px;" onclick="test()">Run Test</button>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div class="error">
							<h5>Console</h5>
							<div id="eval-result" class="errordiv"></div>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</div>
	<script type="text/javascript">
	var funcInfos = [];
		var check = function(){
			var code = javaEditor.getValue();
			var gas = $("#gas").val();
			if(!code){
				showMsg("The code is empty !");
				return;
			}
			if(!gas){
				showMsg("The gas is empty !");
				return;
			}
			console.log(code);
			$.ajax({
				url : 'check',
				type : 'post',
				data : JSON.stringify({
					"code" : code,
					"gas" : gas
				}),
				contentType : 'application/json;charset=utf-8',
				success : function(data){
					console.log(data);
					//结果
					var html = [];
					if(data.code == -1){
						html.push('<div style="color:red;">');
							html.push(data.msg);
						html.push('</div>');
						$("#eval-result").html(html.join(''));
						return;
					}else{
						html.push('<div style="color:green;">');
							html.push(data.msg);
						html.push('</div>');
					}
					$("#eval-result").html(html.join(''));
					//方法
					funcInfos = data.content;
					showFunc();
				}
			});
		}
		var showFunc = function(){
			//方法名
			var options = [];
			var params = [];
			for(var i = 0;i< funcInfos.length;i++){
				var funcInfo = funcInfos[i];
				options.push('<option value="' + i + '">' + funcInfo.funName + '</option>');
				if(i == 0){
					var pis = funcInfos[i].pis;
					for(var k = 0;k< pis.length; k++){
						var pi = pis[k];
						params.push('<h5>' + pi.name + '</h5>');
						params.push('<input name="params" class="cinput" type="text" placeholder="Please input ' + pi.type + ' type data" maxlength="60" />');
					}
				}
			}
			$("#func").html(options.join(''));
			//参数
			$("#params").html(params.join(''));
			$("#methonArea").show();
		}
		var funcchange = function(obj){
			var index = parseInt($(obj).val());
			console.log(index);
			var params = [];
			var pis = funcInfos[index].pis;
			for(var k = 0;k< pis.length; k++){
				var pi = pis[k];
				params.push('<h5>' + pi.name + '</h5>');
				params.push('<input name="params" class="cinput" type="text" placeholder="Please input ' + pi.type + ' type data" maxlength="60" />');
			}
			$("#params").html(params.join(''));
		}
		var test = function(){
			var index = $("#func").val();
			//取方法参数
			var runReqVo = {};
			runReqVo.funcName = funcInfos[index].funName;
			runReqVo.pris = [];
			for(var i=0;i<funcInfos[index].pis.length;i++){
				var pi = {};
				pi.name = funcInfos[index].pis[i].name;
				pi.type = funcInfos[index].pis[i].type;
				runReqVo.pris.push(pi);
			}
			
			$("input[name='params']").each(function(index, element){
				runReqVo.pris[index].value = element.value.trim();
			});
			runReqVo.callAddress = $("#calladdress").val();
			runReqVo.ownerAdress = $("#owneraddress").val();
			
			var gas = $("#gas").val();
			if(!gas){
				showMsg("The gas is empty !");
				return;
			}
			runReqVo.gas = gas;
			console.log(runReqVo);
			//验证表单
			if(!runReqVo.funcName){
				showMsg("function name cannot be empty.");
				return;
			}
			if(!runReqVo.callAddress){
				showMsg("CONTENT_CALL_ADDRESS cannot be empty.");
				return;
			}
			if(!runReqVo.ownerAdress){
				showMsg("CONTENT_OWNER_ADDRESS cannot be empty.");
				return;
			}
			for(var i=0;i<runReqVo.pris.length;i++){
				var pri = runReqVo.pris[i];
				if(pri.value == null || pri.value == ''){
					showMsg("Parameter cannot be empty.");
					return;
				}
			}
			//TODO检查类型是否匹配
			
			$.ajax({
				url : 'test',
				type : 'post',
				contentType : 'application/json;charset=UTF-8',
				data : JSON.stringify(runReqVo),
				success : function(data){
					console.log(data);
					var html = [];
					if(data.code == 0){
						html.push('<div style="color:green;">');
							html.push(data.msg);
						html.push('</div>');
					}else{
						html.push('<div style="color:red;">');
							html.push(data.msg);
						html.push('</div>');
					}
					$("#eval-result").append(html.join(''));
					var ele = document.getElementById('eval-result');
					ele.scrollTop = ele.scrollHeight;
				}
			});
		}
		var showMsg = function(msg){
			alert(msg);
		}
	</script>

	<script type="text/javascript">
		$("#tabs").tabs();
		var javaEditor = CodeMirror.fromTextArea(document.getElementById("java-code"), {
			lineNumbers : true,
			matchBrackets : true,
			mode : "text/x-java",

			lineWrapping : true, //代码折叠
			foldGutter : true,
			gutters : [ "CodeMirror-linenumbers", "CodeMirror-foldgutter" ],
			indentUnit : 2,
			smartIndent : true,
			extraKeys : {
				"Ctrl-/" : "autocomplete",
				"Ctrl-D" : "deleteLine",
				"F11" : function(cm) {
					cm.setOption("fullScreen", !cm.getOption("fullScreen"));
				},
				"Esc" : function(cm) {
					if (cm.getOption("fullScreen"))
						cm.setOption("fullScreen", false);
				}
			},
			styleActiveLine : true
		});
	</script>
	<script type="text/javascript">
	var tempToken = "${token}";
	var websocket;
    var init = function(){
        // 判断当前浏览器是否支持websocket
        if(window.WebSocket) {
            websocket = new WebSocket("ws://139.186.77.248:8211/ws");

            websocket.onopen = function() {
                console.log("建立连接.");
                websocket.send("token:" + tempToken);
            }
            websocket.onclose = function() {
                console.log("断开连接");
            }
            websocket.onmessage = function(e) {
                console.log("接收到服务器消息:" + e.data);
                var html = [];
                html.push('<div style="color:green;">');
					html.push(e.data);
				html.push('</div>');
				$("#eval-result").append(html.join(''));
            }
        }
        else {
            alert("当前浏览器不支持web socket");
        }
    }
    $(function(){
    	init();
    });
	</script>
</body>
</html>