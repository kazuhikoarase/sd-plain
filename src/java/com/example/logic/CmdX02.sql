--
-- 絞り込み検索
-- 「--」 で開始する行はコメントです。
-- 「--!」 で開始するコメントは ECMAScript(JavaScript) と解釈されます。
-- Java側のプロパティは変数 param のプロパティとしてアクセスします。
-- パラメータの埋め込みは ${変数名} と記述します。
--

select 
T2.COL1,
T2.COL2,
T2.COL3,
T2.COL4 

from
TX0001 T1,
TX0002 T2

where T1.COL1 = T2.COL1

and T2.COL1=${param.col1}

-- 絞り込み条件
--! // col2 が設定されている場合、条件追加
--! if (param.col2 != null) {
	 and T2.COL2=${param.col2}
--! }

-- 並べ替え
order by T2.COL1, T2.COL2
