<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="constants.ForwardConst" %>

<c:set var="actFol" value="${ForwardConst.ACT_FOL.getValue()}" />
<c:set var="commShow" value="${ForwardConst.CMD_SHOW.getValue()}" />
<c:set var="commIdx" value="${ForwardConst.CMD_INDEX.getValue()}" />

<c:import url="../layout/app.jsp">
    <c:param name="content">
        <c:if test="${flush != null}">
            <div id="flush_success">
                <c:out value="${flush}"></c:out>
            </div>
        </c:if>
        <h2>フォローした従業員　一覧</h2>
        <c:choose>
            <c:when test="${follows_count_mine == 0 || follows_count_mine == null}}">
                <h3>フォローしている従業員はいません。</h3>
            </c:when>
            <c:otherwise>
                <table id="employee_list">
                    <tbody>
                        <tr>
                            <th>社員番号</th>
                            <th>氏名</th>
                            <th>日報</th>
                        </tr>
                        <c:forEach var="follow" items="${follows}" varStatus="status">
                            <tr class="row${status.count % 2}">
                                <td><c:out value="${follow.followed.code}" /></td>
                                <td><c:out value="${follow.followed.name}" /></td>
                                <td>
                                    <a href="<c:url value='?action=${actFol}&command=${commShow}&id=${follow.followed.id}' />">詳細を見る</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <div id="pagination">
            （全 ${follows_count_mine} 件）<br />
            <c:forEach var="i" begin="1" end="${((follows_count_mine - 1) / maxRow) + 1}" step="1">
                <c:choose>
                    <c:when test="${i == page}">
                        <c:out value="${i}" />&nbsp;
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value='?action=${actFol}&command=${commIdx}&page=${i}' />"><c:out value="${i}" /></a>&nbsp;
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
    </c:param>
</c:import>