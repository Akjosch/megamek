package megamek.client.ui.swing.image;

import java.awt.Color;
import java.awt.Image;

/** Image we can apply tint and/or camo to */
public interface TintableImage {
	public TintableImage withTint(Color tint);
	public TintableImage withCamo(Image camo);
}
