<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>GDPR</title>
    <jsp:include page="common.jsp"/>
</head>
<body>

<div class="container">
    <div class="row">
        <h1 class="col-md-12">
            GDPR: General Data Protection Regulation
        </h1>
    </div>

    <hr/>

    <div class="row" style="color: #e83e8c;">
        <h3 class="col-md-12">
            Based on European Union privacy law, called
            <a href="https://en.wikipedia.org/wiki/General_Data_Protection_Regulation">GDPR</a>,
            you can request a copy of all your data that we store.
        </h3>
    </div>

    <hr/>

    <div class="row">
        <h4 class="col-md-12">
            Click <a href="serialize.do?username=${cookie['username'].value}">here</a>
            to download a copy of your data.
        </h4>
    </div>

    <div class="row">
        <div class="col-md-12">
            Notice that to further protect your privacy,
            this copy is encoded in our <b>proprietary</b> format.
        </div>
    </div>

    <hr/>

    <div class="row">
        <h4 class="col-md-12">
            Upload a previously downloaded copy of your data.
        </h4>
    </div>

    <div class="row">
        <div class="col-md-12">
            We will decode our <b>proprietary</b> format,
            and show you the information inside!
        </div>
    </div>

    <br/>

    <form action="upload.do" method="post" enctype="multipart/form-data">
        <div class="form-group row">
            <div class="col-md-4">
                <div class="custom-file">
                    <input type="file" accept=".ser" class="custom-file-input" id="xfile" name="xfile" required>
                    <label class="custom-file-label" for="xfile">Choose file...</label>
                </div>
            </div>
            <div class="col-md-4">
                <button class="btn btn-primary" type="submit">Submit file</button>
            </div>
        </div>
    </form>

    <script>
        $('#xfile').on('change', function () {
            //get the file name
            var fileName = $(this).val();
            //replace the "Choose a file" label
            $(this).next('.custom-file-label').html(fileName);
        })
    </script>


</div>
</body>
</html>
