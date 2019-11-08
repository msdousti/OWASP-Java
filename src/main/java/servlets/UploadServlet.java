package servlets;

import models.User;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

@WebServlet("/upload.do")
@MultipartConfig
public class UploadServlet extends HttpServlet {

    private static final long serialVersionUID = 5997268144819366374L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException, ServletException {

        //FIXME: OWASP A10:2017 - Insufficient Logging & Monitoring

        ServletOutputStream out = response.getOutputStream();

        Part filePart = request.getPart("xfile");
        InputStream is = filePart.getInputStream();

        ObjectInputStream ois = new ObjectInputStream(is);

        User u;

        try {
            //FIXME: OWASP A8:2017 - Insecure Deserialization
            //  Susceptible to RCE (Remote Code Execution)
            //  Notice: The following JAR must be in the classpath of the server:
            //  "commons-collections-3.2.1.jar"
            Object o = ois.readObject();

            u = (User) o;
        } catch (ClassNotFoundException | ClassCastException e) {
            out.println(e.getMessage());
            return;
        }

        //FIXME: OWASP A3:2017 - Sensitive Data Exposure
        out.println("username: " + u.username);
        out.println("role: " + u.role);
        out.println("password: " + u.password);
        out.println("created_at: " +
                ((u.created_at == null) ? "null" : u.created_at.toString()));
        out.println("updated_at: " +
                ((u.updated_at == null) ? "null" : u.updated_at.toString()));
    }
}
