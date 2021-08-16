package com.example.acessoscartoes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.acessoscartoes.database.DadosOpenHelper;
import com.example.acessoscartoes.database.DadosOpenHelperConfig;
import com.example.acessoscartoes.dominio.entidades.Cartao;
import com.example.acessoscartoes.dominio.entidades.Config;
import com.example.acessoscartoes.dominio.repositorio.CartaoRepositorio;
import com.example.acessoscartoes.dominio.repositorio.ConfigRepositorio;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ActivityLogin extends AppCompatActivity {

    private DadosOpenHelperConfig dadosOpenHelperConfig;

    private SQLiteDatabase conexao_db;

    private ConstraintLayout layoutMainLogin;

    private EditText edtSenhaLogin;
    private Button btnProsseguir;

    private ConfigRepositorio configRepositorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        criarConexaoDbConfig();

        // buscar_confg();

        configRepositorio = new ConfigRepositorio(conexao_db);
        List<Config> dados = configRepositorio.buscarTodos();
        if (dados.size()==0) {
            // Toast.makeText(getBaseContext(), "CONFIG VAZIO", Toast.LENGTH_SHORT).show();
        }


        layoutMainLogin = (ConstraintLayout) findViewById(R.id.layoutMainLogin);

        edtSenhaLogin = (EditText) findViewById(R.id.edtSenhaLogin);

        btnProsseguir = (Button) findViewById(R.id.btnProsseguir);
        btnProsseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtSenhaLogin.getText().toString().equals("0512")) {
                    Intent i = new Intent(ActivityLogin.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), R.string.msg_senha_invalida, Toast.LENGTH_SHORT).show();
                    edtSenhaLogin.setText("");
                    edtSenhaLogin.requestFocus();
                    return;
                }
            }
        });

    }

    private void criarConexaoDbConfig() {
        try {
            dadosOpenHelperConfig = new DadosOpenHelperConfig(this);
            conexao_db = dadosOpenHelperConfig.getWritableDatabase();
            Toast.makeText(getBaseContext(), R.string.msg_arq_config_sucesso, Toast.LENGTH_SHORT).show();
            //Snackbar.make(layoutMainLogin, R.string.msg_conexao_sucesso, Snackbar.LENGTH_LONG)
            //        .setAction("OK", null).show();
        } catch(Exception ex) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle( R.string.msg_erro_conexao );
            dlg.setMessage( ex.getMessage());
            dlg.setNeutralButton(R.string.msg_ok, null);
            dlg.show();
        }
    }


    private void buscar_confg() {
        StringBuilder sql = new StringBuilder();
        sql.append( "SELECT * FROM CONFIG_CARTOES" );
        try {
        Cursor dados = conexao_db.rawQuery(sql.toString(),null);
        if (dados.getCount()==0) {
            Toast.makeText(getBaseContext(), "Arquivo de configuração vazio", Toast.LENGTH_SHORT).show();
        }

        } catch(Exception ex) {
            Toast.makeText(getBaseContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

}