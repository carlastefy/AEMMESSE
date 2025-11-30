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

        String address="index.html";
        final LibroDAO libroService = new LibroDAO();
        final Libro libro = libroService.doRetrieveById(isbn);
        if(utente==null) {
            address = "/WEB-INF/results/login.jsp";
        }

        else {
            if(source!= null && source.equals("wishList")){// controllo se il bottone è stato selezionato nella show WishList
                address="/WEB-INF/results/showWishList.jsp";
            }
            else if(source!= null && source.equals("reparto")){// controllo se il bottone è stato selezionato nel reparto
                if(request.getParameter("repartoAttuale")!=null) {
                    final int idReparto = Integer.parseInt(request.getParameter("repartoAttuale"));
                    final List<Reparto> reparti = (List<Reparto>) getServletContext().getAttribute("reparti");
                    for(final Reparto r : reparti) {
                        if(r.getIdReparto()==idReparto) {
                            request.setAttribute("reparto", r);
                        }
                    }
                    address = "/WEB-INF/results/reparto.jsp";
                }
            }else if(source!= null && source.equals("mostraLibro")){
                address = "/WEB-INF/results/mostraLibro.jsp";
                request.setAttribute("libro", libro);
            }
            WishList wishList = (WishList) session.getAttribute("wishList");
            if (wishList == null) {
                wishList = new WishList();
                wishList.setLibri(new ArrayList<>());
            }

            java.util.List<Libro> libri = wishList.getLibri();
            if (libri == null) {
                libri = new ArrayList<>();
                wishList.setLibri(libri);
            }

            // rimuovo la prima occorrenza se presente, altrimenti aggiungo
            boolean removed = libri.remove(libro);
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
