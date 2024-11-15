package dev.enjarai.trickster.spell.trick;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Lifecycle;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.trick.basic.CasterReflectionTrick;
import dev.enjarai.trickster.spell.trick.basic.CostTrick;
import dev.enjarai.trickster.spell.trick.basic.FacingReflectionTrick;
import dev.enjarai.trickster.spell.trick.basic.HotbarReflectionTrick;
import dev.enjarai.trickster.spell.trick.basic.ManaReflectionTrick;
import dev.enjarai.trickster.spell.trick.basic.MaxManaReflectionTrick;
import dev.enjarai.trickster.spell.trick.basic.OnePonyTrick;
import dev.enjarai.trickster.spell.trick.basic.ReadCrowMindTrick;
import dev.enjarai.trickster.spell.trick.basic.ReadSpellTrick;
import dev.enjarai.trickster.spell.trick.basic.ReflectionTrick;
import dev.enjarai.trickster.spell.trick.basic.RevealTrick;
import dev.enjarai.trickster.spell.trick.basic.WriteClosedSpellTrick;
import dev.enjarai.trickster.spell.trick.basic.WriteCrowMindTrick;
import dev.enjarai.trickster.spell.trick.basic.WriteSpellTrick;
import dev.enjarai.trickster.spell.trick.block.BreakBlockTrick;
import dev.enjarai.trickster.spell.trick.block.CanPlaceTrick;
import dev.enjarai.trickster.spell.trick.block.CheckBlockTrick;
import dev.enjarai.trickster.spell.trick.block.CheckResonatorTrick;
import dev.enjarai.trickster.spell.trick.block.ConjureFlowerTrick;
import dev.enjarai.trickster.spell.trick.block.ConjureLightTrick;
import dev.enjarai.trickster.spell.trick.block.ConjureWaterTrick;
import dev.enjarai.trickster.spell.trick.block.DrainFluidTrick;
import dev.enjarai.trickster.spell.trick.block.GetBlockHardnessTrick;
import dev.enjarai.trickster.spell.trick.block.GetRedstonePowerTrick;
import dev.enjarai.trickster.spell.trick.block.PlaceBlockTrick;
import dev.enjarai.trickster.spell.trick.block.PowerResonatorTrick;
import dev.enjarai.trickster.spell.trick.block.SwapBlockTrick;
import dev.enjarai.trickster.spell.trick.bool.AllTrick;
import dev.enjarai.trickster.spell.trick.bool.AnyTrick;
import dev.enjarai.trickster.spell.trick.bool.EqualsTrick;
import dev.enjarai.trickster.spell.trick.bool.GreaterThanTrick;
import dev.enjarai.trickster.spell.trick.bool.IfElseTrick;
import dev.enjarai.trickster.spell.trick.bool.LesserThanTrick;
import dev.enjarai.trickster.spell.trick.bool.NoneTrick;
import dev.enjarai.trickster.spell.trick.bool.NotEqualsTrick;
import dev.enjarai.trickster.spell.trick.dimension.GetDimensionTrick;
import dev.enjarai.trickster.spell.trick.entity.AddVelocityTrick;
import dev.enjarai.trickster.spell.trick.entity.BlockFindEntityTrick;
import dev.enjarai.trickster.spell.trick.entity.DispelPolymorphTrick;
import dev.enjarai.trickster.spell.trick.entity.GetScaleTrick;
import dev.enjarai.trickster.spell.trick.entity.PolymorphTrick;
import dev.enjarai.trickster.spell.trick.entity.RangeFindEntityTrick;
import dev.enjarai.trickster.spell.trick.entity.ReleaseEntityTrick;
import dev.enjarai.trickster.spell.trick.entity.SetScaleTrick;
import dev.enjarai.trickster.spell.trick.entity.StoreEntityTrick;
import dev.enjarai.trickster.spell.trick.entity.query.BlockingReflectionTrick;
import dev.enjarai.trickster.spell.trick.entity.query.GetEntityArmourTrick;
import dev.enjarai.trickster.spell.trick.entity.query.GetEntityHealthTrick;
import dev.enjarai.trickster.spell.trick.entity.query.GetEntityMaxHealthTrick;
import dev.enjarai.trickster.spell.trick.entity.query.GetEntityTypeTrick;
import dev.enjarai.trickster.spell.trick.entity.query.GetEyePositionTrick;
import dev.enjarai.trickster.spell.trick.entity.query.GetFacingTrick;
import dev.enjarai.trickster.spell.trick.entity.query.GetPositionTrick;
import dev.enjarai.trickster.spell.trick.entity.query.GetVelocityTrick;
import dev.enjarai.trickster.spell.trick.entity.query.HeightReflectionTrick;
import dev.enjarai.trickster.spell.trick.entity.query.SneakingReflectionTrick;
import dev.enjarai.trickster.spell.trick.entity.query.SprintingReflectionTrick;
import dev.enjarai.trickster.spell.trick.fleck.GetFlecksTrick;
import dev.enjarai.trickster.spell.trick.fleck.LineFleckTrick;
import dev.enjarai.trickster.spell.trick.fleck.SpellFleckTrick;
import dev.enjarai.trickster.spell.trick.func.AtomicTrick;
import dev.enjarai.trickster.spell.trick.func.ClosureTrick;
import dev.enjarai.trickster.spell.trick.func.ExecuteTrick;
import dev.enjarai.trickster.spell.trick.func.ExecuteWithinCurrentScopeTrick;
import dev.enjarai.trickster.spell.trick.func.FoldTrick;
import dev.enjarai.trickster.spell.trick.func.ForkTrick;
import dev.enjarai.trickster.spell.trick.func.GetCurrentThreadTrick;
import dev.enjarai.trickster.spell.trick.func.KillThreadTrick;
import dev.enjarai.trickster.spell.trick.func.LoadArgumentTrick;
import dev.enjarai.trickster.spell.trick.func.SupplierTrick;
import dev.enjarai.trickster.spell.trick.func.TryCatchTrick;
import dev.enjarai.trickster.spell.trick.inventory.CheckHatTrick;
import dev.enjarai.trickster.spell.trick.inventory.DropStackFromSlotTrick;
import dev.enjarai.trickster.spell.trick.inventory.GetInventorySlotTrick;
import dev.enjarai.trickster.spell.trick.inventory.GetItemInSlotTrick;
import dev.enjarai.trickster.spell.trick.inventory.GetManaInSlotTrick;
import dev.enjarai.trickster.spell.trick.inventory.GetMaxManaInSlotTrick;
import dev.enjarai.trickster.spell.trick.inventory.ImportHatTrick;
import dev.enjarai.trickster.spell.trick.inventory.ImportTrick;
import dev.enjarai.trickster.spell.trick.inventory.OtherHandSlotTrick;
import dev.enjarai.trickster.spell.trick.inventory.OtherHandTrick;
import dev.enjarai.trickster.spell.trick.inventory.ReadMacroRing;
import dev.enjarai.trickster.spell.trick.inventory.SetHatTrick;
import dev.enjarai.trickster.spell.trick.inventory.SwapSlotTrick;
import dev.enjarai.trickster.spell.trick.list.ListAddRangeTrick;
import dev.enjarai.trickster.spell.trick.list.ListAddTrick;
import dev.enjarai.trickster.spell.trick.list.ListCreateTrick;
import dev.enjarai.trickster.spell.trick.list.ListGetTrick;
import dev.enjarai.trickster.spell.trick.list.ListIndexOfTrick;
import dev.enjarai.trickster.spell.trick.list.ListInsertTrick;
import dev.enjarai.trickster.spell.trick.list.ListRemoveElementTrick;
import dev.enjarai.trickster.spell.trick.list.ListRemoveTrick;
import dev.enjarai.trickster.spell.trick.list.ListSizeTrick;
import dev.enjarai.trickster.spell.trick.mana.BatteryCreationTrick;
import dev.enjarai.trickster.spell.trick.mana.PullManaTrick;
import dev.enjarai.trickster.spell.trick.mana.PushManaTrick;
import dev.enjarai.trickster.spell.trick.map.MapGetTrick;
import dev.enjarai.trickster.spell.trick.map.MapInsertTrick;
import dev.enjarai.trickster.spell.trick.map.MapRemoveTrick;
import dev.enjarai.trickster.spell.trick.math.AddTrick;
import dev.enjarai.trickster.spell.trick.math.CeilTrick;
import dev.enjarai.trickster.spell.trick.math.CosTrick;
import dev.enjarai.trickster.spell.trick.math.DivideTrick;
import dev.enjarai.trickster.spell.trick.math.FloorTrick;
import dev.enjarai.trickster.spell.trick.math.MaxTrick;
import dev.enjarai.trickster.spell.trick.math.MinTrick;
import dev.enjarai.trickster.spell.trick.math.ModuloTrick;
import dev.enjarai.trickster.spell.trick.math.MultiplyTrick;
import dev.enjarai.trickster.spell.trick.math.PowerTrick;
import dev.enjarai.trickster.spell.trick.math.RoundTrick;
import dev.enjarai.trickster.spell.trick.math.SinTrick;
import dev.enjarai.trickster.spell.trick.math.SqrtTrick;
import dev.enjarai.trickster.spell.trick.math.SubtractTrick;
import dev.enjarai.trickster.spell.trick.math.TanTrick;
import dev.enjarai.trickster.spell.trick.misc.ClearBarTrick;
import dev.enjarai.trickster.spell.trick.misc.DelayExecutionTrick;
import dev.enjarai.trickster.spell.trick.misc.HashValuesTrick;
import dev.enjarai.trickster.spell.trick.misc.PinChunkTrick;
import dev.enjarai.trickster.spell.trick.misc.ShowBarTrick;
import dev.enjarai.trickster.spell.trick.misc.TypeFragmentTrick;
import dev.enjarai.trickster.spell.trick.particle.HighlightTrick;
import dev.enjarai.trickster.spell.trick.projectile.SummonArrowTrick;
import dev.enjarai.trickster.spell.trick.projectile.SummonDragonBreathTrick;
import dev.enjarai.trickster.spell.trick.projectile.SummonFireballTrick;
import dev.enjarai.trickster.spell.trick.projectile.SummonTntTrick;
import dev.enjarai.trickster.spell.trick.raycast.RaycastBlockPosTrick;
import dev.enjarai.trickster.spell.trick.raycast.RaycastBlockSideTrick;
import dev.enjarai.trickster.spell.trick.raycast.RaycastEntityTrick;
import dev.enjarai.trickster.spell.trick.tree.AddSubtreeTrick;
import dev.enjarai.trickster.spell.trick.tree.EscapePatternTrick;
import dev.enjarai.trickster.spell.trick.tree.GetSubPartsTrick;
import dev.enjarai.trickster.spell.trick.tree.LocateGlyphTrick;
import dev.enjarai.trickster.spell.trick.tree.LocateGlyphsTrick;
import dev.enjarai.trickster.spell.trick.tree.RemoveSubtreeTrick;
import dev.enjarai.trickster.spell.trick.tree.RetrieveGlyphTrick;
import dev.enjarai.trickster.spell.trick.tree.RetrieveSubtreeTrick;
import dev.enjarai.trickster.spell.trick.tree.SetGlyphTrick;
import dev.enjarai.trickster.spell.trick.tree.SetSubtreeTrick;
import dev.enjarai.trickster.spell.trick.vector.AlignVectorTrick;
import dev.enjarai.trickster.spell.trick.vector.CrossProductTrick;
import dev.enjarai.trickster.spell.trick.vector.DotProductTrick;
import dev.enjarai.trickster.spell.trick.vector.ExtractXTrick;
import dev.enjarai.trickster.spell.trick.vector.ExtractYTrick;
import dev.enjarai.trickster.spell.trick.vector.ExtractZTrick;
import dev.enjarai.trickster.spell.trick.vector.InvertTrick;
import dev.enjarai.trickster.spell.trick.vector.LengthTrick;
import dev.enjarai.trickster.spell.trick.vector.MergeVectorTrick;
import dev.enjarai.trickster.spell.trick.vector.NormalizeTrick;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;

