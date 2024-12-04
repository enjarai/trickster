package dev.enjarai.trickster.util.theft;

import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SigGen {
    @ApiStatus.Internal
    public static void gen(String[] args) {
        String typesSpot = "{types}";

        String constructorNum = "{cnum}";

        //--

        String numberSpot = "{num}";

        String structFieldTemplate = "ArgType<T{num}> t{num}";

        String structFieldArgs = "{args}";
        String handlerArgs = "{argsv}";

        //--

        String structSerCallTemplate =
            """
                        var args{num} = t{num}.isolate(0, fragments);
                        fragments = fragments.subList(args{num}.size(), fragments.size());

                        if (!t{num}.match(args{num})) {
                            return false;
                        }
            """;

        String structSerCallsSpot = "{matches}";

        //--

        String structDeserCallTemplate =
            """
                        var args{num} = t{num}.isolate(0, fragments);
                        var v{num} = t{num}.compose(args{num});
                        fragments = fragments.subList(args{num}.size(), fragments.size());
            """;

        String structDeserCallsSpot = "{collects}";

        String method =
                """
                static <T extends Trick, {types}> TrickSignature<T> of({args}, Function{cnum}<T, SpellContext, {types}, Fragment> handler) {
                    return new TrickSignature<T>() {
                        @Override
                        public boolean match(List<Fragment> fragments) {
                {matches}
                            return true;
                        }

                        @Override
                        public Fragment run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                {collects}
                            return handler.apply(trick, ctx, {argsv});
                        }
                    };
                }

                """;

        Map<Integer, String> structTypes = new LinkedHashMap<>();
        Map<Integer, String> structArgs = new LinkedHashMap<>();

        Map<Integer, String> structFields = new LinkedHashMap<>();
        Map<Integer, String> structSerCalls = new LinkedHashMap<>();
        Map<Integer, String> structDeserCalls = new LinkedHashMap<>();

        String allMethods = "";

        for (int i = 1; i < 7; i++) {
            structTypes.put(i, "T" + i);
            structArgs.put(i, "v" + i);

            structFields.put(i, structFieldTemplate.replace(numberSpot, String.valueOf(i)));
            structSerCalls.put(i, structSerCallTemplate.replace(numberSpot, String.valueOf(i)));
            structDeserCalls.put(i, structDeserCallTemplate.replace(numberSpot, String.valueOf(i)));

            String types = String.join(", ", structTypes.values());

            String fieldArgs = String.join(", ", structFields.values());

            String serCalls = String.join("\n", structSerCalls.values());
            String deserCalls = String.join("\n", structDeserCalls.values());
            String handlerArgsString = String.join(", ", structArgs.values());

            String newMethod = method
                    .replace(constructorNum, String.valueOf(i + 2))
                    .replace(typesSpot, types)
                    .replace(structFieldArgs, fieldArgs)
                    .replace(handlerArgs, handlerArgsString)
                    .replace(structSerCallsSpot, serCalls)
                    .replace(structDeserCallsSpot, deserCalls);

            allMethods = allMethods.concat(newMethod);
        }

        try (FileWriter myWriter = new FileWriter("test.txt")) {
            myWriter.write(allMethods);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
