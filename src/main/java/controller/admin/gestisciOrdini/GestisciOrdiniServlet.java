package controller.admin.gestisciOrdini;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.utenteService.Utente;
import model.utenteService.UtenteDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/gestisci-ordini")
public class GestisciOrdiniServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final UtenteDAO utenteDAO = new UtenteDAO();
        final List<Utente> utenti = utenteDAO.doRetrieveAll();
        utenti.removeIf(Validator::checkIfUserAdmin);

        request.setAttribute("utenti", utenti);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/ordini/gestisciOrdini.jsp");
        dispatcher.forward(request, response);
    }
}