public class Tricks {
    private static final Map<Pattern, Trick> LOOKUP = new HashMap<>();

    public static final RegistryKey<Registry<Trick>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("trick"));
    public static final Registry<Trick> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable()) {
        @Override
        public RegistryEntry.Reference<Trick> add(RegistryKey<Trick> key, Trick value, RegistryEntryInfo info) {
            if (LOOKUP.containsKey(value.getPattern())) {
                Trickster.LOGGER.warn(
                        "WARNING: A mod is overriding a pattern that is already defined! This may result in one of the tricks being unusable. ({} overrode {})",
                        key.getValue(), getId(LOOKUP.get(value.getPattern()))
                );
            }

            LOOKUP.put(value.getPattern(), value);
            return super.add(key, value, info);
        }
    }).buildAndRegister();

    // Functions
    public static final ExecuteTrick EXECUTE = register("execute", new ExecuteTrick());
    public static final ExecuteWithinCurrentScopeTrick EXECUTE_SAME_SCOPE = register("execute_same_scope", new ExecuteWithinCurrentScopeTrick());
    public static final ForkTrick FORK = register("fork", new ForkTrick());
    public static final FoldTrick FOLD = register("fold", new FoldTrick());
    public static final TryCatchTrick TRY_CATCH = register("try_catch", new TryCatchTrick());
    public static final AtomicTrick ATOMIC = register("atomic", new AtomicTrick());
    public static final ClosureTrick CLOSURE = register("closure", new ClosureTrick());
    public static final SupplierTrick SUPPLIER = register("supplier", new SupplierTrick());
    public static final KillThreadTrick KILL_THREAD = register("kill_thread", new KillThreadTrick());
    public static final GetCurrentThreadTrick CURRENT_THREAD = register("current_thread", new GetCurrentThreadTrick());
    public static final LoadArgumentTrick LOAD_ARGUMENT_1 = register("load_argument_1", new LoadArgumentTrick(Pattern.of(4, 1), 0));
    public static final LoadArgumentTrick LOAD_ARGUMENT_2 = register("load_argument_2", new LoadArgumentTrick(Pattern.of(4, 2), 1));
    public static final LoadArgumentTrick LOAD_ARGUMENT_3 = register("load_argument_3", new LoadArgumentTrick(Pattern.of(4, 5), 2));
    public static final LoadArgumentTrick LOAD_ARGUMENT_4 = register("load_argument_4", new LoadArgumentTrick(Pattern.of(4, 8), 3));
    public static final LoadArgumentTrick LOAD_ARGUMENT_5 = register("load_argument_5", new LoadArgumentTrick(Pattern.of(4, 7), 4));
    public static final LoadArgumentTrick LOAD_ARGUMENT_6 = register("load_argument_6", new LoadArgumentTrick(Pattern.of(4, 6), 5));
    public static final LoadArgumentTrick LOAD_ARGUMENT_7 = register("load_argument_7", new LoadArgumentTrick(Pattern.of(4, 3), 6));
    public static final LoadArgumentTrick LOAD_ARGUMENT_8 = register("load_argument_8", new LoadArgumentTrick(Pattern.of(4, 0), 7));

    // Basic
    public static final OnePonyTrick TWO = register("two", new OnePonyTrick());
    public static final RevealTrick REVEAL = register("reveal", new RevealTrick());
    public static final HighlightTrick HIGHLIGHT = register("highlight", new HighlightTrick());
    public static final ReadSpellTrick READ_SPELL = register("read_spell", new ReadSpellTrick());
    public static final WriteSpellTrick WRITE_SPELL = register("write_spell", new WriteSpellTrick());
    public static final WriteClosedSpellTrick WRITE_CLOSED_SPELL = register("write_closed_spell", new WriteClosedSpellTrick());
    public static final ReadCrowMindTrick READ_CROW_MIND = register("read_crow_mind", new ReadCrowMindTrick());
    public static final WriteCrowMindTrick WRITE_CROW_MIND = register("write_crow_mind", new WriteCrowMindTrick());

    // Caster
    public static final ReflectionTrick REFLECTION = register("reflection", new ReflectionTrick());
    public static final CasterReflectionTrick CASTER_REFLECTION = register("caster_reflection", new CasterReflectionTrick());
    public static final CostTrick COST = register("cost", new CostTrick());
    public static final ManaReflectionTrick MANA_REFLECTION = register("mana_reflection", new ManaReflectionTrick());
    public static final MaxManaReflectionTrick MAX_MANA_REFLECTION = register("max_mana_reflection", new MaxManaReflectionTrick());
    public static final FacingReflectionTrick FACING_REFLECTION = register("facing_reflection", new FacingReflectionTrick());
    public static final HotbarReflectionTrick HOTBAR_REFLECTION = register("hotbar_reflection", new HotbarReflectionTrick());
    public static final ReadMacroRing READ_MACRO_RING = register("read_macro_ring", new ReadMacroRing());

    // Entity
    public static final GetPositionTrick GET_POSITION = register("get_position", new GetPositionTrick());
    public static final GetEyePositionTrick GET_EYE_POSITION = register("get_eye_position", new GetEyePositionTrick());
    public static final GetEntityTypeTrick GET_ENTITY_TYPE = register("get_entity_type", new GetEntityTypeTrick());
    public static final GetFacingTrick GET_FACING = register("get_facing", new GetFacingTrick());
    public static final GetVelocityTrick GET_VELOCITY = register("get_velocity", new GetVelocityTrick());
    public static final GetEntityHealthTrick GET_HEALTH = register("get_health", new GetEntityHealthTrick());
    public static final GetEntityMaxHealthTrick GET_MAX_HEALTH = register("get_max_health", new GetEntityMaxHealthTrick());
    public static final GetEntityArmourTrick GET_ARMOUR_VALUE = register("get_armour", new GetEntityArmourTrick());
    public static final HeightReflectionTrick HEIGHT_REFLECTION = register("height_reflection", new HeightReflectionTrick());
    public static final SneakingReflectionTrick SNEAKING_REFLECTION = register("sneaking_reflection", new SneakingReflectionTrick());
    public static final SprintingReflectionTrick SPRINTING_REFLECTION = register("sprinting_reflection", new SprintingReflectionTrick());
    public static final BlockingReflectionTrick BLOCKING_REFLECTION = register("blocking_reflection", new BlockingReflectionTrick());
    public static final RaycastBlockPosTrick RAYCAST = register("raycast", new RaycastBlockPosTrick());
    public static final RaycastBlockSideTrick RAYCAST_SIDE = register("raycast_side", new RaycastBlockSideTrick());
    public static final RaycastEntityTrick RAYCAST_ENTITY = register("raycast_entity", new RaycastEntityTrick());
    public static final AddVelocityTrick ADD_VELOCITY = register("add_velocity", new AddVelocityTrick());
    public static final PolymorphTrick POLYMORPH = register("polymorph", new PolymorphTrick());
    public static final DispelPolymorphTrick DISPEL_POLYMORPH = register("dispel_polymorph", new DispelPolymorphTrick());
    public static final StoreEntityTrick STORE_ENTITY = register("store_entity", new StoreEntityTrick());
    public static final ReleaseEntityTrick RELEASE_ENTITY = register("release_entity", new ReleaseEntityTrick());
    public static final GetScaleTrick GET_SCALE = register("get_scale", new GetScaleTrick());
    public static final SetScaleTrick SET_SCALE = register("set_scale", new SetScaleTrick());

    // Entity Locating
    public static final BlockFindEntityTrick BLOCK_FIND_ENTITY = register("block_find_entity", new BlockFindEntityTrick());
    public static final RangeFindEntityTrick RANGE_FIND_ENTITY = register("range_find_entity", new RangeFindEntityTrick());

    // Math
    public static final AddTrick ADD = register("add", new AddTrick());
    public static final SubtractTrick SUBTRACT = register("subtract", new SubtractTrick());
    public static final MultiplyTrick MULTIPLY = register("multiply", new MultiplyTrick());
    public static final DivideTrick DIVIDE = register("divide", new DivideTrick());
    public static final ModuloTrick MODULO = register("modulo", new ModuloTrick());
    public static final FloorTrick FLOOR = register("floor", new FloorTrick());
    public static final CeilTrick CEIL = register("ceil", new CeilTrick());
    public static final RoundTrick ROUND = register("round", new RoundTrick());
    public static final MaxTrick MAX = register("max", new MaxTrick());
    public static final MinTrick MIN = register("min", new MinTrick());
    public static final SqrtTrick SQRT = register("sqrt", new SqrtTrick());
    public static final SinTrick SIN = register("sin", new SinTrick());
    public static final CosTrick COS = register("cos", new CosTrick());
    public static final TanTrick TAN = register("tan", new TanTrick());
    public static final PowerTrick POWER = register("power", new PowerTrick());

    // Vector
    public static final ExtractXTrick EXTRACT_X = register("extract_x", new ExtractXTrick());
    public static final ExtractYTrick EXTRACT_Y = register("extract_y", new ExtractYTrick());
    public static final ExtractZTrick EXTRACT_Z = register("extract_z", new ExtractZTrick());
    public static final LengthTrick LENGTH = register("length", new LengthTrick());
    public static final DotProductTrick DOT_PRODUCT = register("dot_product", new DotProductTrick());
    public static final CrossProductTrick CROSS_PRODUCT = register("cross_product", new CrossProductTrick());
    public static final NormalizeTrick NORMALIZE = register("normalize", new NormalizeTrick());
    public static final AlignVectorTrick ALIGN_VECTOR = register("align_vector", new AlignVectorTrick());
    public static final InvertTrick INVERT = register("invert", new InvertTrick());
    public static final MergeVectorTrick MERGE_VECTOR = register("merge_vector", new MergeVectorTrick());

    // Boolean
    public static final IfElseTrick IF_ELSE = register("if_else", new IfElseTrick());
    public static final EqualsTrick EQUALS = register("equals", new EqualsTrick());
    public static final NotEqualsTrick NOT_EQUALS = register("not_equals", new NotEqualsTrick());
    public static final AllTrick ALL = register("all", new AllTrick());
    public static final AnyTrick ANY = register("any", new AnyTrick());
    public static final NoneTrick NONE = register("none", new NoneTrick());
    public static final GreaterThanTrick GREATER_THAN = register("greater_than", new GreaterThanTrick());
    public static final LesserThanTrick LESSER_THAN = register("lesser_than", new LesserThanTrick());

    // List
    public static final ListAddTrick LIST_ADD = register("list_add", new ListAddTrick());
    public static final ListAddRangeTrick LIST_ADD_RANGE = register("list_add_range", new ListAddRangeTrick());
    public static final ListCreateTrick LIST_CREATE = register("list_create", new ListCreateTrick());
    public static final ListGetTrick LIST_GET = register("list_get", new ListGetTrick());
    public static final ListIndexOfTrick LIST_INDEX_OF = register("list_index_of", new ListIndexOfTrick());
    public static final ListInsertTrick LIST_INSERT = register("list_insert", new ListInsertTrick());
    public static final ListRemoveElementTrick LIST_REMOVE_ELEMENT = register("list_remove_element", new ListRemoveElementTrick());
    public static final ListRemoveTrick LIST_REMOVE = register("list_remove", new ListRemoveTrick());
    public static final ListSizeTrick LIST_SIZE = register("list_size", new ListSizeTrick());

    // Map
    public static final MapGetTrick MAP_GET = register("map_get", new MapGetTrick());
    public static final MapInsertTrick MAP_INSERT = register("map_insert", new MapInsertTrick());
    public static final MapRemoveTrick MAP_REMOVE = register("map_remove", new MapRemoveTrick());

    // Tree
    public static final LocateGlyphTrick LOCATE_GLYPH = register("locate_glyph", new LocateGlyphTrick());
    public static final LocateGlyphsTrick LOCATE_GLYPHS = register("locate_glyphs", new LocateGlyphsTrick());
    public static final RetrieveGlyphTrick RETRIEVE_GLYPH = register("retrieve_glyph", new RetrieveGlyphTrick());
    public static final SetGlyphTrick SET_GLYPH = register("set_glyph", new SetGlyphTrick());
    public static final RetrieveSubtreeTrick RETRIEVE_SUBTREE = register("retrieve_subtree", new RetrieveSubtreeTrick());
    public static final SetSubtreeTrick SET_SUBTREE = register("set_subtree", new SetSubtreeTrick());
    public static final AddSubtreeTrick ADD_LEAF = register("add_subtree", new AddSubtreeTrick());
    public static final RemoveSubtreeTrick REMOVE_SUBTREE = register("remove_subtree", new RemoveSubtreeTrick());
    public static final GetSubPartsTrick GET_SUBPARTS = register("get_subparts", new GetSubPartsTrick());
    public static final EscapePatternTrick ESCAPE_PATTERN = register("escape_pattern", new EscapePatternTrick());

    // Block
    public static final BreakBlockTrick BREAK_BLOCK = register("break_block", new BreakBlockTrick());
    public static final PlaceBlockTrick PLACE_BLOCK = register("place_block", new PlaceBlockTrick());
    public static final SwapBlockTrick SWAP_BLOCK = register("swap_block", new SwapBlockTrick());
    public static final ConjureFlowerTrick CONJURE_FLOWER = register("conjure_flower", new ConjureFlowerTrick());
    public static final ConjureWaterTrick CONJURE_WATER = register("conjure_water", new ConjureWaterTrick());
    public static final ConjureLightTrick CONJURE_LIGHT = register("conjure_light", new ConjureLightTrick());
    public static final DrainFluidTrick DRAIN_FLUID = register("drain_fluid", new DrainFluidTrick());
    public static final CheckBlockTrick CHECK_BLOCK = register("check_block", new CheckBlockTrick());
    public static final CanPlaceTrick CAN_PLACE_BLOCK = register("can_place_block", new CanPlaceTrick());
    public static final GetBlockHardnessTrick GET_BLOCK_HARDNESS = register("get_block_hardness", new GetBlockHardnessTrick());
    public static final PowerResonatorTrick POWER_RESONATOR = register("power_resonator", new PowerResonatorTrick());
    public static final CheckResonatorTrick CHECK_RESONATOR = register("check_resonator", new CheckResonatorTrick());
    public static final GetRedstonePowerTrick GET_REDSTONE_POWER = register("get_redstone_power", new GetRedstonePowerTrick());

    // Inventory
    public static final ImportTrick IMPORT = register("import", new ImportTrick());
    public static final ImportHatTrick IMPORT_HAT = register("import_hat", new ImportHatTrick());
    public static final CheckHatTrick CHECK_HAT = register("check_hat", new CheckHatTrick());
    public static final SetHatTrick SET_HAT = register("set_hat", new SetHatTrick());
    public static final OtherHandTrick OTHER_HAND = register("other_hand", new OtherHandTrick());
    public static final OtherHandSlotTrick OTHER_HAND_SLOT = register("other_hand_slot", new OtherHandSlotTrick());
    public static final GetItemInSlotTrick GET_ITEM_IN_SLOT = register("get_item_in_slot", new GetItemInSlotTrick());
    public static final GetManaInSlotTrick GET_MANA_IN_SLOT = register("get_mana_in_slot", new GetManaInSlotTrick());
    public static final GetMaxManaInSlotTrick GET_MAX_MANA_IN_SLOT = register("get_max_mana_in_slot", new GetMaxManaInSlotTrick());
    public static final GetInventorySlotTrick GET_INVENTORY_SLOT = register("get_inventory_slot", new GetInventorySlotTrick());
    public static final DropStackFromSlotTrick DROP_STACK_FROM_SLOT = register("drop_stack_from_slot", new DropStackFromSlotTrick());
    public static final SwapSlotTrick SWAP_SLOT = register("swap_slot", new SwapSlotTrick());

    // Projectile
    public static final SummonArrowTrick SUMMON_ARROW = register("summon_arrow", new SummonArrowTrick());
    public static final SummonFireballTrick SUMMON_FIREBALL = register("summon_fireball", new SummonFireballTrick());
    public static final SummonDragonBreathTrick SUMMON_DRAGON_BREATH = register("summon_dragon_breath", new SummonDragonBreathTrick());
    public static final SummonTntTrick SUMMON_TNT = register("summon_tnt", new SummonTntTrick());

    // Dimension
    public static final GetDimensionTrick GET_DIMENSION = register("get_dimension", new GetDimensionTrick());

    // Flecks
    public static final LineFleckTrick DRAW_LINE = register("draw_line", new LineFleckTrick());
    public static final SpellFleckTrick DRAW_SPELL = register("draw_spell", new SpellFleckTrick());
    public static final GetFlecksTrick GET_FLECKS = register("get_flecks", new GetFlecksTrick());

    // Misc
    public static final TypeFragmentTrick TYPE_FRAGMENT = register("type_fragment", new TypeFragmentTrick());
    public static final HashValuesTrick HASH_VALUES = register("hash_values", new HashValuesTrick());
    public static final DelayExecutionTrick DELAY_EXECUTION = register("delay_execution", new DelayExecutionTrick());
    public static final PinChunkTrick PIN_CHUNK = register("pin_chunk", new PinChunkTrick());
    public static final ShowBarTrick SHOW_BAR = register("show_bar", new ShowBarTrick());
    public static final ClearBarTrick CLEAR_BAR = register("clear_bar", new ClearBarTrick());

    // Mana
    public static final BatteryCreationTrick BATTERY_CREATION = register("battery_creation", new BatteryCreationTrick());
    public static final PushManaTrick PUSH_MANA = register("push_mana", new PushManaTrick());
    public static final PullManaTrick PULL_MANA = register("pull_mana", new PullManaTrick());

    @ApiStatus.Internal
    public static <T extends Trick> T register(String path, T trick) {
        return Registry.register(REGISTRY, Trickster.id(path), trick);
    }

    @Nullable
    public static Trick lookup(Pattern pattern) {
        return LOOKUP.get(pattern);
    }

    public static void register() {
        // init the class :brombeere:
    }
}
