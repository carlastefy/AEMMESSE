package controller.utente.ordine;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.carrelloService.RigaCarrello;
import model.libroService.Libro;
import model.libroService.LibroDAO;
import model.libroService.Sede;
import model.libroService.SedeDAO;
import model.utenteService.Utente;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/procedi-ordine")
public class ProcediOrdine extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        if(Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
        }
        final LibroDAO libroDAO = new LibroDAO();
        final SedeDAO sedeDAO = new SedeDAO();

        final List<Sede> sedi = sedeDAO.doRetrivedAll(); //tutte le sedi che abbiamo
        final List<Sede> sediDaAggiungere = sedeDAO.doRetrivedAll();
        final List<RigaCarrello> righe = (List<RigaCarrello>) session.getAttribute("righeDisponibili");

        if(!righe.isEmpty()){
            for(final RigaCarrello r : righe){
                final Libro l = r.getLibro();
                //prendo le sedi di ogni libro
                final List<Sede> sedeLibro = libroDAO.getPresenzaSede(l.getIsbn());
                for(final Sede s : sedi){
                    //se un libro non ha una delle sedi non la rendo visibile al momento della scelta dell'indirizzo
                    if(!(sedeLibro.contains(s)))
                        sediDaAggiungere.remove(s);
                }
            }
        }

        //per la questione sedi non sono molto sicura...perchè si potrebbe anche far arrivare il libro in una sede senza che
        //esso sia già disponibile in quella sede. Da valutare !!!
        request.setAttribute("sedi", sediDaAggiungere);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/procediOrdine.jsp");
        dispatcher.forward(request, response);
    }
}