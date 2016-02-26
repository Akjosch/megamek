package megamek.common;

import java.io.Serializable;

import megamek.common.options.IOption;
import megamek.common.options.IOptions;
import megamek.common.options.OptionsConstants;

public class Atmosphere implements IAtmosphere, Serializable {
	private static final long serialVersionUID = -8995997586729904899L;
	protected boolean useGroundMapAltitude = false;
	
	/**
	 * @param useGroundMapAltitude <i>true</i> if the atmosphere should take ground altitude into account
	 */
	protected void useGroundMapAltitude(boolean useGroundMapAltitude) {
		this.useGroundMapAltitude = useGroundMapAltitude;
	}

	/* (non-Javadoc)
	 * @see megamek.common.IAtmosphere#applyOptions(megamek.common.options.IOptions)
	 */
	@Override
	public void applyOptions(IOptions options) {
		if (null == options) {
			return;
		}
		IOption opt = options.getOption(OptionsConstants.AAR_USE_GROUND_MAP_ALTITUDE);
		if (null != opt && opt.getType() == IOption.BOOLEAN) {
			useGroundMapAltitude(opt.booleanValue());
		}
	}
	
	/* (non-Javadoc)
	 * @see megamek.common.IAtmosphere#resetOptions()
	 */
	@Override
	public void resetOptions() {
		useGroundMapAltitude(false);
	}
	
	/* (non-Javadoc)
	 * @see megamek.common.IAtmosphere#minAltitudeOver(megamek.common.IBoard, megamek.common.Coords)
	 */
	@Override
	public int minAltitudeOver(IBoard board, Coords pos) {
		if (null == board) {
			return 1;
		}
		return minAltitudeOver(board.getHex(pos));
	}
	
	/* (non-Javadoc)
	 * @see megamek.common.IAtmosphere#minAltitudeOver(megamek.common.IHex)
	 */
	@Override
	public int minAltitudeOver(IHex hex) {
		if (!useGroundMapAltitude || null == hex) {
			return 1;
		}
        int ceil = hex.ceiling();
        if (ceil <= 8) {
            return 1;
        }
        if (ceil <= 16) {
            return 2;
        }
        if (ceil <= 25) {
            return 3;
        }
        if (ceil <= 41) {
            return 4;
        }
        if (ceil <= 83) {
            return 5;
        }
        if (ceil <= 125) {
            return 6;
        }
        if (ceil <= 166) {
            return 7;
        }
        if (ceil <= 333) {
            return 8;
        }
        if (ceil <= 833) {
            return 9;
        }
        return 10;
	}
}
