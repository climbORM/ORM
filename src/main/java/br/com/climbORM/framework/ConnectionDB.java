package br.com.climbORM.framework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import br.com.climbORM.framework.interfaces.*;
import br.com.climbORM.framework.utils.SqlUtil;

import br.com.climbORM.framework.mapping.Entity;
import br.com.climbORM.framework.utils.ReflectionUtil;

public class ConnectionDB implements ClimbConnection {
	private Connection connection;
	private Properties properties;
	private String schema;
	private Transaction transaction;
	private FieldsManager fieldsManager;

	public void createConnectionDB() {
		try {
			this.schema = this.properties.getProperty("currentSchema");
			final String url = this.properties.getProperty("url");
			final String port = this.properties.getProperty("port");
			final String dataBase = this.properties.getProperty("database");
			this.connection = DriverManager.getConnection("jdbc:postgresql://" + url + ":" + port + "/" + dataBase + "",
					this.properties);
			this.transaction = new TransactionDB(this.connection);
			this.fieldsManager = new DynamicFieldsManager(this.connection, this.schema);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isClosedConnection() {
		try {
			return this.connection.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true;
	}

	public ConnectionDB(Properties properties) {

		this.properties = properties;
		createConnectionDB();

	}

	@Override
	public Transaction getTransaction() {
		return this.transaction;
	}


	public void save(Object object) {

		try {

			PreparedStatement ps = SqlUtil.preparedStatementInsert(this.schema, this.connection, ReflectionUtil.generateModel(object),
					ReflectionUtil.getTableName(object));
			ps.executeUpdate();

			ResultSet rsID = ps.getGeneratedKeys();
			if (rsID.next()) {
				Long id = rsID.getLong("id");

				PersistentEntity pers = (PersistentEntity) object;
				pers.setId(id);
			}

			if (ReflectionUtil.isContainsDynamicFields(object)) {
				this.fieldsManager.save(object);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void update(Object object) {

		Long id = ((PersistentEntity) object).getId();

		try {
			PreparedStatement ps = SqlUtil.preparedStatementUpdate(this.schema, this.connection, ReflectionUtil.generateModel(object),
					ReflectionUtil.getTableName(object), id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void delete(Object object) {

		Long id = ((PersistentEntity) object).getId();
		String tableName = ReflectionUtil.getTableName(object);

		String sql = "DELETE FROM " + this.schema + "." + tableName + "	WHERE id = " + id.toString();

		// TODO: COLOCAR LOG AQUI
		System.out.println(sql);

		try {
			Statement stmt = this.connection.createStatement();
			stmt.execute(sql);
			((PersistentEntity) object).setId(null);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void delete(Class object, String where) {
		Entity entity = (Entity) object.getAnnotation(Entity.class);
//		System.out.println(entity.name());

		String tableName = entity.name();

		String sql = "DELETE FROM " + this.schema + "." + tableName + "	" + where;

		System.out.println(sql);

		try {
			Statement stmt = this.connection.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object findOne(Class classe, Long id) {
		return new LazyLoader(this.connection, this.schema).loadLazyObject(classe, id);
	}

	@Override
	public ResultIterator find(Class classe, String where) {
		return new LazyLoader(this.connection,this.schema, classe, where, LazyLoader.ENTITY);
	}

	@Override
	public ResultIterator findWithQuery(Class classe, String sql) {
		return new LazyLoader(this.connection,this.schema, classe, sql, LazyLoader.QUERY_RESULT);
	}


	@Override
	public void close() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




}
