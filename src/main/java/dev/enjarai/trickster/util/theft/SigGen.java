package dev.enjarai.trickster.util.theft;

import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SigGen {
    @ApiStatus.Internal
    public static void main(String[] args) {
        String typesSpot = "{types}";

        String constructorNum = "{cnum}";

        // --

        String numberSpot = "{num}";

        String structFieldTemplate = "ArgType<T{num}> t{num}";

        String structFieldArgs = "{args}";
        String handlerArgs = "{argsv}";

        // --

        String structSerCallTemplate = """
                            if (!t{num}.isolateAndMatch(fragments)) {
                                return false;
                            }
                            fragments = fragments.subList(t{num}.argc(fragments), fragments.size());
                """;

        String structSerCallsSpot = "{matches}";

        // --

        String structDeserCallTemplate = """
                            var args{num} = t{num}.isolate(0, fragments);
                            var v{num} = t{num}.compose(trick, ctx, args{num});
                            fragments = fragments.subList(args{num}.size(), fragments.size());
                """;

        String structDeserCallsSpot = "{collects}";

        // --

        String textsTemplate = """
                            text = text.append(t{num}.asText());
                """;

        String textsJoiner = """
                            text = text.append(", ");
                """;

        String textsSpot = "{asTexts}";

        String method = """
                static <T extends Trick<T>, {types}> Signature<T> of({args}, Function{cnum}<T, SpellContext, {types}, EvaluationResult> handler) {
                    return new Signature<T>() {
                        @Override
                        public boolean match(List<Fragment> fragments) {
                {matches}
                            return true;
                        }

                        @Override
                        public EvaluationResult run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                {collects}
                            return handler.apply(trick, ctx, {argsv});
                        }
                
                        @Override
                        public MutableText asText() {
                            var text = Text.empty();
            
                {asTexts}
                            return text;
                        }
                    };
                }

                """;

        Map<Integer, String> structTypes = new LinkedHashMap<>();
        Map<Integer, String> structArgs = new LinkedHashMap<>();

        Map<Integer, String> structFields = new LinkedHashMap<>();
        Map<Integer, String> structSerCalls = new LinkedHashMap<>();
        Map<Integer, String> structDeserCalls = new LinkedHashMap<>();

        Map<Integer, String> textsCalls = new LinkedHashMap<>();

        String allMethods = "";

        for (int i = 1; i < 7; i++) {
            structTypes.put(i, "T" + i);
            structArgs.put(i, "v" + i);

            structFields.put(i, structFieldTemplate.replace(numberSpot, String.valueOf(i)));
            structSerCalls.put(i, structSerCallTemplate.replace(numberSpot, String.valueOf(i)));
            structDeserCalls.put(i, structDeserCallTemplate.replace(numberSpot, String.valueOf(i)));

            textsCalls.put(i, textsTemplate.replace(numberSpot, String.valueOf(i)));

            String types = String.join(", ", structTypes.values());

            String fieldArgs = String.join(", ", structFields.values());

            String serCalls = String.join("\n", structSerCalls.values());
            String deserCalls = String.join("\n", structDeserCalls.values());
            String handlerArgsString = String.join(", ", structArgs.values());

            String textsString = String.join(textsJoiner, textsCalls.values());

            String newMethod = method
                    .replace(constructorNum, String.valueOf(i + 2))
                    .replace(typesSpot, types)
                    .replace(structFieldArgs, fieldArgs)
                    .replace(handlerArgs, handlerArgsString)
                    .replace(structSerCallsSpot, serCalls)
                    .replace(structDeserCallsSpot, deserCalls)
                    .replace(textsSpot, textsString);

            allMethods = allMethods.concat(newMethod);
        }

        try (FileWriter myWriter = new FileWriter("test.txt")) {
            myWriter.write(allMethods);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
