package util;

import model.ConPool;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThumbnailRepartiMigration {

    public static void main(String[] args) throws Exception {

        // 1) cartella dove salvare le immagini dei reparti nel progetto
        String baseDir = Paths.get("src/main/webapp/images/reparti")
                .toAbsolutePath()
                .toString();
        System.out.println("Base dir reparti: " + baseDir);

        // 2) connessione al DB usando ConPool
        try (Connection conn = ConPool.getConnection()) {

            String selectSql = "SELECT idReparto, immagine FROM reparto";
            String updateSql = "UPDATE reparto SET immagine = ? WHERE idReparto = ?";

            try (PreparedStatement select = conn.prepareStatement(selectSql);
                 PreparedStatement update = conn.prepareStatement(updateSql)) {

                ResultSet rs = select.executeQuery();
                while (rs.next()) {
                    String idReparto = rs.getString("idReparto");          // oppure rs.getInt(...)
                    String img = rs.getString("immagine");

                    // salta se nulla o già locale
                    if (img == null || !img.startsWith("http")) {
                        continue;
                    }

                    try {
                        // uso l'id del reparto come prefisso nome file
                        String newPath = ImageDownloaderReparti.downloadAndCreateThumbnail(
                                baseDir,
                                img,
                                "reparto_" + idReparto
                        );

                        update.setString(1, newPath);   // nuovo path relativo nel DB
                        update.setString(2, idReparto);
                        update.executeUpdate();

                        System.out.println("OK reparto " + idReparto + " -> " + newPath);
                    } catch (Exception e) {
                        System.err.println("ERRORE reparto " + idReparto + " : " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
