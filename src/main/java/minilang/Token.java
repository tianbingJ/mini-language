package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2022-10-16
 */
@Data
public class Token {

    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;
    final int column;

    Token(TokenType type, String lexeme, Object literal, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = column;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
