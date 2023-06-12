import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import minilang.BinaryExpr;
import minilang.Expr;
import minilang.Interpreter;
import minilang.Lexer;
import minilang.LiteralExpr;
import minilang.Parser;
import minilang.Stmt;
import minilang.Token;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
public class ParserTest {

    @Test
    public void testParseVar() {
        String source = "var x = 10;";
        Lexer lexer = new Lexer(source);
        List<Token> tokenList = lexer.scanTokens();

        Parser parser = new Parser(tokenList);
        List<Stmt> stmts = parser.parse();
        System.out.println(stmts);
    }

    @Test
    public void testPrecedence() {
        Expr expr = sourceToExpr("1 + 2 * 3");
        Assertions.assertTrue(expr instanceof BinaryExpr);
        BinaryExpr binary = (BinaryExpr) expr;
        Assertions.assertTrue(binary.getLeft()instanceof LiteralExpr);
        Assertions.assertTrue(binary.getRight()instanceof BinaryExpr);
    }

    private Expr sourceToExpr(String source) {
        Lexer scanner = new Lexer(source);
        Parser parser = new Parser(scanner.scanTokens());
        return parser.expression();
    }

    @Test
    public void testWhile() {
        String source = "var sum = 0;" +
                "var x = 1;" +
                "while (x < 10) {" +
                "sum = sum + x;" +
                "x = x + 1;" +
                "}" +
                "print sum;"
                ;
        Lexer lexer = new Lexer(source);
        List<Token> tokenList = lexer.scanTokens();

        Parser parser = new Parser(tokenList);
        List<Stmt> stmts = parser.parse();

        Interpreter interpreter = new Interpreter();
        interpreter.interpret(stmts);
    }

    @Test
    public void  testScope() {

//        var x = 1;
//        var execute = x < 1000;
//        var sum = 0;
//        while (execute) {
//            sum = sum + x;
//            x = x + 1;
//            execute = x < 1000;
//        }
//
//        print sum;

        String source ="        var x = 1;\n" +
                "        var execute = x < 1000;\n" +
                "        var sum = 0;\n" +
                "        while (execute) {\n" +
                "            sum = sum + x;\n" +
                "            x = x + 1;\n" +
                "            execute = x < 1000;\n" +
                "        }\n" +
                "\n" +
                "        print sum;"
                ;
        Lexer lexer = new Lexer(source);
        List<Token> tokenList = lexer.scanTokens();

        Parser parser = new Parser(tokenList);
        List<Stmt> stmts = parser.parse();

        Interpreter interpreter = new Interpreter();
        interpreter.interpret(stmts);

    }
}
