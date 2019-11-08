package servlets;

import org.jetbrains.annotations.Nullable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Optional;
import java.util.logging.Logger;

@WebServlet("/admin.do")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 4501855365314172264L;
    private static DataSource ds;

    private Logger logger = Logger.getLogger(getClass().getName());

    static {
        try {
            InitialContext ctx = new InitialContext();
            //FIXME: OWASP A5:2017 - Broken Access Control (root privileges)
            ds = (DataSource) ctx.lookup("jdbc/MySQL_root_DataSource");
        } catch (NamingException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        //FIXME: OWASP A5:2017 - Broken Access Control
        String role = getCookieByName(request, "role");
        if (!"admin".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "You must be a system admin!");
            return;
        }

        logger.info("Received request from " + request.getRemoteAddr());

        StringBuilder query = new StringBuilder();
        StringBuilder list = new StringBuilder();
        query.append("UPDATE guestbook SET approved = (CASE id ");

        Enumeration<String> paramIds = request.getParameterNames();
        int count = 0;

        while (paramIds.hasMoreElements()) {
            String id = paramIds.nextElement();
            String val = request.getParameter(id);
            query.append(String.format("WHEN '%s' THEN '%s' ",
                    id, val));
            list.append(String.format("'%s', ", id));
            count++;
        }

        if (count == 0) {
            response.sendRedirect("admin.jsp");
            return;
        }

        // Remove the extra ", " from list
        list.delete(list.length() - 2, list.length());
        query.append(String.format("END) WHERE id IN (%s)", list));

        logger.info("Query: " + query);

        try (Connection connection = ds.getConnection()) {

            Statement st = connection.createStatement();

            //FIXME: OWASP A10:2017 - Insufficient Logging & Monitoring
            // return value not logged
            //FIXME: OWASP A1:2017 - Injection
            //FIXME: OWASP A8:2013 - CSRF
            st.executeUpdate(query.toString());

            response.sendRedirect("admin.jsp");

        } catch (SQLException sqlException) {
            logger.warning(sqlException.getMessage());
        }
    }

    @SuppressWarnings("SameParameterValue")
    @Nullable
    private String getCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return null;
        Optional<Cookie> optional = Arrays.stream(cookies)
                .filter(x -> x.getName().equals(name))
                .findFirst();
        return optional.map(Cookie::getValue).orElse(null);
    }
}