package model.libroService;
import model.ConPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    public void doSave(final Libro libro){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO libro (isbn, titolo, genere, annoPubblicazione, prezzo, sconto, trama, immagine) VALUES(?,?,?,?,?,?,?,?)");
            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitolo());
            ps.setString(3, libro.getGenere());
            ps.setString(4, libro.getAnnoPubblicazioni());
            ps.setDouble(5, libro.getPrezzo());
            ps.setInt(6, libro.getSconto());
            ps.setString(7, libro.getTrama());
            ps.setString(8, libro.getImmagine());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }

            for(final Autore autore : libro.getAutori()){
                this.addAutore(libro.getIsbn(), autore);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteLibro(final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("DELETE FROM rigacarrello WHERE isbn=?");
            ps.setString(1, isbn);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE1 error.");

            ps = con.prepareStatement("DELETE FROM wishlist WHERE isbn=?");
            ps.setString(1, isbn);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE2 error.");

            ps = con.prepareStatement("DELETE FROM reparto WHERE isbn=?");
            ps.setString(1, isbn);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE3 error.");

            ps = con.prepareStatement("DELETE FROM sede WHERE isbn=?");
            ps.setString(1, isbn);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE4 error.");

            ps = con.prepareStatement("DELETE FROM scrittura WHERE isbn=?");
            ps.setString(1, isbn);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE4 error.");

            ps = con.prepareStatement("DELETE FROM libro WHERE isbn=?");
            ps.setString(1, isbn);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE5 error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateLibroSconto(final Libro libro){
        try(final Connection con = ConPool.getConnection()){
            final PreparedStatement ps = con.prepareStatement("UPDATE libro SET sconto = ? WHERE isbn = ?");
            ps.setInt(1, libro.getSconto());
            ps.setString(2, libro.getIsbn());
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("UPDATE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void updateLibro(final Libro libro){
        try(final Connection con = ConPool.getConnection()){
            final PreparedStatement ps = con.prepareStatement("UPDATE libro SET titolo = ?, genere = ?, " +
                    "annoPubblicazione = ?, prezzo = ?, sconto = ?, trama = ?, immagine = ? WHERE isbn = ?");
            ps.setString(1, libro.getTitolo());
            ps.setString(2, libro.getGenere());
            ps.setString(3, libro.getAnnoPubblicazioni());
            ps.setDouble(4, libro.getPrezzo());
            ps.setInt(5, libro.getSconto());
            ps.setString(6, libro.getTrama());
            ps.setString(7, libro.getImmagine());
            ps.setString(8, libro.getIsbn());
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("UPDATE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateDisponibile(final Libro libro){
        try(final Connection con = ConPool.getConnection()){
            final PreparedStatement ps = con.prepareStatement("UPDATE libro SET disponibile = ? WHERE isbn = ?");
            ps.setBoolean(1, libro.isDisponibile());
            ps.setString(2, libro.getIsbn());
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("UPDATE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Libro> doRetriveAll(){
        final List<Libro> libri = new ArrayList<>();
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT isbn, titolo, genere, annoPubblicazione, prezzo, sconto, trama, immagine, disponibile FROM libro");

            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final Libro p = new Libro();
                p.setIsbn(rs.getString(1));
                p.setTitolo(rs.getString(2));
                p.setGenere(rs.getString(3));
                p.setAnnoPubblicazioni(rs.getString(4));
                p.setPrezzo(rs.getDouble(5));
                p.setSconto(rs.getInt(6));
                p.setTrama(rs.getString(7));
                p.setImmagine(rs.getString(8));
                p.setDisponibile(rs.getBoolean(9));
                p.setAutori(this.getScrittura(p.getIsbn()));
                libri.add(p);
            }
            return libri;
        } catch(final SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Libro doRetrieveById(final String isbn) {
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT isbn, titolo, genere, annoPubblicazione, prezzo, sconto, trama, immagine, disponibile FROM libro WHERE isbn=?");
            ps.setString(1, isbn);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final Libro p = new Libro();
                p.setIsbn(rs.getString(1));
                p.setTitolo(rs.getString(2));
                p.setGenere(rs.getString(3));
                p.setAnnoPubblicazioni(rs.getString(4));
                p.setPrezzo(rs.getDouble(5));
                p.setSconto(rs.getInt(6));
                p.setTrama(rs.getString(7));
                p.setImmagine(rs.getString(8));
                p.setDisponibile(rs.getBoolean(9));
                p.setAutori(this.getScrittura(p.getIsbn()));
                return p;
            }
            return null;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Autore> getScrittura(final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT cf FROM scrittura WHERE isbn=?");
            ps.setString(1, isbn);
            final ResultSet rs = ps.executeQuery();
            final List<Autore> autori = new ArrayList<>();
            while (rs.next()) {
                final String cf = rs.getString(1);
                final AutoreDAO service = new AutoreDAO();
                autori.add(service.searchAutore(cf));
            }
            return autori;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Reparto> getAppartenenzaReparto(final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT idReparto FROM appartenenza WHERE isbn=?");
            ps.setString(1, isbn);
            final ResultSet rs = ps.executeQuery();
            final List<Reparto> reparti = new ArrayList<>();
            while (rs.next()) {
                final int idReparto = rs.getInt(1);
                final RepartoDAO service = new RepartoDAO();
                reparti.add(service.doRetrieveById(idReparto));
            }
            return reparti;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Sede> getPresenzaSede(final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT idSede FROM presenza WHERE isbn=?");
            ps.setString(1, isbn);
            final ResultSet rs = ps.executeQuery();
            final List<Sede> sedi = new ArrayList<>();
            while (rs.next()) {
                final int idSede = rs.getInt(1);
                final SedeDAO service = new SedeDAO();
                sedi.add(service.doRetrieveById(idSede));
            }
            return sedi;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAutoreScrittura(final String isbn, final Autore autore){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("DELETE FROM scrittura WHERE isbn=? AND cf=?");
            ps.setString(1, isbn);
            ps.setString(2, autore.getCf());

            final AutoreDAO service = new AutoreDAO();
            service.deleteAutore(autore.getCf());
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void addAutore(final String isbn, final Autore autore){
        try (final Connection con = ConPool.getConnection()) {
            final AutoreDAO autoreService=new AutoreDAO();
            if(autoreService.searchAutore(autore.getCf())==null)
                autoreService.doSave(autore);

            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO scrittura (cf, isbn) VALUES(?,?)");
            ps.setString(1, autore.getCf());
            ps.setString(2, isbn);

            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Libro> Search(final String query) {
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement("SELECT isbn, titolo, genere, annoPubblicazione, prezzo, sconto, trama, immagine, disponibile FROM libro WHERE titolo LIKE ? OR isbn LIKE ?");
            ps.setString(1, "%" + query + "%");
            ps.setString(2, query + "%");

            final ResultSet rs = ps.executeQuery();
            final List<Libro> libri = new ArrayList<>();
            while (rs.next()) {
                final Libro p = new Libro();
                p.setIsbn(rs.getString(1));
                p.setTitolo(rs.getString(2));
                p.setGenere(rs.getString(3));
                p.setAnnoPubblicazioni(rs.getString(4));
                p.setPrezzo(rs.getDouble(5));
                p.setSconto(rs.getInt(6));
                p.setTrama(rs.getString(7));
                p.setImmagine(rs.getString(8));
                p.setDisponibile(rs.getBoolean(9));
                p.setAutori(this.getScrittura(p.getIsbn()));
                libri.add(p);
            }
            return libri;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // NEW: conta il numero totale di libri che soddisfano la query di ricerca
    public int countSearch(final String query) {
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) " +
                            "FROM libro " +
                            "WHERE titolo LIKE ? OR isbn LIKE ?"
            );
            ps.setString(1, "%" + query + "%");
            ps.setString(2, query + "%");

            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // NEW: restituisce solo una pagina di risultati
    public List<Libro> searchPaged(final String query, final int offset, final int limit) {
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "SELECT isbn, titolo, genere, annoPubblicazione, prezzo, sconto, trama, immagine, disponibile " +
                            "FROM libro " +
                            "WHERE titolo LIKE ? OR isbn LIKE ? " +
                            "ORDER BY titolo ASC, isbn ASC " +
                            "LIMIT ? OFFSET ?"
            );
            ps.setString(1, "%" + query + "%");
            ps.setString(2, query + "%");
            ps.setInt(3, limit);
            ps.setInt(4, offset);

            final ResultSet rs = ps.executeQuery();
            final List<Libro> libri = new ArrayList<>();
            while (rs.next()) {
                final Libro p = new Libro();
                p.setIsbn(rs.getString("isbn"));
                p.setTitolo(rs.getString("titolo"));
                p.setGenere(rs.getString("genere"));
                p.setAnnoPubblicazioni(rs.getString("annoPubblicazione"));
                p.setPrezzo(rs.getDouble("prezzo"));
                p.setSconto(rs.getInt("sconto"));
                p.setTrama(rs.getString("trama"));
                p.setImmagine(rs.getString("immagine"));
                p.setDisponibile(rs.getBoolean("disponibile"));
                // se nel tuo Search() chiami getScrittura(...) per gli autori, fallo anche qui
                p.setAutori(this.getScrittura(p.getIsbn()));

                libri.add(p);
            }
            return libri;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // NEW: conta tutti i libri
    public int countAllLibri() {
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT COUNT(*) FROM libro");

            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // NEW: restituisce solo una pagina di libri
    public List<Libro> doRetrieveAllPaged(final int offset, final int limit) {
        final List<Libro> libri = new ArrayList<>();
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement(
                            "SELECT isbn, titolo, genere, annoPubblicazione, prezzo, sconto, trama, immagine, disponibile " +
                                    "FROM libro " +
                                    "ORDER BY titolo ASC, isbn ASC " +   // ordinati alfabeticamente
                                    "LIMIT ? OFFSET ?"
                    );

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final Libro p = new Libro();
                p.setIsbn(rs.getString(1));
                p.setTitolo(rs.getString(2));
                p.setGenere(rs.getString(3));
                p.setAnnoPubblicazioni(rs.getString(4));
                p.setPrezzo(rs.getDouble(5));
                p.setSconto(rs.getInt(6));
                p.setTrama(rs.getString(7));
                p.setImmagine(rs.getString(8));
                p.setDisponibile(rs.getBoolean(9));
                p.setAutori(this.getScrittura(p.getIsbn()));
                libri.add(p);
            }
            return libri;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
