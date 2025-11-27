package controller.HomePage;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.utenteService.Utente;
import model.wishList.WishList;
import model.wishList.WishListDAO;

import java.io.IOException;
@WebServlet("/show-wishlist")
public class ShowWishList extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

        final HttpSession session= request.getSession();
        if(Validator.checkIfUserAdmin((Utente) session.getAttribute("utente"))) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
        }
        String address=null;

        if (session.getAttribute("utente") != null) {
            address="/WEB-INF/results/showWishList.jsp";
        } else {
            // L'utente non ha effettuato l'accesso, reindirizzalo alla pagina di login
            address="/WEB-INF/results/login.jsp";
        }

        final RequestDispatcher dispatcher=request.getRequestDispatcher(address);
        dispatcher.forward(request, response);


    }
}
