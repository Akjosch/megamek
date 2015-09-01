package megamek.client.ui.swing.image;

import java.awt.Color;
import java.awt.Image;

/** Image we can apply tint and/or camo to */
public interface TintableImage {
	public Image withTint(Color tint);
	public Image withCamo(Image camo);
}
