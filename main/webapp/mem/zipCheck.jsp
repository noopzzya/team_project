<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>우편번호 검색</title>
<link href="regform.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="script.js"></script>
</head>
<body>
<div align="center">
<b>우편번호 찾기</b><br><br>
<form action="mem.do?cmd=zipCheck" method="post" name="zipForm">

	<table class="ziptable">
		<tr>
			<td>
				<p>동 이름 입력</p>
				<input type="text" name="dong">
				<input class="zip_bt1" type="button" value="검색" onclick="dongCheck()">
			</td>
		</tr>
	</table>

	<input type="hidden" name="check" value="n">	
</form>

	<table>
	
		<c:if test="${check eq 'n'}">
			<c:if test="${zipcodeList.isEmpty()}">
				<tr><td align="center"><br>검색된 결과가 없습니다.</td></tr>
			</c:if>
		
			<c:if test="${!zipcodeList.isEmpty()}">
				<tr><td align="center">※검색 후, 아래 우편번호를 클릭하면 자동으로 입력됩니다.</td></tr>
				<c:forEach var="vo" items="${zipcodeList}" >
					<tr><td align="left">
					<a href="javascript:sendAddress('${vo.getZipcode()}','${vo.getSido()}','${vo.getGugun()}','${vo.getDong()}','${vo.getRi()}','${vo.getBunji()}')">
					${vo.getZipcode()}&nbsp;${vo.getSido()}&nbsp;${vo.getGugun()}&nbsp;${vo.getDong()}&nbsp;${vo.getRi()}&nbsp;${vo.getBunji()}
					</a><br>
					</td></tr>
				</c:forEach>
			</c:if>
				
		</c:if>
		
		<tr>
			<td align="center">
				<input class="zip_bt2" type="button" value="닫기" onclick="javascript:window.close()">				
			</td>
		</tr>
		
	</table>
</div>
</body>
</html>