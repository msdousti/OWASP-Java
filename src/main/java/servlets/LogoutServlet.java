package servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout.do")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = -7529404061769958490L;


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        //FIXME: OWASP A2:2017 - Broken Authentication
        HttpSession session = request.getSession();
        session.invalidate();
        response.sendRedirect("index.jsp");
    }

    private void deleteCookies(HttpServletRequest request,
                               HttpServletResponse response) {
        for (Cookie c : request.getCookies()) {
            c.setMaxAge(0);
            c.setValue(null);
            response.addCookie(c);
        }
    }
}