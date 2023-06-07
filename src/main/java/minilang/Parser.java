package minilang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
public class Parser {

    private static class ParseError extends RuntimeException {

        ParseError(String msg) {
            super(msg);
        }
    }

    private final List<Token> tokens;

    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }


    public Expr expression() {
        return assignment();
    }

    /**
     * assign是右结合操作符
     * a = (b + c )= 1,这种情况的合法性是通过判断 lvalue是否是Expr.Variable
     * 后面还会有更复杂的复制, object.field = 1
     *
     * @return
     */
    Expr assignment() {
        Expr expr = or();
        //assignment
        if (match(TokenType.EQUAL)) {
            Token equals = previous();
            Expr value = assignment();
            if (expr instanceof VariableExpr) {
                Token name = ((VariableExpr) expr).name;
                return new AssignExpr(name, value);
            }
            error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    Expr or() {
        Expr expr = and();
        while (match(TokenType.OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new LogicalExpr(expr, operator, right);
        }
        return expr;
    }

    Expr and() {
        Expr expr = equality();
        while (match(TokenType.AND)) {
            Token operator = previous();
            Expr right = and();
            expr = new LogicalExpr(expr, operator, right);
        }
        return expr;
    }

    private Stmt declaration() {
        try {
            if (match(TokenType.VAR)) {
                return varDeclaration();
            }
            if (match(TokenType.FUN)) {
                return function("function");
            }

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private FunctionStmt function(String kind) {
        Token name = consume(TokenType.IDENTIFIER, "Expect " + kind + " name.");
        consume(TokenType.LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Cann't have more than 255 parameters.");
                }
                parameters.add(consume(TokenType.IDENTIFIER, "Execct parameter name."));
            }
            while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
        consume(TokenType.LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new FunctionStmt(name, parameters, body);
    }

    private Stmt varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name");
        Expr initializer = null;
        if (match(TokenType.EQUAL)) {
            initializer = expression();
        }
        consume(TokenType.SEMISOLON, "Expect ';'");
        return new VarStmt(name, initializer);
    }


    private Stmt statement() {
        if (match(TokenType.PRINT)) {
            return printStatement();
        }
        if (match(TokenType.LEFT_BRACE)) {
            return new BlockStmt(block());
        }
        if (match(TokenType.IF)) {
            return ifStatement();
        }
        if (match(TokenType.WHILE)) {
            return whileStmtment();
        }
        if (match(TokenType.FOR)) {
            return forStatement();
        }
        if (match(TokenType.RETURN)) {
            return returnStmt();
        }
        return expressionStatement();
    }

    private Stmt returnStmt() {
        Token keyword = previous();
        Expr value = null;
        if (!check(TokenType.SEMISOLON)) {
            value = expression();
        }
        consume(TokenType.SEMISOLON, "Expect ';' after return value.");
        return new ReturnStmt(keyword, value);
    }

    private Stmt forStatement() {
        consume(TokenType.LEFT_PAREN, "expect '(' after 'for'.");
        Stmt initializer;
        if (match(TokenType.SEMISOLON)) {
            initializer = null;
        } else if (match(TokenType.VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!check(TokenType.SEMISOLON)) {
            condition = expression();
        }
        consume(TokenType.SEMISOLON, "expect ';' after loop condition");

        Expr increment = null;
        if (!check(TokenType.RIGHT_PAREN)) {
            increment = expression();
        }
        consume(TokenType.RIGHT_PAREN, "expect ')' after for clauses.");

        Stmt body = statement();

        if (increment != null) {
            body = new BlockStmt(Arrays.asList(body, new ExprStmt(increment)));
        }
        if (condition == null) {
            condition = new LiteralExpr(true);
        }

        body = new WhileStmt(condition, body);
        if (initializer != null) {
            body = new BlockStmt(Arrays.asList(initializer, body));
        }
        return body;
    }

    private Stmt whileStmtment() {
        consume(TokenType.LEFT_PAREN, "expect '(' after while");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "expect ')' after while condition");
        Stmt stmt = statement();
        return new WhileStmt(condition, stmt);
    }

    private Stmt ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after if");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }
        return new IfStmt(condition, thenBranch, elseBranch);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(TokenType.SEMISOLON, "Expect ';' after value.");
        return new PrintStmt(value);
    }

    private Stmt expressionStatement() {
        Expr value = expression();
        consume(TokenType.SEMISOLON, "Expect ';' after value.");
        return new ExprStmt(value);
    }


    private Expr equality() {
        Expr expr = comparison();
        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new BinaryExpr(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = term();
        while (match(TokenType.GREATER, TokenType.LESS, TokenType.GREATER_EQUAL, TokenType.LESS_EQUAL)) {
            Token opertor = previous();
            Expr right = term();
            expr = new BinaryExpr(expr, opertor, right);
        }
        return expr;
    }

    /**
     * 1.怎么处理优先级
     * 这里是处理加法和乘法的优先级问题，左右子树表达式求值都是用的factor()，优先对优先级更高的乘法进行求值;
     * 1 + 2 * 3        -> (+ 1 ( * 2 3))
     * 1 * 2 + 3        -> (+ (* 1 2) 3)
     * 1 * 2 + 3 * 4    -> (+ (* 1 * 2) (* 3 4))
     * <p>
     * 2.怎么处理同类操作符的结合性(左结合)
     * 这里有一个while循环， 考虑: 1 * 2 + 3 * 4 + 5 * 6, 会在这里循环2次
     * (1) expr = (1 * 2)
     * (2) right = (3 * 4), -> expr = ( + (* 1  2) (* 3 4) )
     * (3) 遇到 +, right = (* 5 6), (2)中的expr倍塞入左子树中，在语法树中层次更深，优先级更高，形成左结合性。
     *
     * @return 3.
     */
    private Expr term() {
        Expr expr = factor();
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token opertor = previous();
            Expr right = factor();
            //相同优先级情况下，当前表达式，不断塞入左子树,左倾斜二叉树
            expr = new BinaryExpr(expr, opertor, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();
        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token opertor = previous();
            Expr right = unary();
            expr = new BinaryExpr(expr, opertor, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new UnaryExpr(operator, right);
        }
        return call();
    }

    private Expr call() {
        Expr expr = primary();
        //处理f(1)(2),f(1)的结果作为callee，放在语法树的下层
        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }
        return expr;
    }

    /**
     * 以callee作为被调，添加参数列表等信息; 返回Expr.Call表达式
     *
     * @param callee
     * @return
     */
    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    error(peek(), "cannot have more than 255 arguments.");
                }
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }

        Token paren = consume(TokenType.RIGHT_PAREN, "expect ')' after arguments");
        return new CallExpr(callee, paren, arguments);
    }

    private Expr primary() {
        if (match(TokenType.FALSE)) {
            return new LiteralExpr(false);
        }
        if (match(TokenType.TRUE)) {
            return new LiteralExpr(true);
        }
        if (match(TokenType.NIL)) {
            return new LiteralExpr(null);
        }
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new LiteralExpr(previous().literal);
        }
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "expect ')' after expression.");
            return new GroupExpr(expr);
        }
        if (match(TokenType.IDENTIFIER)) {
            return new VariableExpr(previous());
        }
        throw error(peek(), "Expect expression.");
    }

    private Token consume(TokenType tokenType, String msg) {
        if (check(tokenType)) {
            return advance();
        }
        throw error(peek(), msg);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private ParseError error(Token token, String message) {
        return new ParseError(message + "around " + token.lexeme +  " at line " + token.line + " column:" + token.column);
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMISOLON) {
                return;
            }
            switch (peek().type) {
                case CLASS:
                case FOR:
                case FUN:
                case IF:
                case PRINT:
                case RETURN:
                case VAR:
                case WHILE:
                    return;
            }
            advance();
        }
    }
}