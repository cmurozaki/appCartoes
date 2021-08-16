package com.example.acessoscartoes.database;

public class ScriptDLL {

    public static String getCreateTableCartoes() {

        StringBuilder sql = new StringBuilder();

        sql.append( "CREATE TABLE IF NOT EXISTS CARTOES (" );
        sql.append( "  CODIGO      INTEGER     PRIMARY KEY AUTOINCREMENT NOT NULL," );
        sql.append( "  NOME_CARTAO VARCHAR(40) NOT NULL DEFAULT (''), " );
        sql.append( "  NUM_CARTAO  VARCHAR(16) NOT NULL DEFAULT (''), " );
        sql.append("   VENCIMENTO  VARCHAR(5)  NOT NULL DEFAULT (''), " );
        sql.append("   CVV         VARCHAR(3)  NOT NULL DEFAULT (''), " );
        sql.append("   SENHA       VARCHAR(15) NOT NULL DEFAULT (''), " );
        sql.append("   MELHOR_DIA  VARCHAR(2)  NOT NULL DEFAULT (''), " );
        sql.append("   DIA_VENC    VARCHAR(2)  NOT NULL DEFAULT ('') )" );

        return sql.toString();

    }

    public static String getCreateTableConfig() {

        StringBuilder sql = new StringBuilder();

        sql.append( "CREATE TABLE IF NOT EXISTS CONFIG_CARTOES (" );
        sql.append( "  CODIGO  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," );
        sql.append( "  NOME    VARCHAR(15)  NOT NULL DEFAULT (''), " );
        sql.append( "  SENHA   VARCHAR(4)   NOT NULL DEFAULT (''), " );
        sql.append( "  CELULAR VARCHAR(12) NOT NULL DEFAULT ('') )" );

        return sql.toString();

    }

    public static String getCreateTableLancamentos() {

        StringBuilder sql = new StringBuilder();

        sql.append( "CREATE TABLE IF NOT EXISTS LANC_CARTAO( ");
        sql.append( "  CODIGO        INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " );
        sql.append( "  NUM_CARTAO    VARCHAR(16) NOT NULL DEFAULT (''), " );
        sql.append( "  DATA_COMPRA   DATE        NOT NULL DEFAULT (0), " );
        sql.append( "  DESCRICAO     VARCHAR(40) NOT NULL DEFAULT (''), " );
        sql.append( "  VALOR_COMPRA  REAL        NOT NULL DEFAULT (0), " );
        sql.append( "  PARCELAS      INTEGER     NOT NULL DEFAULT (1), " );
        sql.append( "  DATA_1_PARC   DATE        NOT NULL DEFAULT (0), " );
        sql.append( "  VALOR_PARC    REAL        NOT NULL DEFAULT (0), " );
        sql.append( "  DATA_ULT_PARC DATE        NOT NULL DEFAULT (0) ) ");

        return sql.toString();

    }
}
