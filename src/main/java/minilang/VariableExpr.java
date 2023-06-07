package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class VariableExpr implements Expr {

    Token name;

    VariableExpr(Token name) {
        this.name = name;
    }


    @Override
    public Object accept(ExprVisitor visitor) {
        return visitor.visitVariableExpr(this);
    }
}
