package dev.enjarai.trickster.spell.mana.generation;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.MutableManaPool;
import net.minecraft.world.entity.EntityLike;

//earlier i made this use any ManaPool, but it added complexity, and requirements to the interface, for like
// no point, so i removed it
public interface HasMana extends EntityLike {
    MutableManaPool getPool();
}
