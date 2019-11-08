package servlets;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

@WebServlet("/loginx.do")
public class LoginXServlet extends HttpServlet {
    private static final long serialVersionUID = -1813590570829849128L;
    private static DataSource ds;

    private Logger logger = Logger.getLogger(getClass().getName());

    static {
        try {
            InitialContext ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/MySQL_readonly_DataSource");
        } catch (NamingException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        logger.info("Received request from " + request.getRemoteAddr());
        String userParam = request.getParameter("username");
        String passParam = request.getParameter("password");

        String username, password, role;

        try (Connection connection = ds.getConnection()) {

            // Prepared statements are NOT susceptible to SQL Injection
            PreparedStatement pstmt = connection.prepareStatement(
                    "select * from users where username = ? and password = ? LIMIT 1");

            pstmt.setString(1, userParam);
            pstmt.setString(2, passParam);

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                logger.info("User not found!");

                response.sendRedirect("failed.jsp");
                return;
            }

            username = rs.getString("username");
            password = rs.getString("password");
            role = rs.getString("role");

            logger.info("User found.");

        } catch (SQLException sqlException) {
            logger.warning(sqlException.getMessage());
            response.sendRedirect("failed.jsp");
            return;
        }

        //FIXME: OWASP A2:2017 - Broken Authentication
        //  Parameter "Remember me" is not observed
        //  Cookie security settings (httpOnly, secure, age, domain, path, same-site)
        //  For same-site, see: https://stackoverflow.com/a/43106260/459391
        //      response.setHeader("Set-Cookie", "key=value; HttpOnly; SameSite=strict")

        //FIXME: OWASP A5:2017 - Broken Access Control
        //  Cookie used without any signature
        Cookie uCookie = new Cookie("username", username);
        response.addCookie(uCookie);

        //FIXME: OWASP A5:2017 - Broken Access Control
        //  Cookie used without any signature
        //FIXME: OWASP A3:2017 - Sensitive Data Exposure
        //  Password stored as plaintext on client-side
        Cookie pCookie = new Cookie("password", password);
        response.addCookie(pCookie);

        //FIXME: OWASP A5:2017 - Broken Access Control
        //  Cookie used without any signature
        Cookie rCookie = new Cookie("role", role);
        response.addCookie(rCookie);

        response.sendRedirect("user.jsp");
    }
}