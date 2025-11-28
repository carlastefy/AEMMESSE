package controller.utente.ordine;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.carrelloService.Carrello;
import model.ordineService.Ordine;
import model.ordineService.OrdineDAO;
import model.utenteService.Utente;

import java.io.IOException;
import java.util.List;

@WebServlet("/visualizza-ordini")
public class VisualizzaOrdini extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        //metto gli ordini dell'utente in sessione così da poter
        //gestire meglio la stampa
        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        if(Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
        }
        final OrdineDAO ordineDAO = new OrdineDAO();
        final List<Ordine> ordini = ordineDAO.doRetrieveByUtente(utente.getEmail());
        session.setAttribute("ordini", ordini);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/areaPservices/visualizzaOrdini.jsp");
        dispatcher.forward(request, response);
    }
}
