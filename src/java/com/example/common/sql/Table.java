package com.example.common.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.common.Constants;

/**
 * Table
 * @author Kazuhiko Arase
 */
public abstract class Table<T extends TableRow> {

	private final Class<T> clazz;
	
	/**
	 * コンストラクタ
	 * @param clazz
	 */
	protected Table(final Class<T> clazz) {
		this.clazz = clazz;
	}

	/**
	 * 表メタデータを取得する。
	 * @return 表メタデータ
	 */
	protected abstract TableMetaData getTableMetaData();

	/**
	 * 列メタデータを取得する。
	 * @return 列メタデータ
	 */
	protected abstract ColumnMetaData[] getColumnMetaData();

	/**
	 * スキーマ名(データソース名)を考慮してデータベース接続を取得する。
	 * @return
	 * @throws SQLException
	 */
	private SqlConnection getConnection() throws SQLException {
		if (Constants.DATASOURCE_NAME_SECOND.equals(getTableMetaData().getSchemaName() ) ) {
			return Sql.currentConnection(Transaction.SECOND);
		} else {
			return Sql.currentConnection();
		}
	}
	
	public List<T> select(final SqlString whereClause) throws SQLException {
		return getConnection().selectAll(
			new SqlConnection.ResultRecordFactory<T>() {
				@Override
				public T create() {
					return newRow();
				}
			},
			getColumnNames(),
			buildSelectSql(whereClause),
			clazz
		);
	}

	public int selectCount(final SqlString whereClause) throws SQLException {
		Integer count = getConnection().selectFirstColumnOne(
			buildSelectCountSql(whereClause)
		);
		return (count != null)? count.intValue() : 0;
	}
	
	public boolean selectRow(final T row) throws SQLException {
		return getConnection().selectOne(
				new SqlConnection.ResultRecordFactory<T>() {
					@Override
					public T create() {
						return row;
					}
				},
				getColumnNames(),
				buildSelectRowSql(row),
				clazz
			) != null;
	}
	
	public void insertRow(final T row) throws SQLException {
		final int updateCount = getConnection().
			update(buildInsertRowSql(row) );
		if (updateCount != 1) {
			throw new RowAccessException("fail to insert");
		}
	}
	
	public void updateRow(final T row) throws SQLException {
		final int updateCount = getConnection().
			update(buildUpdateRowSql(row) );
		if (updateCount != 1) {
			throw new RowAccessException("fail to update");
		}
	}
	
	public void deleteRow(final T row) throws SQLException {
		final int updateCount = getConnection().
			update(buildDeleteRowSql(row) );
		if (updateCount != 1) {
			throw new RowAccessException("fail to delete");
		}
	}

	public T newRow() {
		return TableRowFactory.create(clazz);
	}

	public final SqlString buildSelectSql(final SqlString whereClause) {
		final StringBuilder sql = new StringBuilder();
		sql.append("select ");
		boolean first = true;
		for (ColumnMetaData columnMeta : getColumnMetaData() ) {
			if (!first) {
				sql.append(",");
			}
			sql.append(columnMeta.getColumnName() );
			first = false;
		}
		sql.append(" from ");
		sql.append(getTableMetaData().getTableName() );
		sql.append(" ");
		sql.append(whereClause);
		return new SqlString(sql.toString() );
	}

	public final SqlString buildSelectCountSql(final SqlString whereClause) {
		final StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from ");
		sql.append(getTableMetaData().getTableName() );
		sql.append(" ");
		sql.append(whereClause);
		return new SqlString(sql.toString() );
	}
	
	public SqlString buildSelectRowSql(final T row) {
		return buildSelectSql(buildWhereByPrimaryKey(row));
	}

