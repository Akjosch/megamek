package megamek.client.ui.swing.image;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;

class SVGImageLoader extends ImageLoader {
    @Override
    public Image loadImage(File file, Toolkit toolkit) {
        Image svgImage = null;
        if (file.getName().toLowerCase().endsWith(".svg") || file.getName().toLowerCase().endsWith(".svgz")) {
            svgImage = SVGImage.fromFile(file);
        }
        return svgImage;
    }
}