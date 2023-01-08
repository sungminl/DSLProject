package libs;

import org.antlr.runtime.MismatchedTokenException;
import org.antlr.v4.runtime.*;

/**
 * Helper class for processing token streams produced by ANTLR
 */
public class Tokens {
    private final TokenStream tokenStream;

    /**
     * Initialize a token stream for a given lexer
     *
     * @param lexer Lexer used for tokenization
     */
    public Tokens(Lexer lexer) {
        tokenStream = new CommonTokenStream(lexer);
    }

    /**
     * Returns the next token.
     *
     * @return Next token in the token stream
     */
    public Token get() {
        return tokenStream.LT(1);
    }

    /**
     * Returns the next token and removes it from the token stream
     *
     * @return Next token in the token stream
     */
    public Token getNext() {
        Token next = tokenStream.LT(1);
        tokenStream.consume();
        return next;
    }

    /**
     * Checks whether the next token has a specific type
     *
     * @return True if the token matches the checked type, false if not
     */
    public boolean check(int tokenType) {
        Token next = tokenStream.LT(1);
        return next.getType() == tokenType;
    }

    /**
     * Checks whether the next token has a specific type and removes it from the token stream
     *
     * @return True if the token matches the checked type, false if not
     */
    public boolean checkNext(int tokenType) {
        Token next = tokenStream.LT(1);
        if (next.getType() == tokenType) {
            tokenStream.consume();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Throws an exception if the next token does not have a specific type
     */
    public void expect(int tokenType) throws MismatchedTokenException {
        Token next = tokenStream.LT(1);
        if (next.getType() != tokenType) {
            throw new MismatchedTokenException();
        }
    }

    /**
     * Throws an exception if the next token does not have a specific type, and otherwise removes it from the token stream
     */
    public void expectNext(int tokenType) throws MismatchedTokenException {
        Token next = tokenStream.LT(1);
        if (next.getType() == tokenType) {
            tokenStream.consume();
        }
        else {
            throw new MismatchedTokenException();
        }
    }
}
