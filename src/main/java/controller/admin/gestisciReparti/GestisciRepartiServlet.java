package controller.admin.gestisciReparti;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.Reparto;
import model.libroService.RepartoDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/gestisci-reparti")
public class GestisciRepartiServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final RepartoDAO repartoService = new RepartoDAO();
        final List<Reparto> reparti = repartoService.doRetrivedAll();
        request.setAttribute("reparti", reparti);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/reparti/gestisciReparti.jsp");
        dispatcher.forward(request, response);
    }


}
