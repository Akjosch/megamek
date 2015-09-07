package megamek.client.ui.swing.image;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;

/** Default AWT image loader, should come last in list */
class AWTImageLoader extends ImageLoader {
	@Override
	public Image loadImage(File file, Toolkit toolkit) {
		return toolkit.getImage(file.toString());
	}
	
}