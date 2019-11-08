package servlets;

import models.User;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

@WebServlet("/serialize.do")
public class SerializeServlet extends HttpServlet {

    private static final long serialVersionUID = -3258410676759788999L;

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
                         HttpServletResponse response) throws IOException {

        ServletOutputStream out = response.getOutputStream();

        //FIXME: OWASP A5:2017 - Broken Access Control
        String username = request.getParameter("username");
        if (username == null || "".equals(username)) {
            logger.warning("Empty username.");
            out.println("Empty username.");
            return;
        }

        User user;

        try (Connection connection = ds.getConnection()) {

            // Prepared statements are NOT susceptible to SQL Injection
            PreparedStatement pstmt = connection.prepareStatement(
                    "select * from users where username = ? LIMIT 1");

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                logger.warning("Username not found!");
                //FIXME: OWASP A3:2017 - Sensitive Data Exposure
                out.println("Username not found!");
                return;
            }

            user = new User(
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("password"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
            );

        } catch (SQLException e) {
            logger.warning(e.getMessage());
            //FIXME: OWASP A3:2017 - Sensitive Data Exposure
            out.println(e.getMessage());
            return;
        }

        try (ObjectOutputStream objectOutputStream =
                     new ObjectOutputStream(response.getOutputStream())) {
            objectOutputStream.writeObject(user);
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=\"%s.ser\"", username));
        } catch (IOException e) {
            logger.warning(e.getMessage());
            //FIXME: OWASP A3:2017 - Sensitive Data Exposure
            out.println(e.getMessage());
        }
    }
}
