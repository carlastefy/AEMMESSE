package controller.admin.gestisciSedi;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.Sede;
import model.libroService.SedeDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/gestisci-sedi")
public class GestisciSediServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final SedeDAO sedeService = new SedeDAO();
        final List<Sede> sedi = sedeService.doRetrivedAll();
        request.setAttribute("sedi", sedi);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/sedi/gestisciSedi.jsp");
        dispatcher.forward(request, response);
    }


}