package com.example.acessoscartoes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Currency;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.acessoscartoes.database.DadosOpenHelper;
import com.example.acessoscartoes.database.DadosOpenHelperLancamentos;
import com.example.acessoscartoes.dominio.entidades.Cartao;
import com.example.acessoscartoes.dominio.entidades.Lancamento;
import com.example.acessoscartoes.dominio.repositorio.CartaoRepositorio;
import com.example.acessoscartoes.dominio.repositorio.LancamentoRepositorio;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.abhinay.input.CurrencyEditText;

public class ActivityLancamentos extends AppCompatActivity {

    public int    intNumParcelas;
    public float  nValorCompra;
    public int    intCodigoLanc;

    public String      dtPrimeiraParcela;
    public AlertDialog alerta;

    private EditText             edtDataCompra;
    private EditText             edtDataPrimeiraParcela;
    private EditText             edtDataUltimaParcela;
    private EditText             edtDiaMelhorCompra;
    private EditText             edtDiaVenc;
    private EditText             edtNumParcelas;
    private EditText             edtDescricaoCompra;
    private ImageButton          ibtnCalendario;
    private ImageButton          ibtnCalendarioPrimParc;
    private Calendar             calendar;
    private DatePickerDialog     datePickerDialog;
    private Button               btnCalcular;
    private FloatingActionButton btnConfirmar;
    private FloatingActionButton btnExcluirLancamento;

    private Lancamento                 lancamento;
    private LancamentoRepositorio      lancamentoRepositorio;
    private DadosOpenHelperLancamentos dadosOpenHelperLancamentos;
    private SQLiteDatabase             conexao_db;
    private LancamentosAdapter         lancamentosAdapter;

    ////
    // Máscara para valor monetário
    // - Premissa: Adicionar a linha abaixo em 'build.gradle (.app):
    //   implementation 'me.abhinay.input:currency-edittext:1.1' + <Sync Now>
    // - No XML (exemplo):
    //     <me.abhinay.input.CurrencyEditText
    //        android:id="@+id/edtValorTotalCompra"
    //        android:layout_width="wrap_content"
    //        android:layout_height="wrap_content"
    //        android:hint="R$ 999.999,99"
    //        android:inputType="numberDecimal"
    //        app:layout_constraintStart_toStartOf="@+id/textView11"
    //        app:layout_constraintTop_toBottomOf="@+id/textView11"
    //        android:textSize="18sp" />
    private CurrencyEditText edtValorTotalCompra;
    private CurrencyEditText edtValorPorParcela;

    private boolean isUpdating = false;

