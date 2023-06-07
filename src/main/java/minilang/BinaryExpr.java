package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class BinaryExpr implements Expr {

    private Expr left, right;
    private Token operator;

    BinaryExpr(Expr left, Token operator, Expr right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }


    @Override
    public Object accept(ExprVisitor visitor) {
        return visitor.visitBinaryExpr(this);
    }
}
