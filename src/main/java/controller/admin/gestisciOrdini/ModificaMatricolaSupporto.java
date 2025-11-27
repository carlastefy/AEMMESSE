package controller.admin.gestisciOrdini;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.gestoreService.Gestore;
import model.gestoreService.GestoreDAO;
import model.ordineService.Ordine;
import model.ordineService.OrdineDAO;
import model.utenteService.Utente;
import model.utenteService.UtenteDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/modifica-matricola")
public class ModificaMatricolaSupporto extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final GestoreDAO gestoreDAO = new GestoreDAO();
        final OrdineDAO ordineDAO = new OrdineDAO();
        final UtenteDAO utenteDAO = new UtenteDAO();

        final Utente utente = utenteDAO.doRetrieveById(request.getParameter("utenteScelto"));
        final String matricola =request.getParameter("matricolaAttuale");
        final String idOrdine = request.getParameter("ordineID");
        final Ordine ordine = ordineDAO.doRetrieveById(idOrdine);
        final List<Gestore> gestori = gestoreDAO.doRetrivedAll();

        //elimino la matricola a cui era stato affidato l'ordine poichè se decido di cambiarla vuol dire che non è disponibile
        final int size = gestori.size();
        for(int i = 0; i< size; ++i){
            if(gestori.get(i).getMatricola().equalsIgnoreCase(matricola)){
                gestori.remove(i);
                break;
            }
        }

        request.setAttribute("ordineAttuale", ordine);
        request.setAttribute("gestori", gestori);
        request.setAttribute("utenteScelto", utente);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/ordini/modificaMatricola.jsp");
        dispatcher.forward(request, response);
    }
}
