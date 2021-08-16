package com.example.acessoscartoes;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.acessoscartoes.database.DadosOpenHelper;
import com.example.acessoscartoes.database.DadosOpenHelperLancamentos;
import com.example.acessoscartoes.dominio.entidades.Cartao;
import com.example.acessoscartoes.dominio.repositorio.CartaoRepositorio;
import com.example.acessoscartoes.dominio.repositorio.LancamentoRepositorio;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.acessoscartoes.R.string.msg_copiado_area_transf;
import static com.example.acessoscartoes.R.string.msg_opcao_indisponivel;

public class CadCartaoActivity extends AppCompatActivity {

    // Variáveis PUBLICAS
    public static String strPublicNumCartao;
    public static String strPublicMelhorDiaCompra;
    public static String strPublicDiaVencimento;

    //atributo da classe.
    public AlertDialog alerta;
    // public String strPublicMelhorDia;

    private ImageView imgNomeCartao;
    private ImageView imgExibirSenha;
    private ImageView imgEsconderSenha;

    private EditText edtNomeCartao;
    private EditText edtNumCartao;
    private EditText edtVencimento;
    private EditText edtSenha;
    private EditText edtCVV;
    private EditText edtMelhorDia;
    private EditText edtDiaVenc;

    private Button btnLancamentos;

    ////
    // Variáveis que correspondem a cada campo.
    private String strNomeCartao;
    private String strNumCartao;
    private String strVencimento;
    private String strCVV;
    private String strSenha;
    private String strMelhorDia;
    private String strDiaVenc;
    // Variáveis que correspondem a cada nome de campo.
    ////

    private int intDiaCompra;
    private int intDiaVenc;

    private SQLiteDatabase conexao_db;
    private SQLiteDatabase conexao_db_lanc;

    private DadosOpenHelper dadosOpenHelper;
    private DadosOpenHelper dadosOpenHelperLanc;

    private ConstraintLayout layoutContentCadCartao;
    private CartaoRepositorio cartaoRepositorio;

    private LancamentoRepositorio lancamentoRepositorio;

    private ImageView imgCopiar;

    private Cartao cartao;

