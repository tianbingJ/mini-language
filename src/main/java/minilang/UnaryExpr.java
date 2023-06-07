package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class UnaryExpr implements Expr{

    private Token operator;
    private Expr right;

    UnaryExpr(Token operator, Expr right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object accept(ExprVisitor visitor) {
        return visitor.visitUnaryExpr(this);
    }
}