	public SqlString buildInsertRowSql(final T row) {
		final StringBuilder sql = new StringBuilder();
		sql.append("insert into ");
		sql.append(getTableMetaData().getTableName() );
		sql.append(" (");
		{
			boolean first = true;
			for (ColumnMetaData columnMeta : getColumnMetaData()) {
				if (!row.isSet(columnMeta.getPropName() ) ) {
					continue;
				}
				if (!first) {
					sql.append(",");
				}
				sql.append(columnMeta.getColumnName() );
				first = false;
			}
		}
		sql.append(") values (");
		{
			boolean first = true;
			for (ColumnMetaData columnMeta : getColumnMetaData()) {
				if (!row.isSet(columnMeta.getPropName() ) ) {
					continue;
				}
				if (!first) {
					sql.append(",");
				}
				sql.append(toConstant(columnMeta, row) );
				first = false;
			}
		}
		sql.append(")");
		return new SqlString(sql.toString() );
	}

	public SqlString buildUpdateRowSql(final T row) {
		final StringBuilder sql = new StringBuilder();
		sql.append("update ");
		sql.append(getTableMetaData().getTableName() );
		sql.append(" set ");
		boolean first = true;
		for (ColumnMetaData columnMeta : getColumnMetaData()) {
			if (!row.isSet(columnMeta.getPropName() ) ) {
				continue;
			}
			if (columnMeta.isPrimaryKey() ) {
				continue;
			}
			if (!first) {
				sql.append(",");
			}
			sql.append(columnMeta.getColumnName() );
			sql.append("=");
			sql.append(toConstant(columnMeta, row) );
			first = false;
		}
		sql.append(" ");
		sql.append(buildWhereByPrimaryKey(row) );
		return new SqlString(sql.toString() );
	}

	public SqlString buildDeleteRowSql(final T row) {
		final StringBuilder sql = new StringBuilder();
		sql.append("delete from ");
		sql.append(getTableMetaData().getTableName() );
		sql.append(" ");
		sql.append(buildWhereByPrimaryKey(row) );
		return new SqlString(sql.toString() );
	}

	private SqlString buildWhereByPrimaryKey(final T row) {

		final StringBuilder sql = new StringBuilder();
		sql.append("where ");
		
		boolean first = true;
		for (ColumnMetaData columnMeta : getColumnMetaData()) {
			if (!columnMeta.isPrimaryKey() ) {
				continue;
			}
			if (!first) {
				sql.append(" and ");
			}
			sql.append(columnMeta.getColumnName() );
			sql.append("=");
			sql.append(toConstant(columnMeta, row) );
			first = false;
		}

		return new SqlString(sql.toString() );
	}
	
	private String[] getColumnNames() {
		final List<String> columnNames = new ArrayList<String>();
		for (ColumnMetaData columnMeta : getColumnMetaData() ) {
			columnNames.add(columnMeta.getColumnName() );
		}
		return columnNames.toArray(new String[columnNames.size()]);
	}

	private String toConstant(ColumnMetaData columnMeta, T row) {
		return SqlType.get(columnMeta.getJavaType(), columnMeta.getPropName() ).
			toConstant(row.getValue(columnMeta.getPropName() ), null);
	}

	public static class TableMetaData {
		private final String schemaName;
		private final String tableName;
		public TableMetaData(String schemaName, String tableName) {
			this.schemaName = schemaName;
			this.tableName = tableName;
		}
		public String getSchemaName() {
			return schemaName;
		}
		public String getTableName() {
			return tableName;
		}
	}
	
	public static class ColumnMetaData {
		private final String columnName;
		private final String propName;
		private final Class<?> javaType;
		private final boolean primaryKey;
		public ColumnMetaData(String columnName, String propName, Class<?> javaType, boolean primaryKey) {
			this.columnName = columnName;
			this.propName = propName;
			this.javaType = javaType;
			this.primaryKey = primaryKey;
		}
		public String getColumnName() {
			return columnName;
		}
		public String getPropName() {
			return propName;
		}
		public Class<?> getJavaType() {
			return javaType;
		}
		public boolean isPrimaryKey() {
			return primaryKey;
		}
	}
}