package minilang;

import static minilang.TokenType.AND;
import static minilang.TokenType.BANG;
import static minilang.TokenType.BANG_EQUAL;
import static minilang.TokenType.CLASS;
import static minilang.TokenType.COMMA;
import static minilang.TokenType.DOT;
import static minilang.TokenType.ELSE;
import static minilang.TokenType.EOF;
import static minilang.TokenType.EQUAL;
import static minilang.TokenType.EQUAL_EQUAL;
import static minilang.TokenType.FALSE;
import static minilang.TokenType.FOR;
import static minilang.TokenType.FUN;
import static minilang.TokenType.GREATER;
import static minilang.TokenType.GREATER_EQUAL;
import static minilang.TokenType.IDENTIFIER;
import static minilang.TokenType.IF;
import static minilang.TokenType.LEFT_BRACE;
import static minilang.TokenType.LEFT_PAREN;
import static minilang.TokenType.LESS;
import static minilang.TokenType.LESS_EQUAL;
import static minilang.TokenType.MINUS;
import static minilang.TokenType.NIL;
import static minilang.TokenType.NUMBER;
import static minilang.TokenType.OR;
import static minilang.TokenType.PLUS;
import static minilang.TokenType.PRINT;
import static minilang.TokenType.RETURN;
import static minilang.TokenType.RIGHT_BRACE;
import static minilang.TokenType.RIGHT_PAREN;
import static minilang.TokenType.SEMISOLON;
import static minilang.TokenType.SLASH;
import static minilang.TokenType.STAR;
import static minilang.TokenType.STRING;
import static minilang.TokenType.TRUE;
import static minilang.TokenType.VAR;
import static minilang.TokenType.WHILE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
public class Lexer {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("or", OR);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private int column = 0;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line, column));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMISOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    throw new RuntimeException("Unexpected character." + c + " at " + line);
                }
                break;
        }
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char advance() {
        char ch = source.charAt(current++);
        if (ch == '\n') {
            column = 0;
        } else {
            column ++;
        }
        return ch;
    }

    private void addToken(TokenType tokenType) {
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(tokenType, text, literal, line, column));
    }

    boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }
        advance();
        return true;
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
            new RuntimeException("Unterminated string. at " + line);
            return;
        }
        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
        }
        while (isDigit(peek())) {
            advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_';
    }

    private boolean isAlphaNumber(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void identifier() {
        while (isAlphaNumber(peek())) {
            advance();
        }

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type != null) {
            addToken(type);
        } else {
            addToken(IDENTIFIER);
        }
    }
}
