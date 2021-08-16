package com.example.acessoscartoes.dominio.repositorio;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.acessoscartoes.dominio.entidades.Cartao;
import com.example.acessoscartoes.dominio.entidades.Lancamento;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LancamentoRepositorio {

    private SQLiteDatabase conexaoDbLanc;

    public static float nValorTotal;

    public LancamentoRepositorio(SQLiteDatabase conexao_lanc) {
        this.conexaoDbLanc = conexao_lanc;
    }


    public void inserir_lanc(Lancamento lancamento) {

        ContentValues contentValues = new ContentValues();

        contentValues.put("DATA_COMPRA"  , lancamento.data_compra);
        contentValues.put("DESCRICAO"    , lancamento.descricao);
        contentValues.put("VALOR_COMPRA" , lancamento.valor_compra);
        contentValues.put("PARCELAS"     , lancamento.parcelas);
        contentValues.put("DATA_1_PARC"  , lancamento.data_1_parc);
        contentValues.put("VALOR_PARC"   , lancamento.valor_parc);
        contentValues.put("DATA_ULT_PARC", lancamento.data_ult_parc);
        contentValues.put("NUM_CARTAO"   , lancamento.num_cartao);

        conexaoDbLanc.insertOrThrow("LANC_CARTAO", null, contentValues);

    }


    public void excluir_lanc(int codigo) {

        String[] parametros = new String[1];
        parametros[0] = String.valueOf(codigo);

        conexaoDbLanc.delete( "LANC_CARTAO", "CODIGO = ?", parametros);

    }


    public void alterar_lanc(Lancamento lancamento) {

        ContentValues contentValues = new ContentValues();

        contentValues.put("DATA_COMPRA"  , lancamento.data_compra);
        contentValues.put("DESCRICAO"    , lancamento.descricao);
        contentValues.put("VALOR_COMPRA" , lancamento.valor_compra);
        contentValues.put("PARCELAS"     , lancamento.parcelas);
        contentValues.put("DATA_1_PARC"  , lancamento.data_1_parc);
        contentValues.put("VALOR_PARC"   , lancamento.valor_parc);
        contentValues.put("DATA_ULT_PARC", lancamento.data_ult_parc);
        contentValues.put("NUM_CARTAO"   , lancamento.num_cartao);

        String[] parametros = new String[1];
        parametros[0] = String.valueOf(lancamento.codigo);

        conexaoDbLanc.update("LANC_CARTAO", contentValues, "CODIGO = ?", parametros );

    }


    public List<Lancamento> buscarPorFiltroData(String strPublicNumCartao, String strDataInicial) {

        // Realiza a busca de lançamentos de um determinado cartão a partir de uma data inicial de vencimento.

        Date dteDataInicial;
        Date dteDataUltCompra;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        List<Lancamento> lancamentos = new ArrayList<Lancamento>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CODIGO, DATA_COMPRA, DATA_ULT_PARC, DESCRICAO, DATA_COMPRA, VALOR_COMPRA, VALOR_PARC, PARCELAS, DATA_1_PARC ");
        sql.append("   FROM LANC_CARTAO ");
        // sql.append("      WHERE NUM_CARTAO=? AND DATA_ULT_PARC BETWEEN ? AND ?");
        sql.append("      WHERE NUM_CARTAO=?");
        sql.append("         ORDER BY DATA_ULT_PARC DESC, DATA_COMPRA ASC");

        String[] parametros = {strPublicNumCartao};
        //parametros[0] = strPublicNumCartao;
        //parametros[1] = strDataInicial;

        Cursor resultado = conexaoDbLanc.rawQuery(sql.toString(), parametros);

        nValorTotal = 0;

        if (resultado.getCount() > 0) {

            resultado.moveToFirst();

            do {

                Lancamento lan = new Lancamento();

                lan.codigo = resultado.getInt(resultado.getColumnIndexOrThrow("CODIGO"));
                lan.data_compra = resultado.getString(resultado.getColumnIndexOrThrow("DATA_COMPRA"));
                lan.data_ult_parc = resultado.getString(resultado.getColumnIndexOrThrow("DATA_ULT_PARC"));
                lan.descricao = resultado.getString(resultado.getColumnIndexOrThrow("DESCRICAO"));
                lan.data_compra = resultado.getString(resultado.getColumnIndexOrThrow("DATA_COMPRA"));
                lan.valor_compra = resultado.getFloat(resultado.getColumnIndexOrThrow("VALOR_COMPRA"));
                lan.valor_parc = resultado.getFloat(resultado.getColumnIndexOrThrow("VALOR_PARC"));
                lan.parcelas = resultado.getInt(resultado.getColumnIndexOrThrow("PARCELAS"));
                lan.data_1_parc = resultado.getString(resultado.getColumnIndexOrThrow("DATA_1_PARC"));

                try {
                    dteDataInicial   = dateFormat.parse(strDataInicial);
                    dteDataUltCompra = dateFormat.parse(lan.data_ult_parc);
                    if (dteDataUltCompra.after(dteDataInicial)) {
                        nValorTotal = nValorTotal + lan.valor_parc;
                        lancamentos.add(lan);
                    }
                } catch(ParseException p) {

                }

            } while (resultado.moveToNext());

        }

        return lancamentos;
    }


    public List<Lancamento> buscarTodosLanc(String strPublicNumCartao) {

        // Lancamento lancamento = new Lancamento();

        List<Lancamento> lancamentos = new ArrayList<Lancamento>();

        StringBuilder sql = new StringBuilder();
        sql.append( "SELECT CODIGO, DATA_COMPRA, DATA_ULT_PARC, DESCRICAO, DATA_COMPRA, VALOR_COMPRA, VALOR_PARC, PARCELAS, DATA_1_PARC ");
        sql.append( "   FROM LANC_CARTAO " );
        sql.append( "      WHERE NUM_CARTAO=? " );
        sql.append( "         ORDER BY DATA_ULT_PARC DESC" );

        String[] parametro = new String[1];
        parametro[0] = strPublicNumCartao;

        Cursor resultado = conexaoDbLanc.rawQuery( sql.toString(), parametro );

        if (resultado.getCount() > 0) {

            resultado.moveToFirst();

            do {
                Lancamento lan = new Lancamento();

                lan.codigo        = resultado.getInt(resultado.getColumnIndexOrThrow("CODIGO"));
                lan.data_compra   = resultado.getString(resultado.getColumnIndexOrThrow("DATA_COMPRA"));
                lan.data_ult_parc = resultado.getString(resultado.getColumnIndexOrThrow("DATA_ULT_PARC"));
                lan.descricao     = resultado.getString(resultado.getColumnIndexOrThrow("DESCRICAO"));
                lan.data_compra   = resultado.getString(resultado.getColumnIndexOrThrow("DATA_COMPRA"));
                lan.valor_compra  = resultado.getFloat(resultado.getColumnIndexOrThrow( "VALOR_COMPRA"));
                lan.valor_parc    = resultado.getFloat(resultado.getColumnIndexOrThrow("VALOR_PARC"));
                lan.parcelas      = resultado.getInt(resultado.getColumnIndexOrThrow("PARCELAS"));
                lan.data_1_parc   = resultado.getString(resultado.getColumnIndexOrThrow("DATA_1_PARC"));

                lancamentos.add(lan);

            } while (resultado.moveToNext());

        }

        return lancamentos;

    }


    public Lancamento buscarLancamento(int codigo) {
        Lancamento lancamento = new Lancamento();

        return lancamento;
    }
}
