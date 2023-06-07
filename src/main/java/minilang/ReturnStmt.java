package minilang;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */

@Data
public class ReturnStmt implements Stmt {

    private Token keyword;

    private Expr value;

    public ReturnStmt(Token keyword, Expr value) {
        this.keyword = keyword;
        this.value = value;
    }
    @Override
    public Object accept(StmtVisitor visitor) {
        return visitor.visitReturnStmt(this);
    }
}
