package com.example.acessoscartoes.dominio.repositorio;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.acessoscartoes.dominio.entidades.Cartao;

import java.util.ArrayList;
import java.util.List;

public class CartaoRepositorio {

    private SQLiteDatabase conexaoDb;

    public CartaoRepositorio(SQLiteDatabase conexao) {
        this.conexaoDb = conexao;
    }

    public void inserir(Cartao cartao) {

        ContentValues contentValues = new ContentValues();

        contentValues.put("NOME_CARTAO", cartao.nome_cartao);
        contentValues.put("NUM_CARTAO", cartao.num_cartao);
        contentValues.put("VENCIMENTO", cartao.vencimento);
        contentValues.put("SENHA", cartao.senha);
        contentValues.put("CVV", cartao.cvv);
        contentValues.put("MELHOR_DIA", cartao.melhor_dia);
        contentValues.put("DIA_VENC", cartao.dia_venc);

        conexaoDb.insertOrThrow("CARTOES", null, contentValues);

    }

    public void excluir(int codigo) {

        String[] parametros = new String[1];
        parametros[0] = String.valueOf(codigo);

        conexaoDb.delete( "CARTOES", "CODIGO = ?", parametros);

    }

    public void alterar(Cartao cartao) {

        ContentValues contentValues = new ContentValues();

        contentValues.put("NOME_CARTAO", cartao.nome_cartao);
        contentValues.put("NUM_CARTAO" , cartao.num_cartao);
        contentValues.put("VENCIMENTO" , cartao.vencimento);
        contentValues.put("SENHA"      , cartao.senha);
        contentValues.put("CVV"        , cartao.cvv);
        contentValues.put("MELHOR_DIA" , cartao.melhor_dia);
        contentValues.put("DIA_VENC"   , cartao.dia_venc);

        String[] parametros = new String[1];
        parametros[0] = String.valueOf(cartao.codigo);

        conexaoDb.update("CARTOES", contentValues, "CODIGO = ?", parametros );

    }

    public List<Cartao> buscarTodos() {

        List<Cartao> cartoes = new ArrayList<Cartao>();

        StringBuilder sql = new StringBuilder();
        sql.append( "SELECT CODIGO, NOME_CARTAO, NUM_CARTAO, VENCIMENTO, CVV, SENHA, MELHOR_DIA, DIA_VENC ");
        sql.append( "    FROM CARTOES ");
        sql.append( "       ORDER BY MELHOR_DIA" );

        Cursor resultado = conexaoDb.rawQuery(sql.toString(), null);

        if (resultado.getCount() > 0) {

            // Verifica se retornou algum registro na consulta.
            resultado.moveToFirst();    // Se sim, move para o primeiro registro.

            do{

                Cartao car = new Cartao();

                car.codigo      = resultado.getInt( resultado.getColumnIndexOrThrow("CODIGO") );
                car.nome_cartao = resultado.getString( resultado.getColumnIndexOrThrow("NOME_CARTAO"));
                car.num_cartao  = resultado.getString( resultado.getColumnIndexOrThrow("NUM_CARTAO"));
                car.vencimento  = resultado.getString( resultado.getColumnIndexOrThrow("VENCIMENTO"));
                car.cvv         = resultado.getString( resultado.getColumnIndexOrThrow("CVV"));
                car.senha       = resultado.getString( resultado.getColumnIndexOrThrow("SENHA"));
                car.melhor_dia  = resultado.getString( resultado.getColumnIndexOrThrow("MELHOR_DIA"));
                car.dia_venc    = resultado.getString( resultado.getColumnIndexOrThrow("DIA_VENC"));

                cartoes.add(car);

            } while(resultado.moveToNext());
        }

        return cartoes;

    }

    public Cartao buscarCartao( int codigo) {

        Cartao cartao = new Cartao();

        StringBuilder sql = new StringBuilder();
        sql.append( "SELECT CODIGO, NOME_CARTAO, NUM_CARTAO, VENCIMENTO, CVV, SENHA, MELHOR_DIA, DIA_VENC ");
        sql.append( "    FROM CARTOES ");
        sql.append( "       WHERE CODIGO = ?" );

        String[] parametros = new String[1];
        parametros[0] = String.valueOf(codigo);

        Cursor resultado = conexaoDb.rawQuery(sql.toString(), parametros);

        if (resultado.getCount() > 0) {

            // Verifica se retornou algum registro na consulta.
            resultado.moveToFirst();    // Se sim, move para o primeiro registro.

            cartao.codigo      = resultado.getInt(resultado.getColumnIndexOrThrow("CODIGO"));
            cartao.nome_cartao = resultado.getString(resultado.getColumnIndexOrThrow("NOME_CARTAO"));
            cartao.num_cartao  = resultado.getString(resultado.getColumnIndexOrThrow("NUM_CARTAO"));
            cartao.vencimento  = resultado.getString(resultado.getColumnIndexOrThrow("VENCIMENTO"));
            cartao.cvv         = resultado.getString(resultado.getColumnIndexOrThrow("CVV"));
            cartao.senha       = resultado.getString(resultado.getColumnIndexOrThrow("SENHA"));
            cartao.melhor_dia  = resultado.getString(resultado.getColumnIndexOrThrow("MELHOR_DIA"));
            cartao.dia_venc    = resultado.getString(resultado.getColumnIndexOrThrow("DIA_VENC"));

            return cartao;

        }

        return null;

    }

}

