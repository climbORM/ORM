package br.com.climbORM.framework;

import br.com.climbORM.framework.interfaces.Transaction;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionDB implements Transaction {

    private Connection connection;

    public TransactionDB(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void start() {
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commit() {
        try {
            this.connection.commit();
            this.connection.setAutoCommit(true);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void rollback() {
        try {
            this.connection.rollback();
            this.connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
