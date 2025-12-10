package controller.utente;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.libroService.Libro;
import model.libroService.LibroDAO;
import model.libroService.Reparto;
import model.utenteService.Utente;
import model.wishList.WishList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/modifica-preferiti")
public class ModificaPrefServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String isbn = request.getParameter("isbn");
        final String source = request.getParameter("source");
        final String position = request.getParameter("position");

        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");

        String address = "index.html";
        final LibroDAO libroService = new LibroDAO();
        final Libro libro = libroService.doRetrieveById(isbn);

        if (utente == null) {
            address = "/WEB-INF/results/login.jsp";
        } else {
            // Use switch for source routing
            if (source != null) {
                switch (source) {
                    case "wishList":
                        address = "/WEB-INF/results/showWishList.jsp";
                        break;
                    case "reparto":
                        final String repartoParam = request.getParameter("repartoAttuale");
                        if (repartoParam != null) {
                            final int idReparto = Integer.parseInt(repartoParam);
                            final List<Reparto> reparti = (List<Reparto>) getServletContext().getAttribute("reparti");
                            for (final Reparto r : reparti) {
                                if (r.getIdReparto() == idReparto) {
                                    request.setAttribute("reparto", r);
                                    break;
                                }
                            }
                            address = "/WEB-INF/results/reparto.jsp";
                        }
                        break;
                    case "mostraLibro":
                        address = "/WEB-INF/results/mostraLibro.jsp";
                        request.setAttribute("libro", libro);
                        break;
                }
            }

            // Wishlist logic
            WishList wishList = (WishList) session.getAttribute("wishList");
            if (wishList == null) {
                wishList = new WishList();
                wishList.setLibri(new ArrayList<>());
            }

            List<Libro> libri = wishList.getLibri();
            if (libri == null) {
                libri = new ArrayList<>();
                wishList.setLibri(libri);
            }

            final boolean removed = libri.remove(libro);
            if (!removed) {
                libri.add(libro);
            }

            session.setAttribute("wishList", wishList);

            if (position != null) {
                address += "#" + position;
            }
        }

        final RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);
    }
}
