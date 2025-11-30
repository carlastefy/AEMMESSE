package controller.HomePage;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.carrelloService.Carrello;
import model.carrelloService.RigaCarrello;
import model.libroService.Libro;
import model.libroService.LibroDAO;
import model.libroService.Reparto;
import model.libroService.RepartoDAO;
import model.utenteService.Utente;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/index.html")
public class HomePageServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        // Memorizza il carrello nella sessione
        // Ottieni la sessione corrente, creandone una nuova se non esiste
        final HttpSession session = request.getSession();

        Carrello carrello = (Carrello) session.getAttribute("carrello");// Verifica se il carrello è già presente nella sessione
        final Utente utente=(Utente) session.getAttribute("utente");

        if(Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
            return;
        }


        if (carrello == null) { // Se il carrello non è presente nella sessione, ne crea uno nuovo
            carrello = new Carrello();
            carrello.setRigheCarrello(new ArrayList<>());//l'ho aggiunto per far funzionare AggiungiCartServlet
            session.setAttribute("carrello", carrello);// Aggiungi il carrello alla sessione
        }

        if(request.getAttribute("libriHome")==null) {
            final List<Reparto> reparti = (List<Reparto>) getServletContext().getAttribute("reparti");
            if (reparti != null) {
                for (final Reparto reparto : reparti) {
                    if (reparto != null && "Libri di Tendenza".equals(reparto.getNome())) {
                        final List<Libro> libriHome = reparto.getLibri();
                        request.setAttribute("libriHome", libriHome);
                        break; // trovato, esco
                    }
                }
            }
        }

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/homepage.jsp");
        dispatcher.forward(request, response);

    }
}
