sd-plain
========
最小限のDAOとWEBアプリのひな形



### Javaソースにインライン記述

```sql
Integer max = Sql.currentConnection().selectFirstColumnOne(
	new SqlString("select max(COL3) from TX0001 where COL4=${0}").
	format("Z") );
```


## 任意のSQL

### 外部ファイルに記述

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
