package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class PrintStmt implements Stmt {
    PrintStmt(Expr expression) {
        this.expression = expression;
    }

    @Override
    public Object accept(StmtVisitor visitor) {
        return visitor.visitPrintStmt(this);
    }

    final Expr expression;

}
