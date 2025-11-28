package controller.utente.ordine;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ordineService.OrdineDAO;
import model.ordineService.RigaOrdine;
import model.utenteService.Utente;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@WebServlet("/rimuovi-ordine")
public class RimuoviDaOrdine extends HttpServlet {
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        if(Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
        }
        final OrdineDAO ordineDAO = new OrdineDAO();

        // Ottiene i parametri dalla richiesta
        final String isbn = request.getParameter("isbn");
        final String idOrdine = (request.getParameter("idOrdine"));

        // Ottiene l'ordine dalla sessione
        final List<RigaOrdine> righeOrdine = (List<RigaOrdine>) ordineDAO.doRetrieveById(idOrdine).getRigheOrdine();

        // Rimuove la riga d'ordine corrispondente
        if (isbn != null && !righeOrdine.isEmpty()) {
            final Iterator<RigaOrdine> iterator = righeOrdine.iterator();
            while (iterator.hasNext()) {
                final RigaOrdine riga = iterator.next();
                if (riga.getLibro().getIsbn().equals(isbn) && riga.getIdOrdine().equals(idOrdine)) {
                    iterator.remove();
                    break;
                }
            }
        }

        // Reindirizza alla pagina di riepilogo dell'ordine
        response.sendRedirect("riepilogoOrdine.jsp");
    }
}
