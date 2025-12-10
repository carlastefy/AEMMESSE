package util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageDownloaderReparti {

    // dimensioni thumbnail (come in home)
    private static final int THUMB_WIDTH = 1262;
    private static final int THUMB_HEIGHT = 400;

    /**
     * Scarica l'immagine da imageUrl (se necessario), crea un thumbnail
     * nella cartella baseDir/thumbs e restituisce il path relativo
     * da salvare nel DB / usare in JSP.
     *
     * baseDir: path ASSOLUTO della cartella "images/libri"
     * baseName: nome base del file (tipicamente l'ISBN)
     */
    public static String downloadAndCreateThumbnail(String baseDir,
                                                    String imageUrl,
                                                    String baseName) throws IOException {

        // es: baseDir = .../src/main/webapp/images/libri
        Path baseDirPath = Paths.get(baseDir);
        Path originalDir = baseDirPath.resolve("original");
        Path thumbDir = baseDirPath.resolve("thumbs");

        Files.createDirectories(originalDir);
        Files.createDirectories(thumbDir);

        String originalFileName = baseName + ".jpg";
        String thumbFileName = baseName + "-230x310.jpg";

        Path originalPath = originalDir.resolve(originalFileName);
        Path thumbPath = thumbDir.resolve(thumbFileName);

        // 1) scarica originale se non esiste
        if (!Files.exists(originalPath)) {
            URL url = new URL(imageUrl);
            try (InputStream in = url.openStream()) {
                Files.copy(in, originalPath);
            }
        }

        // 2) crea thumbnail se non esiste
        if (!Files.exists(thumbPath)) {
            BufferedImage original = ImageIO.read(originalPath.toFile());
            if (original == null) {
                throw new IOException("Impossibile leggere immagine da " + originalPath);
            }

            BufferedImage thumb = new BufferedImage(
                    THUMB_WIDTH, THUMB_HEIGHT, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = thumb.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(original, 0, 0, THUMB_WIDTH, THUMB_HEIGHT, null);
            g2d.dispose();

            ImageIO.write(thumb, "jpg", thumbPath.toFile());
        }

        // Path RELATIVO da salvare nel DB (e usare nelle JSP)
        // supponendo che /images sia il context path statico
        return "images/reparti/thumbs/" + thumbFileName;
    }
}
