package br.com.climbORM.api;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import br.com.climbORM.api.utils.SqlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.climbORM.api.interfaces.ClimbConnection;
import br.com.climbORM.api.mapping.Entity;
import br.com.climbORM.api.mapping.ID;
import br.com.climbORM.api.mapping.Json;
import br.com.climbORM.api.mapping.Relation;
import br.com.climbORM.api.mapping.Transient;
import br.com.climbORM.api.utils.Model;
import br.com.climbORM.api.utils.ReflectionUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ConnectionDB implements ClimbConnection {
	private Connection connection;
	private Properties properties;
	private String schema;

	public void createConnectionDB() {
		try {
			this.schema = this.properties.getProperty("currentSchema");
			final String url = this.properties.getProperty("url");
			final String port = this.properties.getProperty("port");
			final String dataBase = this.properties.getProperty("database");
			this.connection = DriverManager.getConnection("jdbc:postgresql://" + url + ":" + port + "/" + dataBase + "",
					this.properties);
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

	private String getTableName(Object object) {

		Entity entity = null;

		if (ReflectionUtil.isProxedCGLIB(object)) {
			entity = (Entity) object.getClass().getSuperclass().getAnnotation(Entity.class);
		} else {
			entity = (Entity) object.getClass().getAnnotation(Entity.class);
		}

		String tableName = "";

		if (entity != null && entity.name().trim().length() > 0) {
			tableName = entity.name().trim();
		} else {
			throw new Error("ERROR: " + object.getClass().getName() + " Unnamed entity ");
		}

		return tableName;
	}

	private List<Model> generateModel(Object object) {

		List<Model> models = new ArrayList<Model>();
		Field[] fields = null;

		if (ReflectionUtil.isProxedCGLIB(object)) {
			fields = object.getClass().getSuperclass().getDeclaredFields();
		} else {
			fields = object.getClass().getDeclaredFields();
		}


		for (Field field : fields) {

			if (field.isAnnotationPresent(Transient.class)) {
				continue;
			}

			if (field.isAnnotationPresent(Relation.class)) {
				String fieldName = ReflectionUtil.getFieldName(field);
				Object tempValue = ReflectionUtil.getValueField(field, object);
				models.add(new Model(fieldName, tempValue, Long.class, field));

				continue;
			}

			if (field.getType() == Long.class) {
				try {

					ID id = field.getAnnotation(ID.class);

					if (id != null && id.sequencial()) {
						continue;
					}

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, Long.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == Integer.class) {
				try {

					ID id = field.getAnnotation(ID.class);

					if (id != null && id.sequencial()) {
						continue;
					}

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, Integer.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == Float.class || field.getType() == float.class) {
				try {

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, Float.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == Double.class || field.getType() == double.class) {
				try {

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, Double.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
				try {

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, Boolean.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == String.class || field.getType() == char.class) {
				try {

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, String.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == byte[].class) {
				try {
					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, byte[].class, field));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == List.class && field.isAnnotationPresent(Json.class)) {
				try {
					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, List.class, field));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return models;
	}

	public void save(Object object) {

		try {

			PreparedStatement ps = SqlUtil.preparedStatementInsert(this.schema, this.connection, generateModel(object),
					getTableName(object));
			ps.executeUpdate();

			ResultSet rsID = ps.getGeneratedKeys();
			rsID.next();
			Long id = rsID.getLong("id");

			PersistentEntity pers = (PersistentEntity) object;
			pers.setId(id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void update(Object object) {

		Long id = ((PersistentEntity) object).getId();

		try {
			PreparedStatement ps = SqlUtil.preparedStatementUpdate(this.schema, this.connection, generateModel(object),
					getTableName(object), id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void delete(Object object) {
		Long id = ((PersistentEntity) object).getId();
		String tableName = getTableName(object);

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

	public Object findOne(Class classe, Long id) {

		return loadLazyObject(classe, id);

	}

	private Object loadLazyObject(Class classe, Long id) {

		final Entity entity = (Entity) classe.getAnnotation(Entity.class);

		if (entity == null) {
			throw new Error(classe.getName() + " not is Entity");
		}

		List<Model> models = null;
		try {
			models = generateModel(classe.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuilder atributes = new StringBuilder();
		List<String> atributesByte = new ArrayList<>();
		for (Model model : models) {

			if (model.getType() == byte[].class) {
				atributesByte.add(model.getAtribute());
				continue;
			}

			atributes.append(model.getAtribute() + ",");
		}

		String sql = "SELECT " + atributes.toString().substring(0, atributes.toString().length() - 1) + " FROM "
				+ entity.name() + " WHERE ID = " + id.toString();
		System.out.println(sql);

		Object object = null;

		try {

			Statement stmt = this.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 1);
			ResultSet resultSet = stmt.executeQuery(sql);

			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(classe);
			enhancer.setCallback(new MethodInterceptor() {

				public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

					Object inst = proxy.invokeSuper(obj, args);

					if (inst != null) {

						if (inst.getClass().getAnnotation(Entity.class) != null) {
							Long id = ((PersistentEntity) inst).getId();
							return loadLazyObject(inst.getClass(), id);
						}

						if (inst.getClass() == byte[].class) {
							byte[] b = (byte[]) inst;
							String fieldName = new String(b);
							return SqlUtil.getBinaryValue(id, fieldName, entity.name(), connection);
						}

					}

					return proxy.invokeSuper(obj, args);
				}
			});

			object = enhancer.create();

			Field[] fields = object.getClass().getSuperclass().getDeclaredFields();

			while (resultSet.next()) {

				((PersistentEntity) object).setId(id);

				for (Field field : fields) {

					if (field.isAnnotationPresent(Transient.class)) {
						continue;
					}

					if (field.isAnnotationPresent(Relation.class)) {

						try {

							Object instance = Class.forName(field.getGenericType().getTypeName()).newInstance();

							String fieldName = ReflectionUtil.getFieldName(field);
							Long value = resultSet.getLong(fieldName);

							PersistentEntity persistentEntity = (PersistentEntity) instance;
							persistentEntity.setId(value);

							Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
									.invoke(object, instance);

						} catch (Exception e) {
							e.printStackTrace();
						}

						continue;
					}

					if (field.getType() == Long.class) {
						try {

							String fieldName = ReflectionUtil.getFieldName(field);
							Long value = resultSet.getLong(fieldName);
							Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
									.invoke(object, value);

						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if (field.getType() == Integer.class || field.getType() == int.class) {
						try {

							String fieldName = ReflectionUtil.getFieldName(field);
							Integer value = resultSet.getInt(fieldName);
							Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
									.invoke(object, value);

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (field.getType() == Float.class || field.getType() == float.class) {
						try {

							String fieldName = ReflectionUtil.getFieldName(field);
							Float value = resultSet.getFloat(fieldName);
							Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
									.invoke(object, value);

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (field.getType() == Double.class || field.getType() == double.class) {

						try {

							String fieldName = ReflectionUtil.getFieldName(field);
							Double value = resultSet.getDouble(fieldName);
							Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
									.invoke(object, value);

						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
						try {

							try {

								String fieldName = ReflectionUtil.getFieldName(field);
								Boolean value = resultSet.getBoolean(fieldName);
								Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
										.invoke(object, value);

							} catch (Exception e) {
								e.printStackTrace();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (field.getType() == String.class || field.getType() == char.class) {
						try {

							try {

								String fieldName = ReflectionUtil.getFieldName(field);
								String value = resultSet.getString(fieldName);
								Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
										.invoke(object, value);

							} catch (Exception e) {
								e.printStackTrace();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (field.getType() == byte[].class) {
						try {

							try {

								String fieldName = ReflectionUtil.getFieldName(field);
								byte[] value = fieldName.getBytes();
								Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
										.invoke(object, value);

							} catch (Exception e) {
								e.printStackTrace();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (field.getType() == List.class && field.isAnnotationPresent(Json.class)) {
						try {

							try {

								ObjectMapper mapper = new ObjectMapper();

								Class jsonType = ((Class) ((ParameterizedType) field.getGenericType())
										.getActualTypeArguments()[0]);

								String fieldName = ReflectionUtil.getFieldName(field);
								String json = resultSet.getString(fieldName);

								ArrayList value = mapper.readValue(json,
										mapper.getTypeFactory().constructCollectionType(List.class, jsonType));

								Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
										.invoke(object, value);

							} catch (Exception e) {
								e.printStackTrace();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return object;
	}

	@Override
	public void transactionStart() {
		try {
			this.connection.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void transactionCommit() {
		try {
			this.connection.commit();
			this.connection.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void transactionRolback() {

		try {
			this.connection.rollback();
			this.connection.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
