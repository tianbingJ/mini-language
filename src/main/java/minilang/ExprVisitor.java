package minilang;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
public interface ExprVisitor {
    Object visitAssignExpr(AssignExpr expr);
    Object visitBinaryExpr(BinaryExpr expr);
    Object visitCallExpr(CallExpr expr);
    Object visitGroupingExpr(GroupExpr expr);
    Object visitLiteralExpr(LiteralExpr expr);
    Object visitLogicalExpr(LogicalExpr expr);
    Object visitUnaryExpr(UnaryExpr expr);
    Object visitVariableExpr(VariableExpr expr);
}
