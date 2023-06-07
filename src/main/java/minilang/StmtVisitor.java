package minilang;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
public interface StmtVisitor {

    Object visitIfStmt(IfStmt stmt);

    Object visitBlockStmt(BlockStmt stmt);

    Object visitExprStmt(ExprStmt stmt);

    Object visitFunctionStmt(FunctionStmt stmt);

    Object visitPrintStmt(PrintStmt stmt);

    Object visitVarStmt(VarStmt stmt);

    Object visitWhileStmt(WhileStmt stmt);

    Object visitReturnStmt(ReturnStmt stmt);
}
