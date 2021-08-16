package com.example.acessoscartoes.dominio.entidades;

import java.io.Serializable;

public class Cartao implements Serializable {
    public int codigo;
    public String nome_cartao;
    public String num_cartao;
    public String vencimento;
    public String cvv;
    public String senha;
    public String melhor_dia;
    public String dia_venc;

    public String strMelhorDia;

    public Cartao() {

    }
}