    private NumberFormat numberFormat = NumberFormat.getCurrencyInstance();     // Pega a formatacao do sistema, se for brasil R$ se EUA US$

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamentos);

        criarConexaoDb();

        // Toast.makeText(this, CadCartaoActivity.strPublicNumCartao, Toast.LENGTH_LONG).show();

        edtValorTotalCompra    = (CurrencyEditText) findViewById(R.id.edtValorTotalCompra);
        edtValorPorParcela     = (CurrencyEditText) findViewById(R.id.edtValorPorParcela);
        edtDataCompra          = (EditText) findViewById(R.id.edtDataCompra);
        edtDiaMelhorCompra     = (EditText) findViewById(R.id.edtDiaMelhorCompra);
        edtDiaVenc             = (EditText) findViewById(R.id.edtDiaVenc);
        edtDataUltimaParcela   = (EditText) findViewById(R.id.edtDataUltimaParcela);
        ibtnCalendario         = (ImageButton) findViewById(R.id.ibtnCalendario);
        ibtnCalendarioPrimParc = (ImageButton) findViewById(R.id.ibtnCalendarioPrimParc);
        edtDataPrimeiraParcela = (EditText) findViewById(R.id.edtDataPrimeiraParcela);
        btnCalcular            = (Button) findViewById(R.id.btnCalcular);
        edtNumParcelas         = (EditText) findViewById(R.id.edtNumParcelas);
        edtDescricaoCompra     = (EditText) findViewById(R.id.edtDescricaoCompra);
        btnConfirmar           = (FloatingActionButton) findViewById(R.id.btnConfirmarLancamento);
        btnExcluirLancamento   = (FloatingActionButton) findViewById(R.id.btnExcluirLancamento);

        // edtValorTotalCompra.setCurrency("R$"); Não fica bom.
        edtValorTotalCompra.setDelimiter(false);
        edtValorTotalCompra.setSpacing(false);
        edtValorTotalCompra.setDecimals(true);
        //Make sure that Decimals is set as false if a custom Separator is used
        edtValorTotalCompra.setSeparator("");

        recebeParametro();

        // Recebe o valor passado pela Activity anterior
        Intent valor = getIntent();
        edtDiaMelhorCompra.setText(CadCartaoActivity.strPublicMelhorDiaCompra);
        edtDiaVenc.setText(CadCartaoActivity.strPublicDiaVencimento);

        // Botão CALENDÁRIO - DATA DA COMPRA
        ibtnCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                int dia  = calendar.get(Calendar.DAY_OF_MONTH);
                int mes  = calendar.get(Calendar.MONTH);
                int ano  = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(ActivityLancamentos.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String cDia = Integer.toString(dayOfMonth);
                        String cMes = Integer.toString(month+1);
                        if (dayOfMonth<10) {
                            cDia = "0" + cDia;
                        }
                        if (month+1 < 10) {
                            cMes = "0" + cMes;
                        }
                        edtDataCompra.setText(cDia + "/" + cMes + "/" + year);
                    }
                }, ano, mes, dia);
                datePickerDialog.show();
            }
        });

        // Botão CALENDÁRIO - PRIMEIRA PARCELA
        ibtnCalendarioPrimParc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                int dia  = calendar.get(Calendar.DAY_OF_MONTH);
                int mes  = calendar.get(Calendar.MONTH);
                int ano  = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(ActivityLancamentos.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String cDia = Integer.toString(dayOfMonth);
                        String cMes = Integer.toString(month+1);
                        if (dayOfMonth<10) {
                            cDia = "0" + cDia;
                        }
                        if (month+1 < 10) {
                            cMes = "0" + cMes;
                        }
                        edtDataPrimeiraParcela.setText(cDia + "/" + cMes + "/" + year);
                    }
                }, ano, mes, dia);
                datePickerDialog.show();
            }
        });

        // Botão CALCULAR
        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int intNumDias;
                Date dteUltimaParcela;

                // Esconde o teclado virtual
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edtNumParcelas.getWindowToken(),0);

                if (valida_campos(v)) {
                    intNumDias = (intNumParcelas * 30) - 30;   // Número de dias aproximado para a última parcela.
                    // Formata o campo do valor de cada parcela.
                    //DecimalFormat df = new DecimalFormat("0.00");
                    //cValorPorParcela = df.format(nValorCompra / intNumParcelas );
                    //edtValorPorParcela.setText(cValorPorParcela);
                    //
                    // Data da última parcela
                    String dataInicio = edtDataPrimeiraParcela.getText().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar c = Calendar.getInstance();
                    try {
                        dteUltimaParcela = dateFormat.parse(dataInicio);
                        c.setTime(dteUltimaParcela);
                        c.add( c.DAY_OF_MONTH, intNumDias);
                        Date dteDataFinal = c.getTime();
                        String strDataUltimaParcela = dateFormat.format(dteDataFinal);
                        strDataUltimaParcela = strDataUltimaParcela.substring(2);
                        strDataUltimaParcela = CadCartaoActivity.strPublicDiaVencimento +
                                strDataUltimaParcela;
                        edtDataUltimaParcela.setText(strDataUltimaParcela);
                    } catch (ParseException e) {
                        msg_Snackbar(v, e.getMessage());
                    }
                    //
                } else {
                    //Snackbar.make(v, "Não Ok", Snackbar.LENGTH_LONG)
                    //        .setAction("OK", null).show();
                }
            }
        });

        // Botão CONFIRMAR
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmar(v);
            }
        });

        // Botão EXCLUIR LANÇAMENTO
        btnExcluirLancamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarExclusao(v);
            }
        });

        ////
        // Criação de máscara para o campo DATA DA COMPRA
        // Premissa - Adicionar antes em 'build.grandle (.app):
        // compile 'com.github.rtoshiro.mflibrary:mflibrary:1.0.0' + clicar em 'Sync'
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(edtDataCompra, smf);
        edtDataCompra.addTextChangedListener(mtw);
        // Criação de máscara para o campo DATA DA COMPRA
        ////

        ////
        // Criação de máscara para o campo DATA DA PRIMEIRA PARCELA
        // Premissa - Adicionar antes em 'build.grandle (.app):
        // compile 'com.github.rtoshiro.mflibrary:mflibrary:1.0.0' + clicar em 'Sync'
        SimpleMaskFormatter smf2 = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw2 = new MaskTextWatcher(edtDataPrimeiraParcela, smf2);
        edtDataPrimeiraParcela.addTextChangedListener(mtw2);
        // Criação de máscara para o campo DATA DA PRIMEIRA PARCELA
        ////

        ////
        // Criação de máscara para o campo DATA DA ULTIMA PARCELA
        // Premissa - Adicionar antes em 'build.grandle (.app):
        // compile 'com.github.rtoshiro.mflibrary:mflibrary:1.0.0' + clicar em 'Sync'
        SimpleMaskFormatter smf3 = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw3 = new MaskTextWatcher(edtDataUltimaParcela, smf3);
        edtDataUltimaParcela.addTextChangedListener(mtw3);
        // Criação de máscara para o campo DATA DA ULTIMA PARCELA
        ////

    }

    // Função para validar o preenchimento do campos
    private boolean valida_campos(View v) {

        lancamento = new Lancamento();

        String strValorCompra;
        String strNovoValorCompra;
        String strNovoValorCompra2;
        String strNovoValorParcela;
        String strNovoValorParcela2;
        String cValorPorParcela;

        int intNumDias;

        try {
            DecimalFormat df = new DecimalFormat("0.00");
            strValorCompra = edtValorTotalCompra.getText().toString();
            strNovoValorCompra = strValorCompra.replaceAll("\\.", "");
            strNovoValorCompra = strNovoValorCompra.replaceAll(",", ".");
            strNovoValorCompra2 = strNovoValorCompra.substring(1);

            if (edtNumParcelas.getText().length() == 0) {
                msg_Snackbar(v, getString(R.string.msg_informe_num_parcelas));
                return false;
            } else if (edtDataCompra.getText().length() == 0) {
                msg_Snackbar(v, getString(R.string.msg_informe_data_compra));
                return false;
            } else if (edtValorTotalCompra.getText().length() == 0) {
                msg_Snackbar(v, getString(R.string.msg_valor_compra));
                return false;
            } else if (edtDescricaoCompra.getText().length() == 0) {
                msg_Snackbar(v, getString(R.string.msg_descricao_compra));
                return false;
            } else if (edtDataPrimeiraParcela.getText().length() == 0) {
                msg_Snackbar(v, getString(R.string.msg_informe_data_primeira_parcela));
                return false;
            } else {
                intNumParcelas = Integer.parseInt(edtNumParcelas.getText().toString());
                nValorCompra = Float.parseFloat(strNovoValorCompra2);
                dtPrimeiraParcela = edtDataPrimeiraParcela.getText().toString();
            }

        if (intNumParcelas==0) {
            msg_Snackbar(v, getString(R.string.msg_parcelas_1_a_99));
            return false;
        } else if (nValorCompra==0) {
            msg_Snackbar(v, getString(R.string.msg_valor_compra_maior_zero));
            return false;
        }

        } catch(Exception ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        // Grava nas variáveis o valor de cada campo, para ser inserido no banco.
        try {
            lancamento.data_compra   = edtDataCompra.getText().toString();
            lancamento.descricao     = edtDescricaoCompra.getText().toString();
            lancamento.data_1_parc   = dtPrimeiraParcela;
            lancamento.data_ult_parc = edtDataUltimaParcela.getText().toString();
            lancamento.parcelas      = intNumParcelas;
            lancamento.valor_compra  = nValorCompra;
            lancamento.num_cartao    = CadCartaoActivity.strPublicNumCartao;

            // Formata o campo do valor de cada parcela.
            DecimalFormat df = new DecimalFormat("0.00");
            cValorPorParcela = df.format(nValorCompra / intNumParcelas );
            edtValorPorParcela.setText(cValorPorParcela);

            if (edtValorPorParcela.getText().length() > 0) {
                strNovoValorParcela  = edtValorPorParcela.getText().toString().replaceAll("\\.", "");
                strNovoValorParcela  = strNovoValorParcela.replaceAll(",", ".");
                strNovoValorParcela2 = strNovoValorParcela.substring(1);
                lancamento.valor_parc = Float.parseFloat(strNovoValorParcela2);
            }

        } catch(Exception ex) {
            msg_Snackbar(v, ex.getMessage());
        }
        //

        return true;
    }

    // Função de mensagem usando SNACKBAR
    private void msg_Snackbar(View v, String msg) {
        Snackbar.make(v, msg, Snackbar.LENGTH_LONG).setAction("OK", null).show();
    }

    private void confirmar(View v) {

        // lancamento = new Lancamento();

        if (valida_campos(v)) {
            lancamento.codigo = intCodigoLanc;
            try {
                if (lancamento.codigo==0) {
                    lancamentoRepositorio.inserir_lanc(lancamento);
                    msg_Snackbar(v, getString(R.string.msg_lancamento_sucesso));
                    ActivityListaLancamentos.atualizarListaLanc();
                    limparCampos();
                } else {
                    lancamentoRepositorio.alterar_lanc(lancamento);
                    msg_Snackbar(v, getString(R.string.msg_alteracao_lanc_sucesso));
                    ActivityListaLancamentos.atualizarListaLanc();
                    Intent i = new Intent(ActivityLancamentos.this, ActivityListaLancamentos.class);
                    startActivity(i);
                    finish();
                }
            } catch(Exception ex) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle( R.string.msg_erro_conexao );
                dlg.setMessage( ex.getMessage());
                dlg.setNeutralButton(R.string.msg_ok, null);
                dlg.show();
            }
        }
    }

    private void criarConexaoDb() {
        try {
            dadosOpenHelperLancamentos = new DadosOpenHelperLancamentos(this);
            conexao_db = dadosOpenHelperLancamentos.getWritableDatabase();
            //Snackbar.make(layoutContentCadCartao, R.string.msg_conexao_sucesso, Snackbar.LENGTH_LONG)
            //        .setAction("OK", null).show();
            lancamentoRepositorio = new LancamentoRepositorio(conexao_db);
        } catch(Exception ex) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle( R.string.msg_erro_conexao );
            dlg.setMessage( ex.getMessage());
            dlg.setNeutralButton(R.string.msg_ok, null);
            dlg.show();
        }
    }

    private void recebeParametro() {

        String cValorCompra;
        String cValorPorParcela;

        // Recupera o parâmetro enviado pelo 'CartoesAdapter'
        Bundle bundle = getIntent().getExtras();
        lancamento = new Lancamento();
        if ( (bundle != null) && (bundle.containsKey("LANCAMENTO")) ) {

            lancamento = (Lancamento) bundle.getSerializable("LANCAMENTO");

            nValorCompra  = lancamento.valor_compra;
            intNumParcelas = lancamento.parcelas        ;

            intCodigoLanc = lancamento.codigo;
            edtDataCompra.setText(lancamento.data_compra);
            edtDescricaoCompra.setText(lancamento.descricao);
            // edtValorTotalCompra.setText(Float.toString(lancamento.valor_compra));
            edtNumParcelas.setText(Integer.toString(lancamento.parcelas));
            edtDataPrimeiraParcela.setText(lancamento.data_1_parc);
            edtDataUltimaParcela.setText(lancamento.data_ult_parc);

            // Formata o campo do valor de cada parcela e da compra.
            DecimalFormat df = new DecimalFormat("0.00");

            cValorPorParcela = df.format(nValorCompra / intNumParcelas );
            edtValorPorParcela.setText(cValorPorParcela);

            cValorCompra = df.format( nValorCompra );
            edtValorTotalCompra.setText(cValorCompra);
            //

        }
    }

    private void limparCampos() {

        edtValorTotalCompra.setText("");
        edtValorPorParcela.setText("");
        edtDataCompra.setText("");
        edtDataUltimaParcela.setText("");
        edtDataPrimeiraParcela.setText("");
        edtNumParcelas.setText("");
        edtDescricaoCompra.setText("");

        edtDataCompra.requestFocus();

    }

    private void confirmarExclusao(View v) {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("Excluir Cartão");
        //define a mensagem
        builder.setMessage(R.string.msg_confirmacao_exc_lanc);
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                excluirLancamento(v);
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

    private void excluirLancamento(View v) {
        try {
            lancamentoRepositorio.excluir_lanc(lancamento.codigo);
        } catch(Exception ex) {
            msg_Snackbar(v, ex.getMessage());
        }
    }

}