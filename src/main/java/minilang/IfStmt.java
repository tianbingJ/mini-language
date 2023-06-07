package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class IfStmt implements Stmt{

    private Expr expr;

    private Stmt thenBranch;

    private Stmt elseBranch;

    IfStmt(Expr expression, Stmt thenBranch, Stmt elseBranch) {
        this.expr= expression;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public Object accept(StmtVisitor visitor) {
        return visitor.visitIfStmt(this);
    }
}
