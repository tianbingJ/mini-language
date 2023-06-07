package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class VarStmt implements Stmt {

    private Token name;

    private Expr expr;

    public VarStmt(Token name, Expr initExpr) {
        this.name = name;
        this.expr = initExpr;
    }


    @Override
    public Object accept(StmtVisitor visitor) {
        return visitor.visitVarStmt(this);
    }
}
