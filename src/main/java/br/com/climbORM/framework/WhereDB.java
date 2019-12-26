package br.com.climbORM.framework;

import br.com.climbORM.framework.interfaces.Command;
import br.com.climbORM.framework.interfaces.Where;

public class WhereDB implements Where {
    @Override
    public Command where(String whereSql) {
        return new CommandDB(whereSql);
    }
}
