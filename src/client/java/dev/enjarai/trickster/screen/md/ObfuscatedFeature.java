package dev.enjarai.trickster.screen.md;

import io.wispforest.lavendermd.Lexer;
import io.wispforest.lavendermd.MarkdownFeature;
import io.wispforest.lavendermd.Parser;
import io.wispforest.lavendermd.compiler.MarkdownCompiler;
import net.minecraft.text.Style;

import java.util.function.UnaryOperator;

public class ObfuscatedFeature implements MarkdownFeature {
    @Override
    public String name() {
        return "obfuscated";
    }

    @Override
    public boolean supportsCompiler(MarkdownCompiler<?> compiler) {
        return true;
    }

    @Override
    public void registerTokens(TokenRegistrar registrar) {
        registrar.registerToken(Lexer.Token.lexFromChar(HashToken::new), '#');
    }

    @Override
    public void registerNodes(NodeRegistrar registrar) {
        this.registerDoubleTokenFormatting(registrar, HashToken.class, style -> style.withObfuscated(true));
    }

    private <T extends Lexer.Token> void registerDoubleTokenFormatting(NodeRegistrar registrar, Class<T> tokenClass, UnaryOperator<Style> formatting) {
        registrar.registerNode((parser, left1, tokens) -> {
            var left2 = tokens.nibble();

            int pointer = tokens.pointer();
            var content = parser.parseUntil(tokens, tokenClass);

            if (tokenClass.isInstance(tokens.peek()) && tokenClass.isInstance(tokens.peek(1))) {
                tokens.skip(2);
                return new Parser.FormattingNode(formatting).addChild(content);
            } else {
                tokens.setPointer(pointer);
                return new Parser.TextNode(left1.content() + left2.content());
            }
        }, (token, tokens) -> tokenClass.isInstance(token) && tokenClass.isInstance(tokens.peek()) ? tokenClass.cast(token) : null);
    }

    // --- tokens ---

    private static final class HashToken extends Lexer.Token {
        public HashToken() {
            super("#");
        }
    }
}
