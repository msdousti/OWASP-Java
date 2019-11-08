<html>
<head>
    <title>Change Password</title>
    <jsp:include page="common.jsp"/>
</head>
<body>
<div class="container">
    <h1 class="row">Change You Password</h1>

    <hr>

    <%--FIXME: OWASP A3:2017 - Sensitive Data Exposure
        1) URLs are often logged by web servers.
           Sensitive data such as passwords must not be included in URLs.
           Use POST method!
        2) Use TLS.
    --%>
    <form id="frm" action="pwd.do" method="get">
        <%--FIXME: OWASP A2:2017 - Broken Authentication
            Username is determined based on client-provided information
        --%>
        <input type="hidden"
               name="username" id="username"
               value="${cookie['username'].value}">

        <div class="form-group">
            <label for="old">Old Password:</label>
            <input class="form-control" type="password"
                   name="old" id="old"
                   placeholder="Old Password">
        </div>

        <div class="form-group">
            <label for="password">New Password:</label>
            <input class="form-control" type="password"
                   name="password" id="password"
                   placeholder="New password">
        </div>

        <div class="form-group">
            <label for="confirm">Confirm Password:</label>
            <input class="form-control" type="password"
                   name="confirm" id="confirm"
                   placeholder="Confirm password">
        </div>

        <button type="submit" class="btn btn-warning btn-lg">Submit</button>
    </form>

    <script>
        var frm = $("#frm");
        var cookie_pwd = Cookies.get("password");

        frm.submit(function (event) {
            var old = $("#old").val();
            var password = $("#password").val();
            var confirm = $("#confirm").val();

            <%--FIXME: OWASP A5:2017 - Broken Access Control
                Password confirmation is only performed on client side
            --%>
            if (old !== cookie_pwd) {
                bootbox.alert("Invalid old password!");
                event.preventDefault();
                return;
            }

            if (password.length === 0) {
                bootbox.alert("Please enter a password!");
                event.preventDefault();
                return;
            }

            if (password !== confirm) {
                bootbox.alert("Confirmation doesn't match the password!");
                event.preventDefault();
            }
        })
    </script>
</div>
</body>
</html>