package minilang;

import java.util.List;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
public class Interpreter implements ExprVisitor, StmtVisitor {

    Environment globals = new Environment();
    private Environment environment = globals;

    public void interpret(List<Stmt> stmts) {
        for (Stmt stmt : stmts) {
            stmt.accept(this);
        }
    }

    @Override
    public Object visitAssignExpr(AssignExpr expr) {
        Object value = expr.getValue() == null ? null : evaluate(expr.getValue());
        this.environment.assign(expr.getToken(), value);
        return value;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr expr) {
        Object left = evaluate(expr.getLeft());
        Object right = evaluate(expr.getRight());
        switch (expr.getOperator().type) {
            case MINUS:
                checkNumberOperand(expr.getOperator(), left, right);
                return (double) left - (double) right;
            case SLASH:
                checkNumberOperand(expr.getOperator(), left, right);
                return (double) left / (double) right;
            case STAR:
                checkNumberOperand(expr.getOperator(), left, right);
                return (double) left * (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeException(expr.getOperator() + " operands must be two numbers or two strings");
            case GREATER:
                checkNumberOperand(expr.getOperator(), left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperand(expr.getOperator(), left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperand(expr.getOperator(), left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperand(expr.getOperator(), left, right);
                return (double) left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            default:
                return null;
        }
    }

    @Override
    public Object visitCallExpr(CallExpr expr) {
        return null;
    }

    @Override
    public Object visitGroupingExpr(GroupExpr expr) {
        return evaluate(expr);
    }

    @Override
    public Object visitLiteralExpr(LiteralExpr expr) {
        return expr.getValue();
    }

    @Override
    public Object visitLogicalExpr(LogicalExpr expr) {
        Object left = evaluate(expr.getLeft());
        if (expr.getOperator().type == TokenType.OR) {
            if (isTruthy(left)) {
                return left;
            }
        } else {
            if (!isTruthy(left)) {
                return left;
            }
        }
        return evaluate(expr.getRight());
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr expr) {
        //不支持
        return null;
    }

    @Override
    public Object visitVariableExpr(VariableExpr expr) {
        return environment.get(expr.getName());
    }

    @Override
    public Object visitIfStmt(IfStmt stmt) {
        Object value = evaluate(stmt.getExpr());
        if (isTruthy(value)) {
            execute(stmt.getThenBranch());
        } else if (stmt.getElseBranch() != null) {
            execute(stmt.getElseBranch());
        }
        return null;
    }

    @Override
    public Object visitBlockStmt(BlockStmt stmt) {
        List<Stmt> statements = stmt.getStmts();
        executeBlock(statements, new Environment(this.environment));
        return null;
    }

    @Override
    public Object visitExprStmt(ExprStmt stmt) {
        evaluate(stmt.getExpr());
        return null;
    }

    @Override
    public Object visitFunctionStmt(FunctionStmt stmt) {
        //不支持
        return null;
    }

    @Override
    public Object visitPrintStmt(PrintStmt stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringfy(value));
        return null;
    }

    @Override
    public Object visitVarStmt(VarStmt stmt) {
        Object value = null;
        if (stmt.getExpr() != null) {
            value = evaluate(stmt.getExpr());
        }
        environment.define(stmt.getName(), value);
        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt stmt) {
        Object value = evaluate(stmt.getCondition());
        while (isTruthy(value)) {
            stmt.getBody().accept(this);
            value = evaluate(stmt.getCondition());
        }
        return null;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt stmt) {
        //不支持
        return null;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return (boolean) object;
        }
        return true;
    }

    private Object lookUpVariable(Token name) {
        return environment.get(name);
    }

    private void checkNumberOperand(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        throw new RuntimeException(operator + " Operator must be a number!");
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }
        return a.equals(b);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> stmts, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt stmt : stmts) {
                execute(stmt);
            }
        } finally {
            this.environment = previous;
        }
    }

    private String stringfy(Object object) {
        if (object == null) {
            return "nil";
        }
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }
}
