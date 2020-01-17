/**
 * MegaMek - Copyright (C) 2005 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */
package megamek.common.weapons.lrms;

/**
 * @author Sebastian Brocks
 */
public class CLLRM3OS extends LRMWeapon {

    /**
     *
     */
    private static final long serialVersionUID = -4086505975056019860L;

    /**
     *
     */
    public CLLRM3OS() {
        super();

        name = "LRM 3 (OS)";
        setInternalName("CLLRM3OS");
        heat = 0;
        rackSize = 3;
        minimumRange = WEAPON_NA;
        tonnage = 0.6;
        criticals = 0;
        bv = 7;
        flags = flags.or(F_ONESHOT).andNot(F_PROTO_WEAPON);
        // Per Herb all ProtoMech launcher use the ProtoMech Chassis progression. 
        //But LRM Tech Base and Avail Ratings.
        rulesRefs = "231,TM";
        techAdvancement.setTechBase(TECH_BASE_CLAN)
    	.setIntroLevel(false)
    	.setUnofficial(false)
        .setTechRating(RATING_F)
        .setAvailability(RATING_X, RATING_X, RATING_C, RATING_C)
        .setClanAdvancement(3055, 3060, 3061, DATE_NONE, DATE_NONE)
        .setClanApproximate(true, false, false,false, false)
        .setPrototypeFactions(F_CSJ)
        .setProductionFactions(F_CSJ);
    }
}
