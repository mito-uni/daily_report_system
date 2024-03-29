<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="constants.AttributeConst" %>

<c:if test="${errors != null}">
    <div id="flush_error">
        入力内容にエラーがあります。<br />
        <c:forEach var="error" items="${errors}">
            ・<c:out value="${error}" /><br />
        </c:forEach>

    </div>
</c:if>
<fmt:parseDate value="${report.reportDate}" pattern="yyyy-MM-dd" var="reportDay" type="date" />
<label for="${AttributeConst.REP_DATE.getValue()}">日付</label><br />
<input type="date" name="${AttributeConst.REP_DATE.getValue()}" id="${AttributeConst.REP_DATE.getValue()}" value="<fmt:formatDate value='${reportDay}' pattern='yyyy-MM-dd' />" />
<br /><br />

<fmt:parseDate value="${report.startedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="startDay" type="date" />
<label for="${AttributeConst.REP_STARTED_AT.getValue()}">始業時間</label><br />
<input type="time" name="${AttributeConst.REP_STARTED_AT.getValue()}" id="${AttributeConst.REP_STARTED_AT.getValue()}" value="<fmt:formatDate value='${startDay}' pattern='HH:mm' />" />
<br /><br />

<fmt:parseDate value="${report.closedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="closeDay" type="date" />
<label for ="${AttributeConst.REP_CLOSED_AT.getValue()}">終業時間</label><br />
<input type="time" name="${AttributeConst.REP_CLOSED_AT.getValue()}" id="${AttributeConst.REP_CLOSED_AT.getValue()}" value="<fmt:formatDate value='${closeDay}' pattern='HH:mm' />" />
<br /><br />

<label>氏名</label><br />
<c:out value="${sessionScope.login_employee.name}" />
<br /><br />

<label for="${AttributeConst.REP_TITLE.getValue()}">タイトル</label><br />
<input type="text" name="${AttributeConst.REP_TITLE.getValue()}" id="${AttributeConst.REP_TITLE.getValue()}" value="${report.title}" />
<br /><br />

<label for="${AttributeConst.REP_CONTENT.getValue()}">内容</label><br />
<textarea  name="${AttributeConst.REP_CONTENT.getValue()}" id="${AttributeConst.REP_CONTENT.getValue()}" rows="10" cols="50">${report.content}</textarea>
<br /><br />

<label for="${AttributeConst.REP_APPROVAL_FLAG.getValue()}">承認状態</label><br />
<select name="${AttributeConst.REP_APPROVAL_FLAG.getValue()}" id="${AttributeConst.REP_APPROVAL_FLAG.getValue()}">
    <option value="${AttributeConst.APPROVAL_FLAG_WAIT.getIntegerValue()}"<c:if test="${report.approvalFlag == AttributeConst.APPROVAL_FLAG_WAIT.getIntegerValue()}"> selected</c:if>>承認待ち</option>
    <option value="${AttributeConst.APPROVAL_FLAG_TRUE.getIntegerValue()}"<c:if test="${report.approvalFlag == AttributeConst.APPROVAL_FLAG_TRUE.getIntegerValue()}"> selected</c:if>>承認済み</option>
    <option value="${AttributeConst.APPROVAL_FLAG_FALSE.getIntegerValue()}"<c:if test="${report.approvalFlag == AttributeConst.APPROVAL_FLAG_FALSE.getIntegerValue()}"> selected</c:if>>否認</option>
</select>
<br /><br />

<label for="${AttributeConst.REP_COMMENT.getValue()}">コメント</label><br />
<textarea  name="${AttributeConst.REP_COMMENT.getValue()}" id="${AttributeConst.REP_COMMENT.getValue()}" rows="10" cols="50">${report.comment}</textarea>
<br /><br />

<input type="hidden" name="${AttributeConst.REP_ID.getValue()}" value="${report.id}" />
<input type="hidden" name="${AttributeConst.TOKEN.getValue()}" value="${_token}" />
<button type="submit">投稿</button>