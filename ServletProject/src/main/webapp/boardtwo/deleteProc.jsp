<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>

<c:if test="${check == 1}">
<meta charset="UTF-8" http-equiv="Refresh" content="0;url=/boardtwo/list.bdo?pageNum=${pageNum}">
</c:if>

<c:if test="${check == 0}">
	비밀번호가 맞지 않습니다
	<a href="javascript:history.go(-1)">[글 삭제 폼으로 돌아가기]</a>
</c:if>

</head>
<body>
</body>
</html>