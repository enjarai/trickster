package dev.enjarai.trickster.spell.trick;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public abstract class DistortionTrick<T extends DistortionTrick<T>> extends Trick<T> {
    private static final int CACHE_SIZE = 20;

    private final Map<Fragment[], Fragment> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Fragment[], Fragment> eldest) {
            return size() > CACHE_SIZE;
        }
    };

    public DistortionTrick(Pattern pattern) {
        super(pattern);
    }

    public DistortionTrick(Pattern pattern, List<Signature<T>> handlers) {
        super(pattern, handlers);
    }

    public DistortionTrick(Pattern pattern, Signature<T> primary) {
        super(pattern, primary);
    }

    @Override
    public EvaluationResult activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var fragmentArray = fragments.toArray(new Fragment[0]); // Java is a bad language and special-cases this so it's optimized away and returns an array of the correct size
        EvaluationResult result = cache.get(fragmentArray);

        if (result == null) {
            result = super.activate(ctx, fragments);

            if (result instanceof Fragment fragment) {
                cache.put(fragmentArray, fragment);
            }
        }

        return result;
    }
}
