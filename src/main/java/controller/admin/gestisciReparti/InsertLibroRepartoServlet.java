package controller.admin.gestisciReparti;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.RepartoDAO;

import java.io.IOException;

@WebServlet("/insert-libroReparto")
public class InsertLibroRepartoServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String[] libriIsbn = (request.getParameterValues("isbn"));
        final RepartoDAO repartoDAO = new RepartoDAO();
        if(libriIsbn!=null){
            for(final String isbn : libriIsbn){
                repartoDAO.aggiungiLibroReparto(repartoDAO.doRetrieveById(Integer.parseInt(request.getParameter("idReparto")
                        )), isbn);
            }
        }
        response.sendRedirect("gestisci-reparti");
    }
}