    private AlertDialog altera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_cartao);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imgNomeCartao  = (ImageView) findViewById(R.id.imgNomeCartao);
        imgExibirSenha   = (ImageView) findViewById(R.id.imgExibirSenha);
        imgEsconderSenha = (ImageView) findViewById(R.id.imgEsconderSenha);

        edtNomeCartao   = (EditText) findViewById(R.id.edtNomeCartao);
        edtNumCartao    = (EditText) findViewById(R.id.edtNumeroCartao);
        edtVencimento   = (EditText) findViewById(R.id.edtVencimento);
        edtSenha        = (EditText) findViewById(R.id.edtSenha);
        edtCVV          = (EditText) findViewById(R.id.edtCVV);
        edtMelhorDia    = (EditText) findViewById(R.id.edtDiaCompra);
        edtDiaVenc      = (EditText) findViewById(R.id.edtDiaVencimento);

        imgCopiar = (ImageView) findViewById(R.id.imgCopiar);

        layoutContentCadCartao = (ConstraintLayout) findViewById(R.id.layoutContentCadCartao);

        imgNomeCartao.setVisibility(View.INVISIBLE);

        criarConexaoDb();

        recebeParametro();

        ////
        // Criação de máscara para o campo VENCIMENTO
        // Premissa - Adicionar antes em 'build.grandle (.app):
        // compile 'com.github.rtoshiro.mflibrary:mflibrary:1.0.0' + clicar em 'Sync'
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN/NN");
        MaskTextWatcher mtw = new MaskTextWatcher(edtVencimento, smf);
        edtVencimento.addTextChangedListener(mtw);
        // Criação de máscara para o campo VENCIMENTO
        ////

        SimpleMaskFormatter smf_card = new SimpleMaskFormatter("NNNN NNNN NNNN NNNN");
        MaskTextWatcher mtw_card = new MaskTextWatcher(edtNumCartao, smf_card);
        edtNumCartao.addTextChangedListener(mtw_card);

        edtNomeCartao.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (edtNomeCartao.length()>0) {
                    imgNomeCartao.setVisibility(View.INVISIBLE);
                }
            }
        });

        imgExibirSenha.setVisibility(View.VISIBLE);
        imgEsconderSenha.setVisibility(View.INVISIBLE);

        // Botão CONFIRMAR
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                confirmar(view);
            }

        });

        // Botão EXCLUIR
        FloatingActionButton fab3 = findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cartaoRepositorio.excluir(cartao.codigo);
                if (cartao.codigo > 0) {
                    confirmar_exclusao(v);
                }
            }
        });

        // Botão LANÇAMENTOS
        btnLancamentos = (Button) findViewById(R.id.btnLancamentos);
        btnLancamentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valida_campos(v)) {
                    strPublicNumCartao      = edtNumCartao.getText().toString();
                    strPublicMelhorDiaCompra = edtMelhorDia.getText().toString();
                    strPublicDiaVencimento   = edtDiaVenc.getText().toString();
                    Intent i = new Intent(CadCartaoActivity.this, ActivityListaLancamentos.class);
                    startActivity(i);
                }
            }
        });

        imgExibirSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imgEsconderSenha.setVisibility(View.VISIBLE);
                imgExibirSenha.setVisibility(View.INVISIBLE);
            }
        });

        imgEsconderSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imgExibirSenha.setVisibility(View.VISIBLE);
                imgEsconderSenha.setVisibility(View.INVISIBLE);
            }
        });

        imgCopiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager copiar = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                copiar.setText(edtNumCartao.getText());
                Toast.makeText(getBaseContext(), R.string.msg_copiado_area_transf, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpar_campos() {
        edtNomeCartao.setText("");
        edtNumCartao.setText("");
        edtCVV.setText("");
        edtVencimento.setText("");
        edtSenha.setText("");
        edtMelhorDia.setText("");
        edtDiaVenc.setText("");
        edtNomeCartao.requestFocus();
    }

    private void recebeParametro() {

        // Recupera o parâmetro enviado pelo 'CartoesAdapter'
        Bundle bundle = getIntent().getExtras();
        cartao = new Cartao();
        if ( (bundle != null) && (bundle.containsKey("CARTAO")) ) {

            cartao = (Cartao)bundle.getSerializable("CARTAO");

            edtNomeCartao.setText(cartao.nome_cartao);
            edtNumCartao.setText(cartao.num_cartao);
            edtCVV.setText(cartao.cvv);
            edtVencimento.setText(cartao.vencimento);
            edtSenha.setText(cartao.senha);
            edtMelhorDia.setText(cartao.melhor_dia);
            edtDiaVenc.setText(cartao.dia_venc);

        }
    }

    private void msgSnackBar(View v, String strMensagem) {
        Snackbar.make(v, strMensagem, Snackbar.LENGTH_LONG)
                .setAction("OK", null).show();
    }

    private void confirmar_exclusao(View v) {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("Excluir Cartão");
        //define a mensagem
        builder.setMessage("Ao excluir este cartão TODOS os lançamentos referentes a ele serão eliminados. Confirma EXCLUIR este cartão ?");
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                excluirDados(v);
                finish();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
    }

    private void criarConexaoDb() {
        try {

            dadosOpenHelper     = new DadosOpenHelper(this);
            dadosOpenHelperLanc = new DadosOpenHelper(this);

            conexao_db      = dadosOpenHelper.getWritableDatabase();
            conexao_db_lanc = dadosOpenHelper.getWritableDatabase();
            cartaoRepositorio     = new CartaoRepositorio(conexao_db);
            lancamentoRepositorio = new LancamentoRepositorio(conexao_db_lanc);

            //Snackbar.make(layoutContentCadCartao, R.string.msg_conexao_sucesso, Snackbar.LENGTH_LONG)
            //        .setAction("OK", null).show();

        } catch(Exception ex) {

            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle( R.string.msg_erro_conexao );
            dlg.setMessage( ex.getMessage());
            dlg.setNeutralButton(R.string.msg_ok, null);
            dlg.show();

        }
    }

    private void confirmar(View view) {

        // cartao = new Cartao();

        if (valida_campos(view)==true) {
            try {

                if (cartao.codigo==0) {
                    cartaoRepositorio.inserir(cartao);
                    limpar_campos();
                } else {
                    cartaoRepositorio.alterar(cartao);
                }
                msgSnackBar(view, getString(R.string.msg_registro_gravado_sucesso));

            } catch(Exception ex) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle( R.string.msg_erro_conexao );
                dlg.setMessage( ex.getMessage());
                dlg.setNeutralButton(R.string.msg_ok, null);
                dlg.show();
            }
        };

    }

    private boolean valida_campos(View view) {

        boolean res = true;

        strNomeCartao = edtNomeCartao.getText().toString();
        strNumCartao  = edtNumCartao.getText().toString();
        strVencimento = edtVencimento.getText().toString();
        strCVV        = edtCVV.getText().toString();
        strSenha      = edtSenha.getText().toString();
        strMelhorDia  = edtMelhorDia.getText().toString();
        strDiaVenc    = edtDiaVenc.getText().toString();

        cartao.nome_cartao = strNomeCartao;
        cartao.num_cartao  = strNumCartao;
        cartao.vencimento  = strVencimento;
        cartao.cvv         = strCVV;
        cartao.senha       = strSenha;
        cartao.melhor_dia  = strMelhorDia;
        cartao.dia_venc    = strDiaVenc;

        // Validação para preenchimento do nome do cartão.
        if (strNomeCartao.length()==0) {
            msgSnackBar(view, "NOME DO CARTÃO - Campo Obrigatório");
            imgNomeCartao.setVisibility(View.VISIBLE);
            edtNomeCartao.requestFocus();
            res = false;
        }
        //

        // Validação para preenchimento do numero do cartão
        if (edtNomeCartao.getText().length()==0) {
            msgSnackBar(view, getString(R.string.msg_num_cartao_obriga));
            imgNomeCartao.setVisibility(View.VISIBLE);
            edtNumCartao.requestFocus();
            res = false;
        }
        //

        // Validação para o melhor dia para compra.
        if (edtMelhorDia.getText().length() > 0) {
            intDiaCompra = Integer.parseInt(edtMelhorDia.getText().toString());
        } else {
            intDiaCompra = 0;
        }
        if (intDiaCompra < 0 || intDiaCompra >31) {
            msgSnackBar(view, getString(R.string.str_melhor_dia));
            res = false;
        }
        //

        // Validação para o dia de vencimento do cartão.
        if (edtDiaVenc.getText().length() > 0) {
            intDiaVenc = Integer.parseInt(edtDiaVenc.getText().toString());
        } else {
            intDiaVenc = 0;
        }
        if (intDiaVenc < 0 || intDiaVenc >31) {
            msgSnackBar(view, getString(R.string.str_dia_venc));
            res = false;
        }
        //
        return (res);
    }

    private void excluirDados(View view) {

        StringBuilder sql = new StringBuilder();

        try {

            // Exclui o cartão
            cartaoRepositorio.excluir(cartao.codigo);

            // Exclui os lançamentos do cartão
            sql.append("DELETE FROM LANC_CARTAO WHERE NUM_CARTAO=" + CadCartaoActivity.strPublicNumCartao);
            conexao_db_lanc.execSQL(sql.toString());

        } catch (Exception ex) {

            msgSnackBar(view, ex.getMessage());

        }

    }

}