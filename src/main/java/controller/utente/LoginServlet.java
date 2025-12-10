package controller.utente;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.carrelloService.Carrello;
import model.carrelloService.CarrelloDAO;
import model.carrelloService.RigaCarrello;
import model.utenteService.Utente;
import model.utenteService.UtenteDAO;
import model.wishList.WishList;
import model.wishList.WishListDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/login-servlet")

public class LoginServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        //controllo dei form
        final String email = request.getParameter("email");
        final String password = request.getParameter("pw");
        if((email==null || email.isEmpty() || !email.contains("@")) || (password== null || (password.isEmpty()) || password.length()>16)){
            response.sendRedirect("/WEB-INF/errorJsp/loginError.jsp");
        }
        else {
            Utente utente = new Utente();
            utente.setEmail(email);
            utente.setCodiceSicurezza(password);

            final UtenteDAO service = new UtenteDAO();
            if (service.doRetrieveByEmailPassword(utente.getEmail(), utente.getCodiceSicurezza()) == null) {
                final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/errorJsp/loginError.jsp");
                dispatcher.forward(request, response);
            } else {
                final HttpSession session = request.getSession();
                utente = service.doRetrieveById(email);
                session.setAttribute("utente", utente);

                final Carrello carrelloLocale = (Carrello) session.getAttribute("carrello");// Recupera il carrello locale dalla sessione
                final List<RigaCarrello> righeLocali = carrelloLocale.getRigheCarrello();

                final CarrelloDAO carrelloService = new CarrelloDAO();
                Carrello carrelloDb = null;

                if (carrelloService.doRetriveByUtente(utente.getEmail()) != null) {
                    carrelloDb = carrelloService.doRetriveByUtente(utente.getEmail());// Recupera il carrello dal database
                    final List<RigaCarrello> rigaCarrelloDb = carrelloDb.getRigheCarrello();

                    if (righeLocali != null) {
                        // Fusiona i carrelli
                        final int sizeLocali = righeLocali.size();
                        final int sizeDb = rigaCarrelloDb.size();
                        for (int i = 0; i < sizeLocali; ++i) {
                            final RigaCarrello riga = righeLocali.get(i);
                            boolean flag = true;//non presente
                            for (int j = 0; j < sizeDb && flag; ++j) {
                                final RigaCarrello riga2 = rigaCarrelloDb.get(j);
                                if (riga2.getLibro().getIsbn().equals(riga.getLibro().getIsbn())) { //se l'isbn è già presente nel carrello del DB
                                    riga2.setQuantita(riga2.getQuantita() + riga.getQuantita());//incremento semplicemente la quantità
                                    flag = false;
                                }
                            }
                            if (flag) {
                                riga.setIdCarrello(carrelloDb.getIdCarrello());
                                rigaCarrelloDb.add(riga); //altrimenti lo aggiungo nel carrello
                            }
                        }
                    }
                }
                final WishListDAO wishListService = new WishListDAO();
                final WishList wishList = wishListService.doRetrieveByEmail(utente.getEmail());

                session.setAttribute("carrello", carrelloDb);
                session.setAttribute("wishList", wishList);


            }
            response.sendRedirect("index.html");
        }


    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
