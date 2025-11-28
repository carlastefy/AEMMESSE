package controller.utente.ordine;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ordineService.Ordine;
import model.ordineService.OrdineDAO;
import model.ordineService.RigaOrdine;
import model.ordineService.RigaOrdineDAO;
import model.utenteService.Utente;

import java.io.IOException;
import java.util.List;

@WebServlet("/riepilogo-ordine")
public class RiepilogoOrdine extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        if(Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
        }
        final String idOrdine = request.getParameter("idOrdine");
        final OrdineDAO ordineDAO = new OrdineDAO();
        final Ordine ordine = ordineDAO.doRetrieveById(idOrdine);
        session.setAttribute("ordine", ordine);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/areaPservices/riepilogoOrdine.jsp");
        dispatcher.forward(request, response);
    }
}
