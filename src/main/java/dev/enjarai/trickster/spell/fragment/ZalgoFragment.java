package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.StructEndec;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;

import java.util.List;

public record ZalgoFragment(int index) implements Fragment {
    public static final StructEndec<ZalgoFragment> ENDEC = EndecTomfoolery.unit(ZalgoFragment::new);
    public static final Random RANDOM = new LocalRandom(0xABABABA);
    public static final List<String> SILLIES = List.of(
            "amogus",
            "what should i put here",
            "mineblock lore",
            "maybe",
            "or",
            "i can think of something funnier",
            "cause mineblock lore is kinda overrated",
            "which obviously means",
            "we should kick louise from the thread",
            ":brombeere:",
            "eh, ill leave that up to beno",
            "i want to work on this now",
            "this mod",
            "cause its a really cool concept",
            "and i shouldnt forget about it",
            "like i do with so many",
            "other mods",
            "yknow",
            "im really bad at this",
            "like",
            "not modding",
            "but maintaining a mod",
            "and continuing to improve it",
            "i get bored too easily",
            "maybe its cause i have",
            "too many other mods",
            "thats probably part of it",
            "i should just drop some of them",
            "or find new maintainers",
            "recursive resources for example",
            "like, its a cool mod",
            "but i dont really...",
            "care all that much?",
            "like",
            "someone would probably make this",
            "and better",
            "even if i didnt",
            "and its not like its",
            "*particularly* fun to work on",
            "ha, particular",
            "get it??",
            "actually",
            "i probably have enough strings now",
            "time to wrap this up");

    public ZalgoFragment() {
        this(RANDOM.nextInt(SILLIES.size()));
    }

    @Override
    public FragmentType<?> type() {
        return FragmentType.ZALGO;
    }

    @Override
    public Text asText() {
        return Text.literal(SILLIES.get(index)).fillStyle(Style.EMPTY.withObfuscated(true));
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public int getWeight() {
        return 4;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ZalgoFragment that) {
            return this.index == that.index;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return SILLIES.get(index).hashCode();
    }

    @Override
    public boolean fuzzyEquals(Fragment other) {
        return other instanceof ZalgoFragment;
    }

    @Override
    public int fuzzyHash() {
        return RANDOM.nextInt();
    }
}
