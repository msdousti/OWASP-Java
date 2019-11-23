package servlets;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

@WebServlet("/login.do")
public class LoginServlet extends HttpServlet {
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

        // FIXME: implement this kind of logging in a filter
        logger.info("Received request from " + request.getRemoteAddr());

        String userParam = request.getParameter("username");
        String passParam = request.getParameter("password");

        //FIXME: OWASP A7:2017 - Cross-Site Scripting (XSS)
        // Category: Reflected XSS (AKA Non-Persistent or Type II)
        // Category: Server XSS

        // Resolution 1: Use Content-Security-Policy (CSP)
        // Resolution 2: Sanitize input (as always!)
        if (userParam == null || passParam == null) {
            response.setContentType("text/html; charset=UTF-8");

            // NOTE: Internet Explorer, Chrome and Safari have a builtin "XSS filter" to prevent this.
            //   Update: Newer versions of these browser are dropping support for this header:
            //   * Edge: https://textslashplain.com/2018/11/06/an-update-on-the-edge-xss-filter/
            //   * Chrome: https://www.chromium.org/developers/design-documents/xss-auditor

            // For IE, "X-XSS-Protection" must be enabled, as shown below:
            response.setHeader("X-XSS-Protection", "1; mode=block");

            // Firefox, however, does not prevent reflected XSS.
            // See "Firefox - X-XSS-Protection Support.txt" for more info!

            response.getWriter().printf("Either username or password is not provided.\n" +
                            "Please check your input:\n" +
                            "Username = %s\n" +
                            "Password = %s",
                    userParam, passParam);

            return;
        }

        //FIXME: OWASP A1:2017 - Injection
        //   * See "LoginXServlet.java" for an implementation via "Prepared Statements"
        //FIXME: Use "LIMIT 1" at the end of query to improve performance
        String query = String.format("select * from users " +
                        "where username = '%s' " +
                        "and password = '%s'",
                userParam, passParam);


        //FIXME: OWASP A3:2017 - Sensitive Data Exposure
        logger.info("Query: " + query);

        String username, password, role;

        try (Connection connection = ds.getConnection()) {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);

            if (!rs.next()) {
                logger.warning("User not found!");

                response.sendRedirect(response.encodeRedirectURL("failed.jsp"));
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

        // FIXME: Prevent session fixation
        // HttpSession session = request.getSession();
        // session.invalidate();

        //FIXME: OWASP A2:2017 - Broken Authentication
        //  Parameter "Remember me" is not observed
        //  Cookie security settings (httpOnly, secure, age, domain, path, same-site)
        //  For same-site, see: https://stackoverflow.com/a/43106260/459391
        //      response.setHeader("Set-Cookie", "key=value; HttpOnly; Secure; SameSite=strict")

        //FIXME: OWASP A5:2017 - Broken Access Control
        //  Cookie used without any signature
        //  Better yet: This info can be set in user's session at the server side
        Cookie uCookie = new Cookie("username", username);
        response.addCookie(uCookie);

        //FIXME: OWASP A5:2017 - Broken Access Control
        //  Cookie used without any signature
        //FIXME: OWASP A3:2017 - Sensitive Data Exposure
        //  Password stored as plaintext on client-side. No usecase scenario needs this!
        Cookie pCookie = new Cookie("password", password);
        response.addCookie(pCookie);

        //FIXME: OWASP A5:2017 - Broken Access Control
        //  Cookie used without any signature
        //  Better yet: This info can be set in user's session at the server side
        Cookie rCookie = new Cookie("role", role);
        response.addCookie(rCookie);

        response.sendRedirect(response.encodeRedirectURL("user.jsp"));
    }
}