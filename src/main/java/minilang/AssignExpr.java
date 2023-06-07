package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class AssignExpr implements Expr {
    private Token token;

    private Expr value;

    AssignExpr(Token name, Expr value) {
        this.token = name;
        this.value = value;
    }

    @Override
    public Object accept(ExprVisitor visitor) {
        return visitor.visitAssignExpr(this);
    }
}
