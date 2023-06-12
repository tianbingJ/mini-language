package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class WhileStmt implements Stmt {

    private Expr condition;

    private Stmt body;

    WhileStmt(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Object accept(StmtVisitor visitor) {
        return visitor.visitWhileStmt(this);
    }
}
