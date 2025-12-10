package util;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ResizeIcons {

    public static void main(String[] args) throws IOException {
        String baseDir = "src/main/webapp/images";

        String[] files = {
                //"hearts-icon.png",
                //"icon-cart.png",
                //"search-icon.png"
                //"icon-user.png"
                "logo.png"
        };

        for (String name : files) {
            resizeTo25(baseDir, name);
        }
    }

    private static void resizeTo25(String baseDir, String fileName) throws IOException {
        File input = new File(baseDir, fileName);
        BufferedImage original = ImageIO.read(input);
        if (original == null) {
            System.out.println("Impossibile leggere " + input.getAbsolutePath());
            return;
        }

        BufferedImage resized = new BufferedImage(25, 25, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, 88, 66, null);
        g.dispose();

        String outName = fileName.replace(".png", "-88.png");
        File output = new File(baseDir, outName);
        ImageIO.write(resized, "png", output);

        System.out.println("Creato " + output.getAbsolutePath());
    }
}
