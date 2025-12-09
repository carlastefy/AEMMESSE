package controller.utente;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.carrelloService.Carrello;
import model.carrelloService.RigaCarrello;
import model.libroService.Libro;
import model.libroService.LibroDAO;
import model.utenteService.Utente;

import java.io.IOException;
import java.util.List;

@WebServlet("/aggiungi-carrello")
public class AggiungiCartServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String isbn = request.getParameter("isbn");
        final String source= request.getParameter("source");
        final String position = request.getParameter("position");

        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        if(Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
        }
       // Utente utente = (Utente) session.getAttribute("utente");
        final Carrello carrello = (Carrello) session.getAttribute("carrello");

        String address="index.html";
        if(source!= null) { //ho aggiunto il controllo per source potrebbe essere null
            if (source.equals("mostraLibro"))
                address = "mostra-libro";
            else if (source.equals("riepilogoOrdine"))
                address = "riepilogo-ordine";
            else if(source.equals("ricerca")) {
                address = "ricerca-servlet?q="+request.getParameter("q");
            }else if(source.equals("aggiungi-carrello")) {
                address = "mostra-reparto";

            }
        }

        final LibroDAO libroService = new LibroDAO();
        final Libro libro = libroService.doRetrieveById(isbn);

        final List<RigaCarrello> righeCarrello = carrello.getRigheCarrello();

        boolean flag = true; // libro non presente
        if(!righeCarrello.isEmpty()) {
            final int righeSize = righeCarrello.size();
            for (int i = 0; i < righeSize && flag; ++i) {
                final RigaCarrello rigaCarrello = righeCarrello.get(i);
                final Libro libroRiga = rigaCarrello.getLibro(); // libro della rigaCarrello
                if (libroRiga.equals(libro)) {
                    rigaCarrello.setQuantita(rigaCarrello.getQuantita() + 1); // libro presente, incremento la quantità
                    flag = false; // libro presente
                }
            }

        }
        if (flag) { // se il libro non è presente, lo aggiungo
            final RigaCarrello riga = new RigaCarrello();
            riga.setIdCarrello(carrello.getIdCarrello());
            riga.setLibro(libro);
            riga.setQuantita(1);
            righeCarrello.add(riga);
        }


            session.setAttribute("carrello", carrello);

        if (position != null) {
            address += "#" + position;
        }

        //response.sendRedirect(address);//supporta l'ancoraggio*/
        final RequestDispatcher dispatcher = request.getRequestDispatcher(address);
            dispatcher.forward(request, response);

    }

}
