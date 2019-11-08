<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Post a comment</title>
    <jsp:include page="common.jsp"/>
</head>
<body>
<div class="container">
    <%--FIXME: OWASP A5:2017 - Broken Access Control
        This page must not be accessible to unauthenticated users
    --%>
    <%--FIXME: OWASP A2:2017 - Broken Authentication
        Session data about the currently authenticated user is retrieved from cookies
    --%>

    <h1 class="row">Post a comment</h1>

    <hr>

    <h4 class="row">Please note that comments are moderated.</h4>

    <div class="row">
        <div class="colm-md-12">
            They will appear in the <a href="guestbook.jsp">Guestbook</a>
            once they are approved by a system administrator.
        </div>
    </div>

    <br/>

    <form method="post" action="comment.do">
        <label for="comment"></label>

        <%--FIXME: OWASP A5:2017 - Broken Access Control --%>
        <input type="hidden" name="username" value="${cookie['username'].value}">

        <textarea id="comment" name="comment"
                  class="form-control row"
                  maxlength="500"
                  placeholder="Enter your comment here"
                  rows="5"></textarea>
        <br/><br/>
        <input type="submit" class="btn btn-success btn-lg row"/>
    </form>
</div>
</body>
</html>