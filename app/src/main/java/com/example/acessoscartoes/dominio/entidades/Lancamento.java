package com.example.acessoscartoes.dominio.entidades;

import java.io.Serializable;
import java.util.Date;

public class Lancamento implements Serializable {
    public int    codigo;
    public String data_compra;
    public String descricao;
    public Float  valor_compra;
    public int    parcelas;
    public String data_1_parc;
    public Float  valor_parc;
    public String data_ult_parc;
    public String num_cartao;

    public Lancamento() {

    }
}
