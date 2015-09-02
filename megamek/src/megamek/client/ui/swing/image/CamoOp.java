package megamek.client.ui.swing.image;

import java.awt.Image;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class CamoOp extends ConstantSizeRasterOp {
	private final Image camo;
	
	public CamoOp(Image camo) {
		this.camo = camo;
	}

	@Override
	public WritableRaster filter(Raster src, WritableRaster dest) {
		dest = super.filter(src, dest);
		// TODO Auto-generated method stub
		return dest;
	}
}