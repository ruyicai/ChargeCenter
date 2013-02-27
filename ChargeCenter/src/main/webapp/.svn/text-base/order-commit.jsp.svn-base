<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<jsp:useBean id="paybean" scope="request" class="com.ruyicai.charge.chinapay.bean.PaymentBean" />
<jsp:useBean id="pay_url" scope="request" class="java.lang.String" />
<jsp:useBean id="error_code" scope="request" class="java.lang.String" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>order commit</title>
</head>
<body>
<form name="payment" action="<%= pay_url %>" method="POST" target="_blank">
<%
	String  MerId = paybean.getMerId();
	String  OrdId = paybean.getOrdId();
	String  TransDate = paybean.getTransDate();
	String  TransType = paybean.getTransType();
	String  TransAmt = paybean.getTransAmt();
	String  CuryId = paybean.getCuryId();
	String  GateId = paybean.getGateId();
	String  Version	= paybean.getVersion();
	String  ChkValue = paybean.getChkValue();
	String  BgRetUrl = paybean.getBgRetUrl();
	String  PageRetUrl = paybean.getPageRetUrl();
	String  Priv1 = paybean.getPriv1();
	
	//20100304版需要参数
	String  ClientIp = paybean.getClientIP();
%>
<table border="1" cellpadding="2" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111">
           <tr>
				<td>
					<font color=red>*</font>错误码
				</td>

				<td>
                     <%= error_code %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>商户号
				</td>

				<td>
                     <%= MerId %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>订单号
				</td>

				<td>
                     <%= OrdId %> &nbsp;<font color=gray>(16位数字，不填由系统自动产生)</font>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>商户日期
				</td>

				<td>
                     <%= TransDate %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>交易类型
				</td>

				<td>
                     <%= TransType %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>交易币种
				</td>

				<td>
                     <%= CuryId %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>订单金额
				</td>

				<td>
                     <%= TransAmt %> &nbsp;<font color=gray>(12位数字，不填默认金额为1分)</font>
                </td>
			</tr>
			<tr>
				<td>
					支付网关号
				</td>

				<td>
                     <%= GateId %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>版本号
				</td>

				<td>
                     <%= Version %>
                </td>
			</tr>
			<tr>
				<td>
					页面应答接收URL
				</td>

				<td>
                     <%= PageRetUrl %> &nbsp; <font color=gray>（商户系统前台应答接受地址）</font>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>后台应答接收URL
				</td>

				<td>
                     <%= BgRetUrl %> &nbsp; <font color=gray>（商户系统后台应答接受地址）</font>
                </td>
			</tr>
			<tr>
				<td>
					商户私有域
				</td>

				<td>
                     <%= Priv1 %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>客户端IP
				</td>

				<td>
                     <%= ClientIp %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>商户数字签名
				</td>

				<td width="800">
                     <pre><%= ChkValue %></pre>
                </td>
			</tr>

		</table>
		
		<hr>
<input type=hidden name="MerId" value="<%=MerId%>">
<input type=hidden name="OrdId" value="<%=OrdId%>">
<input type=hidden name="TransAmt" value="<%=TransAmt%>">
<input type=hidden name="CuryId" value="<%=CuryId%>">
<input type=hidden name="TransDate" value="<%=TransDate%>">
<input type=hidden name="TransType" value="<%=TransType%>">
<input type=hidden name="Version" value="<%=Version%>">
<input type=hidden name="BgRetUrl" value="<%=BgRetUrl%>">
<input type=hidden name="PageRetUrl" value="<%=PageRetUrl%>">
<input type=hidden name="Priv1" value="<%=Priv1%>">
<input type=hidden name="ChkValue" value="<%=ChkValue%>">
<input type=hidden name="GateId" value="<%=GateId%>">
<input type=hidden name="ClientIp" value="<%=ClientIp%>">


如果您的浏览器没有弹出支付页面，请点击按钮<input type='button' name='v_action' value='提交订单' onClick='document.payment.submit()'>再次提交。
		
</form>

<script language=JavaScript>
	document.payment.submit();
</script>			
</body>
</html>