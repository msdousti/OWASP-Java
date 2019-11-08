<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>User homepage</title>
    <jsp:include page="common.jsp"/>
    <style>
        .row {
            margin-top: 20px;
        }
    </style>
</head>
<body>

<div class="container">

    <%--FIXME: OWASP A5:2017 - Broken Access Control
        This page must not be accessible to unauthenticated users
    --%>
    <%--FIXME: OWASP A2:2017 - Broken Authentication
        Session data about the currently authenticated user is retrieved from cookies
    --%>

    <%--FIXME: OWASP A7:2017 - Cross-Site Scripting (XSS)
        Adversary can send a request to this page,
        and set the header to something which includes a rogue cookie like:
            Cookie: username=kambiz<script>alert(45)</script>; password=1; role=admin
    --%>
    <c:set var="username" value="${cookie['username'].value}" scope="page"/>
    <c:set var="password" value="${cookie['password'].value}" scope="page"/>
    <c:set var="role" value="${cookie['role'].value}" scope="page"/>


    <div class="row">
        <h1 class="col-md-6">Welcome!</h1>
        <div class="col-md-3">
            <a href="logout.do">
                <button class="btn btn-lg btn-danger">Logout</button>
            </a>
        </div>
    </div>

    <hr>

    <p>&nbsp;</p>

    <div class="row">
        <div style="font-weight: bold;" class="col-md-3">Your username is:</div>
        <div class="col-md-3">${username}</div>
        <div class="col-md-6">
            <a href="comment.jsp">
                <button class="btn btn-primary">Post a comment</button>
            </a>
            <a href="<%= response.encodeURL("guestbook.jsp") %>">
                <button class="btn btn-success">View guestbook</button>
            </a>
        </div>
    </div>

    <div class="row">
        <%--FIXME: OWASP A3:2017 - Sensitive Data Exposure
            Sensitive information is added to DOM (Document Object Model)
            It's accessible by JavaScript, even if the cookie isn't
        --%>
        <div style="font-weight: bold;" class="col-md-3">Your wpassword is:</div>
        <div class="col-md-3">${password}</div>
        <div class="col-md-3">
            <a href="<%= response.encodeURL("change_pass.jsp") %>" >
                <button class="btn btn-dark">Change Password</button>
            </a>
        </div>
    </div>

    <div class="row">
        <div style="font-weight: bold;" class="col-md-6">
            General Data Protection Regulation
        </div>
        <div class="col-md-3">
            <a href="gdpr.jsp">
                <button class="btn btn-info">Manage GDPR</button>
            </a>
        </div>
    </div>

    <div class="row">
        <div style="font-weight: bold;" class="col-md-3">Your role is:</div>
        <div class="col-md-3">${role}</div>

        <%--FIXME: OWASP A5:2017 - Broken Access Control
            Access is granted based on a cookie
        --%>
        <c:if test="${role.equals('admin')}">
            <div class="col-md-3">
                <a href="admin.jsp">
                    <button class="btn btn-warning">Administrative Tasks</button>
                </a>
            </div>
        </c:if>
    </div>

</div>
</body>
</html>