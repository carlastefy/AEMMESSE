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

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;

@WebServlet("/aggiorna-reparto")
public class AggiornaRepartoServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
            final int id= Integer.parseInt(request.getParameter("idReparto"));
            final String descrizione=request.getParameter("descrizione");
            final String immagine=request.getParameter("immagine");
            String address="/WEB-INF/results/admin/reparti/gestisciReparti.jsp";
            if(descrizione==null || immagine==null){
                    address="/WEB-INF/errorJsp/erroreForm.jsp";
            }
            else {
                    final RepartoDAO repartoService = new RepartoDAO();
                    final Reparto reparto = new Reparto();
                    reparto.setIdReparto(id);
                    reparto.setDescrizione(descrizione);
                    reparto.setImmagine(immagine);

                    repartoService.updateReparto(reparto);
            }
            final RequestDispatcher dispatcher = request.getRequestDispatcher(address);
            dispatcher.forward(request, response);
    }
}
