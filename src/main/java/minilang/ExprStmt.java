package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class ExprStmt implements Stmt {

    private Expr expr;

    ExprStmt(Expr expr) {
        this.expr = expr;
    }

    @Override
    public Object accept(StmtVisitor visitor) {
        return visitor.visitExprStmt(this);
    }
}
