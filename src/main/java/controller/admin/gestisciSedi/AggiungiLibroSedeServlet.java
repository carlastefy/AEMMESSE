package controller.admin.gestisciSedi;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/aggiungi-libro-sede")
public class AggiungiLibroSedeServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        final SedeDAO sedeDAO = new SedeDAO();
        final Sede s = sedeDAO.doRetrieveById(Integer.parseInt(request.getParameter("idSede")));
        request.setAttribute("sede", s);

        final LibroDAO libroService = new LibroDAO();
        final List<Libro> libri = libroService.doRetriveAll();
        final List<Libro> libriGiaPresenti = sedeDAO.getPresenza(s.getIdSede());

        if(!libriGiaPresenti.isEmpty()){
            for(final Libro l : libriGiaPresenti){
                libri.remove(l);
            }
        }
        request.setAttribute("libri", libri);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/sedi/stampaLibri.jsp");
        dispatcher.forward(request, response);
    }
}
