package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class LogicalExpr implements Expr {
    private Expr left, right;

    private Token operator;

    LogicalExpr(Expr left, Token operator, Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object accept(ExprVisitor visitor) {
        return visitor.visitLogicalExpr(this);
    }
}
