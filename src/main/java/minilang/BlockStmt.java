package minilang;

import java.util.List;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
public class BlockStmt implements Stmt {

    private List<Stmt> stmts;

    BlockStmt(List<Stmt> stmts) {
        this.stmts = stmts;
    }

    @Override
    public Object accept(StmtVisitor visitor) {
        return visitor.visitBlockStmt(this);
    }
}
