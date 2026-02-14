package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncompatibleTypesBlunder;
import dev.enjarai.trickster.spell.blunder.InvalidInputsBlunder;
import dev.enjarai.trickster.spell.trick.Tricks;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.ColorHelper;
import org.joml.Vector4f;

import java.util.List;

import static net.minecraft.util.math.ColorHelper.Argb.*;
import static net.minecraft.util.math.ColorHelper.Argb.getAlpha;
import static net.minecraft.util.math.ColorHelper.Argb.getArgb;
import static net.minecraft.util.math.ColorHelper.Argb.getBlue;
import static net.minecraft.util.math.ColorHelper.Argb.getGreen;
import static net.minecraft.util.math.ColorHelper.Argb.getRed;

public record ColorFragment(int color) implements Fragment, AddableFragment, MultiplicableFragment, AverageableFragment, RoundableFragment {

    public static final StructEndec<ColorFragment> ENDEC = StructEndecBuilder.of(
            Endec.INT.fieldOf("color", ColorFragment::color),
            ColorFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.COLOR;
    }

    @Override
    public Text asText() {

        return Text.literal("#(%02X)%02X%02X%02X".formatted(
                ColorHelper.Argb.getAlpha(color),
                ColorHelper.Argb.getRed(color),
                ColorHelper.Argb.getGreen(color),
                ColorHelper.Argb.getBlue(color)
        ));
    }

    public Text asFormattedText() {
        return asText().copy().withColor(color);
    }

    @Override
    public int getWeight() {
        return 4;
    }

    public Vector4f asVector() {
        return argbAsVector(color);
    }

    public static Vector4f argbAsVector(int color) {
        return new Vector4f(
                ColorHelper.Argb.getRed(color) / 255f,
                ColorHelper.Argb.getGreen(color) / 255f,
                ColorHelper.Argb.getBlue(color) / 255f,
                ColorHelper.Argb.getAlpha(color) / 255f
        );
    }

    public static ColorFragment fromVector(Vector4f vec) {
        return new ColorFragment(ColorHelper.Argb.getArgb(((int) vec.w * 255), ((int) vec.x * 255), ((int) vec.y * 255), ((int) vec.z * 255)));
    }

    @Override
    public AddableFragment add(Fragment other) throws BlunderException {
        if (other instanceof ColorFragment that) {
            return ColorFragment.fromVector(asVector().sub(1, 1, 1, 1).mul(-1).mul(that.asVector().sub(1, 1, 1, 1).mul(-1)).sub(1, 1, 1, 1).mul(-1));
        }
        throw new IncompatibleTypesBlunder(Tricks.ADD);
    }

    @Override
    public MultiplicableFragment multiply(Fragment other) throws BlunderException {
        if (other instanceof ColorFragment that) {
            return ColorFragment.fromVector(asVector().mul(that.asVector()));
        }
        throw new IncompatibleTypesBlunder(Tricks.MULTIPLY);
    }

    @Override
    public AverageableFragment avg(List<AverageableFragment> other) throws BlunderException {

        other.addFirst(this);
        int r = 0, g = 0, b = 0, a = 0, l = 0;
        int m = other.size();

        for (AverageableFragment fragment : other) {
            if (fragment instanceof ColorFragment(int color)) {
                r += getRed(color);
                g += getGreen(color);
                b += getBlue(color);
                a += getAlpha(color);
                l += Math.max(getRed(color), Math.max(getGreen(color), getBlue(color)));
            } else {
                throw new InvalidInputsBlunder(Tricks.AVG, List.of());
            }
        }

        r /= m;
        g /= m;
        b /= m;

        float f = (float) l / (float) m;
        float s = (float) Math.max(r, Math.max(g, b));
        r = (int) ((float) r * f / s);
        g = (int) ((float) g * f / s);
        b = (int) ((float) b * f / s);
        return new ColorFragment(getArgb(a / m, r, g, b));
    }

    @Override
    public RoundableFragment floor() throws BlunderException {
        int result = 0;
        float distance = 3;
        var self = asVector();
        for (DyeColor dye : DyeColor.values()) {
            var dist = argbAsVector(dye.getEntityColor()).sub(self).lengthSquared();
            if (dist < distance) {
                distance = dist;
                result = dye.getEntityColor();
            }
        }
        return new ColorFragment(result);
    }

    @Override
    public RoundableFragment ceil() throws BlunderException {
        int result = 0;
        float distance = 3;
        var self = asVector();
        for (DyeColor dye : DyeColor.values()) {
            var dist = argbAsVector(dye.getSignColor()).sub(self).lengthSquared();
            if (dist < distance) {
                distance = dist;
                result = ColorHelper.Argb.withAlpha(255, dye.getSignColor());
            }
        }
        return new ColorFragment(result);
    }

    @Override
    public RoundableFragment round() throws BlunderException {
        int result = 0;
        float distance = 3;
        var self = asVector();
        for (DyeColor dye : DyeColor.values()) {
            var dist = argbAsVector(dye.getEntityColor()).sub(self).lengthSquared();
            if (dist < distance) {
                distance = dist;
                result = dye.getEntityColor();
            }
            dist = argbAsVector(ColorHelper.Argb.withAlpha(255, dye.getSignColor())).sub(self).lengthSquared();
            if (dist < distance) {
                distance = dist;
                result = ColorHelper.Argb.withAlpha(255, dye.getSignColor());
            }
        }
        return new ColorFragment(result);
    }
}
