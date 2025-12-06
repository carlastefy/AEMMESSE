package model.tesseraService;

import model.ConPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TesseraDAO {
    public void doSave(final Tessera tessera){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO tessera (numero, dataCreazione, dataScadenza, email) VALUES(?,?,?,?)");
            ps.setString(1, tessera.getNumero());
            ps.setDate(2, java.sql.Date.valueOf(tessera.getDataCreazione()));
            ps.setDate(3, java.sql.Date.valueOf(tessera.getDataScadenza()));
            ps.setString(4, tessera.getEmail());

            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }

            tessera.setPunti(this.doRetrieveById(tessera.getNumero()).getPunti());
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTessera(final String numero){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("DELETE FROM tessera WHERE numero=?");
            ps.setString(1, numero);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTessera(final Tessera tessera){
        try(final Connection con = ConPool.getConnection()){
            final PreparedStatement ps = con.prepareStatement("UPDATE tessera SET punti = ? WHERE numero = ?");
            ps.setInt(1, tessera.getPunti());
            ps.setString(2, tessera.getNumero());
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("UPDATE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Tessera> doRetrivedAll(){
        final List<Tessera> tessere = new ArrayList<>();
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT numero, dataCreazione, dataScadenza, punti, email FROM tessera");

            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final Tessera p = new Tessera();
                p.setNumero(rs.getString(1));
                p.setDataCreazione(rs.getDate(2).toLocalDate());
                p.setDataScadenza(rs.getDate(3).toLocalDate());
                p.setPunti(rs.getInt(4));
                p.setEmail(rs.getString(5));
                tessere.add(p);
            }
            return tessere;
        } catch(final SQLException e){
            throw new RuntimeException(e);
        }
    }
    public List<String> doRetrivedAllByNumero(){
        final List<String> numeri = new ArrayList<>();
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT numero, dataCreazione, dataScadenza, punti, email FROM tessera");

            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                numeri.add(rs.getString(1));
            }
            return numeri;
        } catch(final SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Tessera doRetrieveById(final String numero) {
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT numero, dataCreazione, dataScadenza, punti, email FROM tessera WHERE numero=?");
            ps.setString(1, numero);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final Tessera p = new Tessera();
                p.setNumero(rs.getString(1));
                p.setDataCreazione(rs.getDate(2).toLocalDate());
                p.setDataScadenza(rs.getDate(3).toLocalDate());
                p.setPunti(rs.getInt(4));
                p.setEmail(rs.getString(5));
                return p;
            }
            return null;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Tessera doRetrieveByEmail(final String email) {
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT numero, dataCreazione, dataScadenza, punti, email FROM tessera WHERE email=?");
            ps.setString(1, email);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final Tessera p = new Tessera();
                p.setNumero(rs.getString(1));
                p.setDataCreazione(rs.getDate(2).toLocalDate());
                p.setDataScadenza(rs.getDate(3).toLocalDate());
                p.setPunti(rs.getInt(4));
                p.setEmail(rs.getString(5));
                return p;
            }
            return null;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
