package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import model.libroService.Reparto;
import model.libroService.RepartoDAO;

import java.util.List;

@WebServlet(name="MyInit", urlPatterns = "/MyInit", loadOnStartup = 0)
public class InitServlet extends HttpServlet {

    public void init() throws ServletException{

        final RepartoDAO service = new RepartoDAO();
        final List<Reparto> reparti = service.doRetrivedAll();
        getServletContext().setAttribute("reparti", reparti);
        super.init();
    }
}
