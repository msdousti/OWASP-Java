<%@ page isErrorPage="true" %>

<div style="font-family: monospace">
    =========== Oh No! ===========
    <pre>
<% exception.printStackTrace(new java.io.PrintWriter(pageContext.getOut())); %>
    </pre>
</div>