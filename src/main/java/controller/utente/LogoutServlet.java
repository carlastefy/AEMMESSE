package controller.utente;

import controller.utils.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.carrelloService.Carrello;
import model.carrelloService.CarrelloDAO;
import model.carrelloService.RigaCarrello;
import model.carrelloService.RigaCarrelloDAO;
import model.libroService.Libro;
import model.libroService.Reparto;
import model.libroService.RepartoDAO;
import model.utenteService.Utente;
import model.wishList.WishList;
import model.wishList.WishListDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/log-out")
public class LogoutServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final CarrelloDAO carrelloDAO = new CarrelloDAO();
        final WishListDAO wishListDAO = new WishListDAO();
        final Carrello carrello = (Carrello) session.getAttribute("carrello");
        final WishList wishList = (WishList) session.getAttribute("wishList");
        final Utente utente = (Utente) session.getAttribute("utente");
        wishList.setEmail(utente.getEmail());

        try{
            final RigaCarrelloDAO rigaCarrelloService = new RigaCarrelloDAO();
            if(carrelloDAO.doRetriveByUtente(utente.getEmail()) != null && !(carrelloDAO.doRetriveByUtente(utente.getEmail()).getRigheCarrello().isEmpty())) {
                //Carrello carrello2=carrelloDAO.doRetriveByUtente(utente.getEmail());
                rigaCarrelloService.deleteRigheCarrelloByIdCarrello(carrelloDAO.doRetriveByUtente(utente.getEmail()).getIdCarrello());//elimino ciò che è presente nel db
            }
            final WishListDAO wishListService = new WishListDAO();
            if(wishListDAO.doRetrieveByEmail(utente.getEmail())!= null && !(wishListDAO.doRetrieveByEmail(utente.getEmail()).getLibri().isEmpty())) {
                wishListService.deleteWishListByEmail(utente.getEmail());//elimino ciò che è presente nel db
            }

            if(carrello.getRigheCarrello()!= null) {
                for (RigaCarrello riga : carrello.getRigheCarrello())
                    rigaCarrelloService.doSave(riga); //ripopolo il db con le informazioni presenti in sessione
            }
            if(wishList.getLibri()!= null) {
                for (Libro libro : wishList.getLibri()) {
                    wishListService.doSave(wishList, libro.getIsbn()); //ripopolo il db con le informazioni presenti in sessione
                }
            }


        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        //Se l'admin modifica i reparti è necessario apportare modifiche alla lista salvata del serveltContext
        if(Validator.checkIfUserAdmin(utente)){
            getServletContext().removeAttribute("reparti");
            final RepartoDAO service = new RepartoDAO();
            final List<Reparto> reparti = service.doRetrivedAll();
            getServletContext().setAttribute("reparti", reparti);
        }
        session.invalidate();
        response.sendRedirect("index.html");

    }
}
