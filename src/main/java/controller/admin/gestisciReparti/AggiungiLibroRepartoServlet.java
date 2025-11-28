package controller.admin.gestisciReparti;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.Libro;
import model.libroService.LibroDAO;
import model.libroService.Reparto;
import model.libroService.RepartoDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/aggiungi-libro")
public class AggiungiLibroRepartoServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        final RepartoDAO repartoDAO = new RepartoDAO();
        final Reparto r = repartoDAO.doRetrieveById(Integer.parseInt(request.getParameter("idReparto")));
        request.setAttribute("reparto", r);

        final LibroDAO libroService = new LibroDAO();
        final List<Libro> libri = libroService.doRetriveAll();
        final List<Libro> libriGiaPresenti = repartoDAO.getAppartenenza(r.getIdReparto());

        if(!libriGiaPresenti.isEmpty()){
            for(final Libro l : libriGiaPresenti){
                libri.remove(l);
            }
        }
        request.setAttribute("libri", libri);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/reparti/stampaLibri.jsp");
        dispatcher.forward(request, response);
    }
}
