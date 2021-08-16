package com.example.acessoscartoes;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.acessoscartoes.database.DadosOpenHelper;
import com.example.acessoscartoes.database.DadosOpenHelperLancamentos;
import com.example.acessoscartoes.dominio.entidades.Cartao;
import com.example.acessoscartoes.dominio.repositorio.CartaoRepositorio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView lstDados;
    private FloatingActionButton fab;

    private SQLiteDatabase conexao_db;
    private SQLiteDatabase conexao_db_lanc;

    private DadosOpenHelper dadosOpenHelper;

    private DadosOpenHelperLancamentos dadosOpenHelperLancamentos;

    private ConstraintLayout layoutMain;

    private CartoesAdapter cartoesAdapter;

    private CartaoRepositorio cartaoRepositorio;

    private void pedirPermissoes() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        // else
            // configurarServico();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // configurarServico();
                    Toast.makeText(this, "Permissão concedida.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Não vai funcionar!!!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pedirPermissoes();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        lstDados = (RecyclerView) findViewById(R.id.lstDados);

        layoutMain = (ConstraintLayout) findViewById(R.id.layoutListaLancamentos);

        criarConexaoDb();
        criarConexaoDbLancamentos();

        lstDados.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lstDados.setLayoutManager(linearLayoutManager);

        cartaoRepositorio = new CartaoRepositorio(conexao_db);
        List<Cartao> dados = cartaoRepositorio.buscarTodos();

        cartoesAdapter = new CartoesAdapter(dados);
        lstDados.setAdapter(cartoesAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CadCartaoActivity.class);
                startActivityForResult(i, 0);       // Usado quando necessita de atualizar os dados. O tratamento está abaixo no 'protected void onActivityResult'
            }
        });

        Button but = (Button) findViewById(R.id.button);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage("011994813368", null,"Teste", null, null);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0) {
            List<Cartao> dados = cartaoRepositorio.buscarTodos();
            cartoesAdapter = new CartoesAdapter(dados);
            lstDados.setAdapter(cartoesAdapter);
        }
    }

    private void criarConexaoDb() {
        try {
            dadosOpenHelper = new DadosOpenHelper(this);
            conexao_db = dadosOpenHelper.getWritableDatabase();
            //Snackbar.make(layoutMain, R.string.msg_conexao_sucesso, Snackbar.LENGTH_LONG)
            //        .setAction("OK", null).show();
        } catch(Exception ex) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle( R.string.msg_erro_conexao );
            dlg.setMessage( ex.getMessage());
            dlg.setNeutralButton(R.string.msg_ok, null);
            dlg.show();
        }
    }

    private void criarConexaoDbLancamentos() {
        try {
            dadosOpenHelperLancamentos = new DadosOpenHelperLancamentos(this);
            conexao_db_lanc = dadosOpenHelperLancamentos.getWritableDatabase();
            //Toast.makeText(this, R.string.msg_conexao_db_lanc_sucesso, Toast.LENGTH_SHORT).show();
        } catch(Exception ex) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle( R.string.msg_erro_conexao );
            dlg.setMessage( ex.getMessage());
            dlg.setNeutralButton(R.string.msg_ok, null);
            dlg.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(layoutMain, "Indisponível", Snackbar.LENGTH_SHORT)
                    .setAction("OK", null).show();
            return true;
        }
        if (id == R.id.action_close) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void altera_estrutura_tabela_lanc() {

        StringBuilder sql = new StringBuilder();

        sql.append("ALTER TABLE LANC_CARTAO");
    }

    public void excluirTabelas() {

        StringBuilder sql = new StringBuilder();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirma EXCLUIR a Tabela de Lançamentos ?").setCancelable(false).setPositiveButton("Sim", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sql.append( "DROP TABLE IF EXISTS LANCAMENTOS" );
                        try {
                            conexao_db_lanc.execSQL(sql.toString());
                            Toast.makeText(getBaseContext(), "Tabela excluída com sucesso", Toast.LENGTH_LONG).show();
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
}