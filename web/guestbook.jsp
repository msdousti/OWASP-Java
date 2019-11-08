<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>

<html>
<head>
    <title>Guestbook</title>
    <jsp:include page="common.jsp"/>
</head>
<link rel="stylesheet"
      href="static/css/custom.css">
<body>
<div class="container">
    <sql:query dataSource="jdbc/MySQL_readonly_DataSource" var="result">
        SELECT u.username, gb.comment, gb.created_at
        FROM guestbook gb
        JOIN users u ON gb.userId = u.id
        WHERE gb.approved = 1
    </sql:query>

    <h1 class="row">Guestbook</h1>

    <hr>

    <h3 class="row">What do our customers say?</h3>
    <p class="row">
        (Note: This list only contains comments
        approved by a site administrator)
    </p>
    <br/>

    <div class="row th tr">
        <div class="col-md-2">Username</div>
        <div class="col-md-8">Comment</div>
        <div class="col-md-2">Created At</div>
    </div>

    <c:forEach var="row" items="${result.rows}">
        <div class="row tr">
            <div class="col-md-2"><c:out value="${row.username}"/></div>
            <div class="col-md-8"><c:out value="${row.comment}"/></div>
            <div class="col-md-2"><c:out value="${row.created_at}"/></div>
        </div>
    </c:forEach>

    <br/><br/>

    <p class="row">
        <c:choose>
            <c:when test="${cookie['username'] != null}">
                <a href="comment.jsp">
                    <button class="btn btn-lg btn-primary">
                        Post a comment
                    </button>
                </a>
            </c:when>
            <c:otherwise>
                <a href="index.jsp">
                    <button class="btn btn-lg btn-danger">
                        Login to add a comment
                    </button>
                </a>
            </c:otherwise>
        </c:choose>
    </p>

</div>
</body>
</html>