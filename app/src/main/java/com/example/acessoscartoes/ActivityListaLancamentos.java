package com.example.acessoscartoes;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.acessoscartoes.database.DadosOpenHelperLancamentos;
import com.example.acessoscartoes.dominio.entidades.Lancamento;
import com.example.acessoscartoes.dominio.repositorio.LancamentoRepositorio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityListaLancamentos extends AppCompatActivity {

    private static RecyclerView lstDadosLanc;
    private ConstraintLayout layoutListaLancamentosCartao;
    private static LancamentoRepositorio  lancamentoRepositorio;
    private static SQLiteDatabase conexao_db;
    private DadosOpenHelperLancamentos dadosOpenHelperLancamentos;
    private FloatingActionButton fab;
    private FloatingActionButton fabExcluir;

    private TextView txtDiaVencimento;
    private TextView txtDataIncial;

    private static String strDataFinal = "";
    public Calendar calendar;
    private DatePickerDialog datePickerDialog;

    // private String strDataFinal;

    static LancamentosAdapter lancamentosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_lancamentos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lstDadosLanc = (RecyclerView) findViewById(R.id.lstLancamentosCartao);

        layoutListaLancamentosCartao = (ConstraintLayout) findViewById(R.id.layoutListaLancamentosLanc);

        criarConexaoDb();

        lstDadosLanc.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lstDadosLanc.setLayoutManager(linearLayoutManager);

        atualizarListaLanc();

        txtDiaVencimento = (TextView) findViewById(R.id.txtDiaVencimento);
        txtDiaVencimento.setText("VENCIMENTO: " + CadCartaoActivity.strPublicDiaVencimento);

        txtDataIncial = (TextView) findViewById(R.id.txtDataInicial);
        txtDataIncial.setText("");

        // Adicionar novo lançamento
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityListaLancamentos.this, ActivityLancamentos.class);
                startActivity(i);
                atualizarListaLanc();
                // finish();
            }

        });

        // Excluir TODOS os lançamento do cartão
        fabExcluir = findViewById(R.id.fabExcluir);
        fabExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirTodosLancamentosCartao();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0) {
            List<Lancamento> dados = lancamentoRepositorio.buscarTodosLanc(CadCartaoActivity.strPublicNumCartao);
            lancamentosAdapter = new LancamentosAdapter(dados);
            lstDadosLanc.setAdapter(lancamentosAdapter);
        }
    }

    private void criarConexaoDb() {
        try {
            dadosOpenHelperLancamentos = new DadosOpenHelperLancamentos(this);
            conexao_db = dadosOpenHelperLancamentos.getWritableDatabase();
        } catch(Exception ex) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle( R.string.msg_erro_conexao );
            dlg.setMessage( ex.getMessage());
            dlg.setNeutralButton(R.string.msg_ok, null);
            dlg.show();
        }
    }

    private void excluirTodosLancamentosCartao() {
        StringBuilder sql = new StringBuilder();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirma EXCLUIR TODOS os lançamentos deste cartão ?").setCancelable(false).setPositiveButton("Sim", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sql.append( "DELETE FROM LANC_CARTAO" );
                        sql.append( "   WHERE NUM_CARTAO=" + CadCartaoActivity.strPublicNumCartao);
                        try {
                            conexao_db.execSQL(sql.toString());
                            atualizarListaLanc();
                            Toast.makeText(getBaseContext(), (R.string.msg_lancamento_excluidos_sucesso), Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Toast.makeText( getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                    private void finishActivity() {
                        finish();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Atualiza a lista de lancamentos
    public static void atualizarListaLanc() {
        lancamentoRepositorio = new LancamentoRepositorio(conexao_db);
        List<Lancamento> dados = lancamentoRepositorio.buscarTodosLanc(CadCartaoActivity.strPublicNumCartao);
        lancamentosAdapter = new LancamentosAdapter(dados);
        lstDadosLanc.setAdapter(lancamentosAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_lancamentos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filtrar) {
            calendarioBuscarLancPorFiltro();
        }
        else if (id == R.id.action_executar_filtro) {
            if (txtDataIncial.getText().length()==0) {
                Toast.makeText(getBaseContext(), "Escolha uma DATA para executar o filtro", Toast.LENGTH_LONG).show();
            } else {
                executarBuscaPorFiltro();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void calendarioBuscarLancPorFiltro() {

        calendar = Calendar.getInstance();
        int dia  = calendar.get(Calendar.DAY_OF_MONTH);
        int mes  = calendar.get(Calendar.MONTH);
        int ano  = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(ActivityListaLancamentos.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String strDia;
                String strMes;
                String strAno;

                strDia = Integer.toString(dayOfMonth-1);
                strMes = Integer.toString(month+1);
                strAno = Integer.toString(year);

                if (dayOfMonth<10) {
                    strDia = "0" + strDia;
                }
                if (month+1 < 10) {
                    strMes = "0" + strMes;
                }

                strDataFinal = strDia + "/" + strMes + "/" + strAno;
                txtDataIncial.setText(strDia + "/" + strMes + "/" + strAno);

            }
        }, ano, mes, dia);
        datePickerDialog.show();

    }

    public void executarBuscaPorFiltro() {

        lancamentoRepositorio = new LancamentoRepositorio(conexao_db);
        List<Lancamento> dados = lancamentoRepositorio.buscarPorFiltroData(CadCartaoActivity.strPublicNumCartao, txtDataIncial.getText().toString());
        lancamentosAdapter = new LancamentosAdapter(dados);
        lstDadosLanc.setAdapter(lancamentosAdapter);

    }

}