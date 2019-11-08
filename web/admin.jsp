<%@ page import="java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql" %>

<%
//    response.setHeader("Content-Security-Policy", "script-src 'self'");
%>

<html>
<head>
    <%--<meta http-equiv="Content-Security-Policy" content="script-src 'self'">--%>
    <title>Administration</title>
    <jsp:include page="common.jsp"/>
    <link rel="stylesheet"
          href="static/css/custom.css">
</head>
<body>
<div class="container">
    <%--FIXME: OWASP A5:2017 - Broken Access Control
        This page must not be accessible to unauthenticated users
    --%>
    <%--FIXME: OWASP A2:2017 - Broken Authentication
        Session data about the currently authenticated user is retrieved from cookies
    --%>

    <h1 class="row">Administration</h1>

    <hr>

    <p>&nbsp;</p>
    <h4 class="row">Comments awaiting moderator approval:</h4><br/>

    <sql:query dataSource="jdbc/MySQL_readonly_DataSource" var="result">
        SELECT u.username, gb.*
        FROM guestbook gb
        JOIN users u ON gb.userId = u.id
        WHERE gb.approved = 0
    </sql:query>

    <div class="row th tr">
        <div class="col-md-2">Approve?</div>
        <div class="col-md-2">Username</div>
        <div class="col-md-6">Comment</div>
        <div class="col-md-2">Created At</div>
    </div>

    <form action="admin.do">

        <c:forEach var="row" items="${result.rows}">
            <div class="row tr">
                <div class="col-md-2">
                    <label>
                        <select class="form-control" name="${row.id}">
                            <option value="0">No</option>
                            <option value="1">Yes</option>
                            <option value="2">Reject</option>
                        </select>
                    </label>
                </div>
                <%
                    Map map = (Map) pageContext.findAttribute("row");
                %>

                    <%--FIXME: OWASP A7:2017 - Cross-Site Scripting (XSS)--%>
                    <%--Category: Stored XSS (AKA Persistent or Type I)--%>
                    <%--Category: Server XSS--%>
                    <%--Resolution 1: Use "Expression Language (EL)" instead of "scriptlets".
                        ${row.username}
                        ${row.comment}
                        ${row.created_at}
                    --%>
                    <%--Resolution 2: Use Content-Security-Policy (CSP)
                        See above this page
                    --%>
                    <%--Resolution 3: Sanitize input (as always!)--%>


                <div class="col-md-2">
                    <%= map.get("username")%>
                </div>
                <div class="col-md-6">
                    <%= map.get("comment")%>
                </div>
                <div class="col-md-2">
                    <%= map.get("created_at")%>
                </div>
            </div>
        </c:forEach>

        <br/>
        <span class="row">
                <button type="submit" class="btn btn-primary">Submit</button>
        </span>

    </form>

</div>

</body>
</html>