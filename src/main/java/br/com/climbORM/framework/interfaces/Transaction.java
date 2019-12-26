package br.com.climbORM.framework.interfaces;

public interface Transaction {
    public void start();
    public void commit();
    public void rollback();
}
