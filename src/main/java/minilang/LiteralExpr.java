package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class LiteralExpr implements Expr {

    private Object value;

    public LiteralExpr(Object value) {
        this.value = value;
    }

    @Override
    public Object accept(ExprVisitor visitor) {
        return visitor.visitLiteralExpr(this);
    }
}
