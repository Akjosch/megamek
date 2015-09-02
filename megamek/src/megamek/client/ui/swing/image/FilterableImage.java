package megamek.client.ui.swing.image;

import java.awt.Image;
import java.awt.image.RasterOp;

/** Image we can apply RasterOp filters to */
public interface FilterableImage {
	public Image setRasterOperation(RasterOp filter);
	public Image withFilter(RasterOp filter);
}
