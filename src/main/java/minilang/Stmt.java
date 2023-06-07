package minilang;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-07
 */
public interface Stmt {
    Object accept(StmtVisitor visitor);
}
