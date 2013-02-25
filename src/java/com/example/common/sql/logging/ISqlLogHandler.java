package com.example.common.sql.logging;

/**
 * データベース接続のログをハンドリングするインタフェース.
 * @author Kazuhiko Arase
 */
public interface ISqlLogHandler {

    /**
     * ログを出力する。
     * @param method メソッド名
     * @param info 付加情報
     */
    void log(String method, String info);
}
