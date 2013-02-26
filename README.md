最小限のDAOとWEBアプリのひな形です。

## あらかじめ既存のテーブル毎に作成したクラスによる操作

主にPKEYを指定した一行単位の処理に対応します。

```sql
create table TX0001 (
    COL1 CHAR(8) not null,
    COL2 DECIMAL(10,2),
    COL3 INTEGER,
    COL4 VARCHAR(20),
    -- プライマリキー
    primary key (COL1)
);
```

```java
// 1行登録
Tx0001.Row row = Tx0001.newRow();
row.setCol1("1");
row.setCol2(BigDecimal.ZERO);
row.setCol3(4);
row.setCol4("a");
Tx0001.insertRow(row);
```

```java
// col1 = 1 の行を検索
Tx0001.Row row = Tx0001.newRow();
row.setCol1("1");

boolean selected = Tx0001.selectRow(row);

if (selected) {
	// 既存の場合、 update
	Tx0001.updateRow(row);
} else {
	// 未登録の場合、 insert
	Tx0001.insertRow(row);
}
```

```java
// col1 = 1 の行を更新
Tx0001.Row row = Tx0001.newRow();
row.setCol1("1");
row.setCol3(5);
row.setCol4("b");
Tx0001.updateRow(row);
```

```java
// col1 = 1 の行を削除
Tx0001.Row row = Tx0001.newRow();
row.setCol1("1");
Tx0001.deleteRow(row);
```

## 任意のSQL

テーブルの結合や、複雑な検索条件などに対応します。

### Javaソースにインライン記述

```sql
Integer max = Sql.currentConnection().selectFirstColumnOne(
	new SqlString("select max(COL3) from TX0001 where COL4=${0}").
	format("Z") );
```

```
Sql.currentConnection().update(new SqlString("delete from TX0001 where 1<>1") );
```

### 外部ファイルに記述

「--!」で開始するコメントは ECMAScript(JavaScript) と解釈されます。

Java側のプロパティは変数 param のプロパティとしてアクセスします。

パラメータの埋め込みは ${変数名} と記述します。

CmdX02.sql
```sql
select 
    T2.COL1,
    T2.COL2,
    T2.COL3,
    T2.COL4 

from
    TX0001 T1,
    TX0002 T2

where T1.COL1 = T2.COL1 and T2.COL1=${param.col1}

--! // col2 が設定されている場合、条件追加
--! if (param.col2 != null) {
     and T2.COL2=${param.col2}
--! }

order by T2.COL1, T2.COL2
```

CmdX02.java
```java
public class CmdX02 extends SqlCmdBase {

    public void execute() throws Exception {
		List<MyData> list = Sql.currentConnection().
			selectAll(MyData.class, buildSql() );
	}

    private String col1;
    private BigDecimal col2;

	public String getCol1() {
		return col1;
	}
	public void setCol1(String col1) {
		this.col1 = col1;
	}
	public BigDecimal getCol2() {
		return col2;
	}
	public void setCol2(BigDecimal col2) {
		this.col2 = col2;
	}

	public static class MyData {

		private String col1;
		private BigDecimal col2;
		private Integer col3;
		private String col4;
		
		public String getCol1() {
			return col1;
		}
		public void setCol1(String col1) {
			this.col1 = col1;
		}
		public BigDecimal getCol2() {
			return col2;
		}
		public void setCol2(BigDecimal col2) {
			this.col2 = col2;
		}
		public Integer getCol3() {
			return col3;
		}
		public void setCol3(Integer col3) {
			this.col3 = col3;
		}
		public String getCol4() {
			return col4;
		}
		public void setCol4(String col4) {
			this.col4 = col4;
		}
	}
}
```
