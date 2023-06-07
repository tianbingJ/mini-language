package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class GroupExpr implements Expr {

    private Expr expr;

    GroupExpr(Expr expr) {
        this.expr = expr;
    }

    @Override
    public Object accept(ExprVisitor visitor) {
        return visitor.visitGroupingExpr(this);
    }
}
