package megamek.common;

import megamek.common.options.IOptions;

/**
 * Instances of this interface hold the atmospheric conditions over a given battlefield.
 * <p>
 * In the long run, this should replace most of PlanetaryConditions, aside of things like gravity.
 * In particular:
 * <ul>
 * <li>Wind strength - per-altitude, so that different layers can have different winds
 * <li>Wind direction - per-altitude
 * <li>Shifting wind strength and direction setting - per-altitude
 * <li>Fog - per-altitude, to simulate cloud cover and low-lying fog
 * <li>Temperature - per-altitude, for the temperature gradient
 * <li>Weather conditions - per-altitude, so that it doesn't rain above the cloud layer
 * <li>Atmospheric pressure - per-altitude, limiting the flight ceiling in some cases
 * <li>Sand blowing - per-altitude, to limit the maximum height of the effect
 * </ul>
 * Gravity, light conditions, and electro-magnetic interference should remain with planetary conditions.
 * <p>
 * In addition, unofficial rules like toxic air layers could land in instances of this class as well.
 * <p>
 * This class gets added to board instances, to make sure multi-board games can have different atmospheres.
 */
public interface IAtmosphere {
    /**
     * Apply the options to the atmosphere. Only apply options actually in the argument;
     * missing options have their values unchanged.
     */
    public abstract void applyOptions(IOptions options);

    /**
     * Reset the options to their defaults
     */
    public abstract void resetOptions();

    /**
     * Lowest flight-capable altitude. Typically 1.
     */
    public abstract int minAltitude();
    
    /**
     * Highest flight-capable altitude; units raising above this altitude end up outside the
     * corresponding board and in the high-altitude board if available.
     */
    public abstract int maxAltitude();
    
    /**
     * @return the highest altitude (Aerospace elevation) that features in this
     *         position over the board extend to.
     */
    public abstract int minAltitudeOver(IBoard board, Coords pos);

    /**
     * @return the highest altitude (Aerospace elevation) that features in this
     *         hex extend to.
     */
    public abstract int minAltitudeOver(IHex hex);

}
