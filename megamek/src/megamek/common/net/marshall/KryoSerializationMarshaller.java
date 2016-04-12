package megamek.common.net.marshall;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import megamek.common.Aero;
import megamek.common.ArmlessMech;
import megamek.common.ArtilleryTracker;
import megamek.common.BattleArmor;
import megamek.common.BipedMech;
import megamek.common.Board;
import megamek.common.BoardDimensions;
import megamek.common.Building;
import megamek.common.CalledShot;
import megamek.common.ConvFighter;
import megamek.common.Coords;
import megamek.common.Dropship;
import megamek.common.EjectedCrew;
import megamek.common.FighterSquadron;
import megamek.common.FixedWingSupport;
import megamek.common.GameTurn;
import megamek.common.GunEmplacement;
import megamek.common.Hex;
import megamek.common.IGame;
import megamek.common.IHex;
import megamek.common.ITerrain;
import megamek.common.Infantry;
import megamek.common.InitiativeRoll;
import megamek.common.Jumpship;
import megamek.common.LandAirMech;
import megamek.common.LargeSupportTank;
import megamek.common.MapSettings;
import megamek.common.MechWarrior;
import megamek.common.Mounted;
import megamek.common.PlanetaryConditions;
import megamek.common.Player;
import megamek.common.Protomech;
import megamek.common.QuadMech;
import megamek.common.SmallCraft;
import megamek.common.SpaceStation;
import megamek.common.SuperHeavyTank;
import megamek.common.SupportTank;
import megamek.common.SupportVTOL;
import megamek.common.Tank;
import megamek.common.TeleMissile;
import megamek.common.Terrain;
import megamek.common.TripodMech;
import megamek.common.UnitLocation;
import megamek.common.VTOL;
import megamek.common.Warship;
import megamek.common.actions.*;
import megamek.common.net.Packet;
import megamek.common.net.marshall.kryo.BoardDimensionsSerializer;
import megamek.common.net.marshall.kryo.EntityClassTurnFieldSerializer;
import megamek.common.net.marshall.kryo.PacketSerializer;
import megamek.common.net.marshall.kryo.PlayerFieldSerializer;
import megamek.common.net.marshall.kryo.TerrainSerializer;
import megamek.common.net.marshall.kryo.UnitLocationFieldSerializer;
import megamek.common.net.marshall.kryo.UnitNumberTurnFieldSerializer;
import megamek.common.options.GameOptions;
import megamek.common.options.Option;
import megamek.common.options.WeaponQuirks;

/**
 * Marshaller/unmarshaller based on the {@link Kryo} serialization library
 */
public class KryoSerializationMarshaller extends PacketMarshaller {
    /** Concrete classes and arrays which don't need special serializers */
    private final static List<Class<?>> SIMPLE_CLASSES = Arrays.asList(
        Object.class, /* Important to make sure all objects work */ 
        Vector.class, ArrayList.class, HashMap.class, TreeSet.class, HashSet.class, Hashtable.class,
        boolean[].class,
        GameOptions.class,
        PlanetaryConditions.class, MapSettings.class, IGame.Phase.class, InitiativeRoll.class,
        Option.class,
        // Board data
        Coords.class, Board.class, IHex[].class, Hex.class, Building.class, Building.BasementType.class,
        ITerrain[].class,
        ArtilleryTracker.class,
        // Units and their equipment/parts
        BipedMech.class, ArmlessMech.class, LandAirMech.class, QuadMech.class, TripodMech.class,
        Protomech.class, Tank.class, GunEmplacement.class, SuperHeavyTank.class, SupportTank.class,
        LargeSupportTank.class, VTOL.class, SupportVTOL.class, Aero.class, ConvFighter.class,
        FixedWingSupport.class, FighterSquadron.class, Jumpship.class, SpaceStation.class,
        Warship.class, SmallCraft.class, Dropship.class, TeleMissile.class, Infantry.class,
        BattleArmor.class, EjectedCrew.class, MechWarrior.class,
        Mounted.class, CalledShot.class, WeaponQuirks.class,
        GameTurn.class,
        // Actions
        WeaponAttackAction.class, ArtilleryAttackAction.class, BAVibroClawAttackAction.class,
        BrushOffAttackAction.class, DisplacementAttackAction.class, ChargeAttackAction.class,
        DfaAttackAction.class, PushAttackAction.class, LayExplosivesAttackAction.class,
        PhysicalAttackAction.class, BreakGrappleAttackAction.class, ClubAttackAction.class,
        GrappleAttackAction.class, JumpJetAttackAction.class, KickAttackAction.class,
        PunchAttackAction.class, TripAttackAction.class, ProtomechPhysicalAttackAction.class,
        RamAttackAction.class, SearchlightAttackAction.class, TeleMissileAttackAction.class,
        ThrashAttackAction.class, ClearMinefieldAction.class, DodgeAction.class,
        FindClubAction.class, FiringModeChangeAction.class, FlipArmsAction.class,
        RepairWeaponMalfunctionAction.class, SpotAction.class, TorsoTwistAction.class,
        TriggerAPPodAction.class, TriggerBPodAction.class, UnjamAction.class,
        UnjamTurretAction.class, UnloadStrandedAction.class
        );
    private final static ThreadLocal<Kryo> KRYOS = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(true);
            /* IDs need to stay consistent, -2 and -1 are reserved, 0-8 used for primitive types */
            kryo.register(Packet.class, new PacketSerializer(), 9);
            for(Class<?> cls : SIMPLE_CLASSES) {
                kryo.register(cls);
            }
            kryo.register(Player.class, new PlayerFieldSerializer(kryo));
            kryo.register(BoardDimensions.class, new BoardDimensionsSerializer());
            kryo.register(Terrain.class, new TerrainSerializer());
            kryo.register(GameTurn.EntityClassTurn.class, new EntityClassTurnFieldSerializer(kryo));
            kryo.register(GameTurn.UnitNumberTurn.class, new UnitNumberTurnFieldSerializer(kryo));
            kryo.register(UnitLocation.class, new UnitLocationFieldSerializer(kryo));
            return kryo;
        };
    };

    
    /*
     * (non-Javadoc)
     * 
     * @see PacketMarshaller#marshall(megamek.common.net.Packet,
     *      java.io.OutputStream)
     */
    @Override public void marshall(Packet packet, OutputStream stream) throws Exception {
        try(Output output = new Output(stream)) {
            KRYOS.get().writeObject(output, packet);
            output.flush();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see PacketMarshaller#unmarshall(java.io.InputStream)
     */
    @Override public Packet unmarshall(InputStream stream) throws Exception {
        try(Input input = new Input(stream)) {
            return KRYOS.get().readObject(input, Packet.class);
        }
    }

}
