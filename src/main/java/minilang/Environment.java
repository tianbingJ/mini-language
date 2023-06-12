package minilang;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tianbing <tianbing@kuaishou.com>
 * Created on 2023-06-11
 */
public class Environment {

    private final Map<String, Object> values = new HashMap<>();

    private Environment parent;

    public Environment() {
        parent = null;
    }

    public Environment(Environment env) {
        this.parent= env;
    }

    public void define(String name, Object value) {
        if (values.containsKey(name)) {
            throw new RuntimeException("name already defined:" + name);
        }
        values.put(name, value);
    }

    public void define(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            throw new RuntimeException("name already defined:" + name);
        }
        values.put(name.lexeme, value);
    }

    public void assign(Token name, Object value) {
        Environment env = this;
        while (env != null) {
            if (env.values.containsKey(name.lexeme)) {
                env.values.put(name.lexeme, value);
                break;
            }
            env = env.parent;
        }
        if (env == null) {
            throw new RuntimeException(name + " undefined");
        }
    }


    public Object get(Token name) {
        //这里需要区分name存在值是null和name不存在的情况，所以需要先contains
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
        if (parent != null) {
            return parent.get(name);
        }
        throw new RuntimeException("cannot find token " + name);
    }

}
