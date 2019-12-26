package br.com.climbORM.framework;

import br.com.climbORM.framework.interfaces.Command;

import java.util.List;

public class CommandDB implements Command {

    private String whereSql;

    public CommandDB(String whereSql) {
        this.whereSql = whereSql;
    }

    @Override
    public List<Object> getList() {
        return null;
    }
}
