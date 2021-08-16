package com.example.acessoscartoes.dominio.repositorio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AlertDialog;

import com.example.acessoscartoes.R;
import com.example.acessoscartoes.dominio.entidades.Cartao;
import com.example.acessoscartoes.dominio.entidades.Config;

import java.util.ArrayList;
import java.util.List;

public class ConfigRepositorio {

    private SQLiteDatabase conexaoDb;

    public ConfigRepositorio(SQLiteDatabase conexao) {

        this.conexaoDb = conexao;
    }

    public void inserir(Config config) {

    }

    public List<Config> buscarTodos() {

        List<Config> config = new ArrayList<Config>();

        StringBuilder sql = new StringBuilder();
        sql.append( "SELECT CODIGO, NOME, SENHA, CELULAR " );
        sql.append( "    FROM CONFIG_CARTOES ");

        Cursor resultado = conexaoDb.rawQuery(sql.toString(), null);

        try {
            if (resultado.getCount() > 0) {

                // Verifica se retornou algum registro na consulta.
                resultado.moveToFirst();    // Se sim, move para o primeiro registro.

                do {

                    Config conf = new Config();

                    conf.codigo = resultado.getInt(resultado.getColumnIndexOrThrow("CODIGO"));
                    conf.nome = resultado.getString(resultado.getColumnIndexOrThrow("NOME"));
                    conf.senha = resultado.getString(resultado.getColumnIndexOrThrow("SENHA"));
                    conf.celular = resultado.getString(resultado.getColumnIndexOrThrow("CELULAR"));

                    config.add(conf);

                } while (resultado.moveToNext());
            }
        } catch (Exception ex) {
        }

        return config;
    }

}
