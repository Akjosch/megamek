package megamek.client.ui.swing.image;

import java.awt.Image;

/** Put on Image subclasses which don't need filters to scale or rotate them */
public interface TransformableImage {
	public Image getScaledInstance(int width, int height, int hints);
    /** Pass (Double)null for an unrotated instance */
	public Image getRotatedInstance(Double rot);
}
