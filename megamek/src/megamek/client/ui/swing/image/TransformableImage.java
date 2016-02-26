package megamek.client.ui.swing.image;

import java.awt.Image;

/** Put on Image subclasses which don't need filters to scale or rotate them */
public interface TransformableImage {
    /** From java.awt.Image */
    public Image getScaledInstance(int width, int height, int hints);
    public Image getRotatedInstance(double rot);
}
