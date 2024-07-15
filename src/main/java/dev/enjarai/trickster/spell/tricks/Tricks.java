package dev.enjarai.trickster.spell.tricks;

import com.mojang.serialization.Lifecycle;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.tricks.entity.query.*;
import dev.enjarai.trickster.spell.tricks.func.ClosureTrick;
import dev.enjarai.trickster.spell.tricks.func.ExecuteTrick;
import dev.enjarai.trickster.spell.tricks.func.IteratorTrick;
import dev.enjarai.trickster.spell.tricks.func.LoadArgumentTrick;
import dev.enjarai.trickster.spell.tricks.basic.*;
import dev.enjarai.trickster.spell.tricks.block.*;
import dev.enjarai.trickster.spell.tricks.bool.*;
import dev.enjarai.trickster.spell.tricks.entity.*;
import dev.enjarai.trickster.spell.tricks.event.CreateSpellCircleTrick;
import dev.enjarai.trickster.spell.tricks.event.DeleteSpellCircleTrick;
import dev.enjarai.trickster.spell.tricks.func.SupplierTrick;
import dev.enjarai.trickster.spell.tricks.inventory.*;
import dev.enjarai.trickster.spell.tricks.list.*;
import dev.enjarai.trickster.spell.tricks.math.*;
import dev.enjarai.trickster.spell.tricks.misc.TypeFragmentTrick;
import dev.enjarai.trickster.spell.tricks.projectile.SummonArrowTrick;
import dev.enjarai.trickster.spell.tricks.tree.*;
import dev.enjarai.trickster.spell.tricks.vector.*;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Tricks {
    private static final Map<Pattern, Trick> LOOKUP = new HashMap<>();

    public static final RegistryKey<Registry<Trick>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("trick"));
    public static final Registry<Trick> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable()) {
        @Override
        public RegistryEntry.Reference<Trick> add(RegistryKey<Trick> key, Trick value, RegistryEntryInfo info) {
            LOOKUP.put(value.getPattern(), value);
            return super.add(key, value, info);
        }
    };

    // Functions
    public static final ExecuteTrick EXECUTE = register("execute", new ExecuteTrick());
    public static final IteratorTrick ITERATOR = register("iterator", new IteratorTrick());
    public static final ClosureTrick CLOSURE = register("closure", new ClosureTrick());
    public static final SupplierTrick SUPPLIER = register("supplier", new SupplierTrick());
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
    public static final ReadSpellTrick READ_SPELL = register("read_spell", new ReadSpellTrick());
    public static final WriteSpellTrick WRITE_SPELL = register("write_spell", new WriteSpellTrick());
    public static final ClearSpellTrick CLEAR_SPELL = register("clear_spell", new ClearSpellTrick());
    public static final ReadCrowMindTrick READ_CROW_MIND = register("read_crow_mind", new ReadCrowMindTrick());
    public static final WriteCrowMindTrick WRITE_CROW_MIND = register("write_crow_mind", new WriteCrowMindTrick());

    // Caster
    public static final ReflectionTrick REFLECTION = register("reflection", new ReflectionTrick());
    public static final CasterReflectionTrick CASTER_REFLECTION = register("caster_reflection", new CasterReflectionTrick());
    public static final CostTrick COST = register("cost", new CostTrick());
    public static final ManaReflectionTrick MANA_REFLECTION = register("mana_reflection", new ManaReflectionTrick());
    public static final FacingReflectionTrick FACING_REFLECTION = register("facing_reflection", new FacingReflectionTrick());

    // Entity
    public static final GetPositionTrick GET_POSITION = register("get_position", new GetPositionTrick());
    public static final GetEntityTypeTrick GET_ENTITY_TYPE = register("get_entity_type", new GetEntityTypeTrick());
    public static final GetFacingTrick GET_FACING = register("get_facing", new GetFacingTrick());
    public static final GetEntityHealthTrick GET_HEALTH = register("get_health", new GetEntityHealthTrick());
    public static final GetEntityMaxHealthTrick GET_MAX_HEALTH = register("get_max_health", new GetEntityMaxHealthTrick());
    public static final GetEntityArmourTrick GET_ARMOUR_VALUE = register("get_armour", new GetEntityArmourTrick());
    public static final HeightReflectionTrick HEIGHT_REFLECTION = register("height_reflection", new HeightReflectionTrick());
    public static final SneakingReflectionTrick SNEAKING_REFLECTION = register("sneaking_reflection", new SneakingReflectionTrick());
    public static final RaycastBlockPosTrick RAYCAST = register("raycast", new RaycastBlockPosTrick());
    public static final RaycastBlockSideTrick RAYCAST_SIDE = register("raycast_side", new RaycastBlockSideTrick());
    public static final RaycastEntityTrick RAYCAST_ENTITY = register("raycast_entity", new RaycastEntityTrick());
    public static final AddVelocityTrick ADD_VELOCITY = register("add_velocity", new AddVelocityTrick());
    public static final PolymorphTrick POLYMORPH = register("polymorph", new PolymorphTrick());
    public static final DispelPolymorphTrick DISPEL_POLYMORPH = register("dispel_polymorph", new DispelPolymorphTrick());
    public static final GetEntityManaTrick GET_MANA = register("get_mana", new GetEntityManaTrick());
    public static final LeechEntityManaTrick LEECH_MANA = register("leech_mana", new LeechEntityManaTrick());

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

    // Vector
    public static final ExtractXTrick EXTRACT_X = register("extract_x", new ExtractXTrick());
    public static final ExtractYTrick EXTRACT_Y = register("extract_y", new ExtractYTrick());
    public static final ExtractZTrick EXTRACT_Z = register("extract_z", new ExtractZTrick());
    public static final LengthTrick LENGTH = register("length", new LengthTrick());
    public static final DotProductTrick DOT_PRODUCT = register("dot_product", new DotProductTrick());
    public static final CrossProductTrick CROSS_PRODUCT = register("cross_product", new CrossProductTrick());
    public static final NormalizeTrick NORMALIZE = register("normalize", new NormalizeTrick());
    public static final AlignVectorTrick ALIGN_VECTOR = register("align_vector", new AlignVectorTrick());
    public static final ReverseVectorTrick REVERSE_ALIGN_VECTOR = register("reverse_align_vector", new ReverseVectorTrick());
    public static final MergeVectorTrick MERGE_VECTOR = register("merge_vector", new MergeVectorTrick());

    // Boolean
    public static final IfElseTrick IF_ELSE = register("if_else", new IfElseTrick());
    public static final EqualsTrick EQUALS = register("equals", new EqualsTrick());
    public static final NotEqualsTrick NOT_EQUALS = register("not_equals", new NotEqualsTrick());
    public static final AllTrick ALL = register("all", new AllTrick());
    public static final AnyTrick ANY = register("any", new AnyTrick());
    public static final NoneTrick NONE = register("none", new NoneTrick());

    // List
    public static final ListAddTrick LIST_ADD = register("list_add", new ListAddTrick());
    public static final ListAddRangeTrick LIST_ADD_RANGE = register("list_add_range", new ListAddRangeTrick());
    public static final ListCreateTrick LIST_CREATE = register("list_create", new ListCreateTrick());
    public static final ListGetTrick LIST_GET = register("list_get", new ListGetTrick());
    public static final ListIndexOfTrick LIST_INDEX_OF = register("list_index_of", new ListIndexOfTrick());
    public static final ListInsertTrick LIST_INSERT = register("list_insert", new ListInsertTrick());
    public static final ListRemoveElementTrick LIST_REMOVE_ELEMENT = register("list_remove_element", new ListRemoveElementTrick());
    public static final ListRemoveTrick LIST_REMOVE = register("list_remove", new ListRemoveTrick());

    // Tree
    public static final LocateGlyphTrick LOCATE_GLYPH = register("locate_glyph", new LocateGlyphTrick());
    public static final LocateGlyphsTrick LOCATE_GLYPHS = register("locate_glyphs", new LocateGlyphsTrick());
    public static final RetrieveGlyphTrick RETRIEVE_GLYPH = register("retrieve_glyph", new RetrieveGlyphTrick());
    public static final SetGlyphTrick SET_GLYPH = register("set_glyph", new SetGlyphTrick());
    public static final RetrieveSubtreeTrick RETRIEVE_SUBTREE= register("retrieve_subtree", new RetrieveSubtreeTrick());
    public static final SetSubtreeTrick SET_SUBTREE = register("set_subtree", new SetSubtreeTrick());
    public static final AddSubtreeTrick ADD_LEAF = register("add_subtree", new AddSubtreeTrick());
    public static final RemoveSubtreeTrick REMOVE_SUBTREE = register("remove_subtree", new RemoveSubtreeTrick());

    // Events
    public static final CreateSpellCircleTrick CREATE_SPELL_CIRCLE = register("create_spell_circle", new CreateSpellCircleTrick());
    public static final DeleteSpellCircleTrick DELETE_SPELL_CIRCLE = register("delete_spell_circle", new DeleteSpellCircleTrick());

    // Blocks
    public static final BreakBlockTrick BREAK_BLOCK = register("break_block", new BreakBlockTrick());
    public static final SwapBlockTrick SWAP_BLOCK = register("swap_block", new SwapBlockTrick());
    public static final ConjureFlowerTrick CONJURE_FLOWER = register("conjure_flower", new ConjureFlowerTrick());
    public static final ConjureWaterTrick CONJURE_WATER = register("conjure_water", new ConjureWaterTrick());
    public static final CheckBlockTrick CHECK_BLOCK = register("check_block", new CheckBlockTrick());
    public static final CanPlaceTrick CAN_PLACE_BLOCK = register("can_place_block", new CanPlaceTrick());
    public static final GetBlockHardnessTrick GET_BLOCK_HARDNESS = register("get_block_hardness", new GetBlockHardnessTrick());
    public static final DestabilizeBlockTrick DESTABILIZE_BLOCK = register("destabilize_block", new DestabilizeBlockTrick());
    public static final DisguiseBlockTrick DISGUISE_BLOCK = register("disguise_block", new DisguiseBlockTrick());
    public static final DispelBlockDisguiseTrick DISPEL_BLOCK_DISGUISE = register("dispel_block_disguise", new DispelBlockDisguiseTrick());
    public static final PowerResonatorTrick POWER_RESONATOR = register("power_resonator", new PowerResonatorTrick());
    public static final CheckResonatorTrick CHECK_RESONATOR = register("check_resonator", new CheckResonatorTrick());

    // Inventory
    public static final ImportTrick IMPORT = register("import", new ImportTrick());
    public static final ImportHatTrick IMPORT_HAT = register("import_hat", new ImportHatTrick());
    public static final CheckHatTrick CHECK_HAT = register("check_hat", new CheckHatTrick());
    public static final OtherHandTrick OTHER_HAND = register("other_hand", new OtherHandTrick());
    public static final GetItemInSlotTrick GET_ITEM_IN_SLOT = register("get_item_in_slot", new GetItemInSlotTrick());
    public static final GetInventorySlotTrick GET_INVENTORY_SLOT = register("get_inventory_slot", new GetInventorySlotTrick());

    // Projectile
    public static final SummonArrowTrick SUMMON_ARROW = register("summon_arrow", new SummonArrowTrick());

    // Misc
    public static final TypeFragmentTrick TYPE_FRAGMENT = register("type_fragment", new TypeFragmentTrick());

    private static <T extends Trick> T register(String path, T trick) {
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
