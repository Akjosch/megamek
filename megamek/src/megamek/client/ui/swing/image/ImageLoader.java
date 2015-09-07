package megamek.client.ui.swing.image;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Alternative loaders for some image types not supported by AWT */
public abstract class ImageLoader {
	private static final List<ImageLoader> imageLoaders;
	
	static {
		imageLoaders = new ArrayList<ImageLoader>(2);
		imageLoaders.add(new SVGImageLoader());
		imageLoaders.add(new AWTImageLoader());
	}
	
	/** Add a new image loader to the first position of the list, if it isn't there already */
	public static void addImageLoader(ImageLoader loader) {
		if (null != loader && !imageLoaders.contains(loader)) {
			imageLoaders.add(0, loader);
		}
	}
	
	public static Image loadImageFromFile(File file, Toolkit toolkit) {
    	if (!file.exists()) {
            System.out.println("Warning: MechTileSet is trying to " +
            		"load a file that doesn't exist: " + file.getPath());
    	}
        for (ImageLoader loader : imageLoaders) {
        	Image img = loader.loadImage(file, toolkit);
        	if (null != img) {
        		return img;
        	}
        }
        return null;
	}
	
	/** Return <i>null</i> if loader is not applicable for the image type */
	public abstract Image loadImage(File file, Toolkit toolkit);
}