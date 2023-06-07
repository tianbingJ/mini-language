package minilang;

import java.util.List;

import lombok.Data;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
@Data
class FunctionStmt implements Stmt {
    public FunctionStmt(Token name, List<Token> params, List<Stmt> body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public Object accept(StmtVisitor visitor) {
        return visitor.visitFunctionStmt(this);
    }

    final Token name;
    final List<Token> params;
    final List<Stmt> body;
}


