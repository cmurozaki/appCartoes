package com.example.acessoscartoes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acessoscartoes.dominio.entidades.Lancamento;

import java.text.DecimalFormat;
import java.util.List;

public class LancamentosAdapter extends RecyclerView.Adapter<LancamentosAdapter.ViewHolderLancamento> {


    private List<Lancamento> dados;

    public LancamentosAdapter(List<Lancamento> dados) {
        this.dados = dados;
    }

    @NonNull
    @Override
    public LancamentosAdapter.ViewHolderLancamento onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.linha_lancamentos, parent, false);

        ViewHolderLancamento holderLancamento = new ViewHolderLancamento(view, parent.getContext());

        return holderLancamento;

    }

    @Override
    public void onBindViewHolder(@NonNull LancamentosAdapter.ViewHolderLancamento holder, int position) {

        String strDataUltParcela;
        String strDescricaoCompra;
        String strValorParcela;
        String strNumParcelas;
        String strDataCompra;

        if ( (dados != null) && (dados.size() > 0) ) {

            Lancamento lancamento = dados.get(position);

            DecimalFormat df = new DecimalFormat("0.00");

            strDataUltParcela  = lancamento.data_ult_parc;
            strDescricaoCompra = lancamento.descricao;
            strValorParcela    = df.format(lancamento.valor_parc);
            strNumParcelas     = Integer.toString(lancamento.parcelas);
            strDataCompra      = lancamento.data_compra;

            holder.txtDataUltimaParcela.setText(strDataUltParcela + "   " + strDescricaoCompra + "   - Parcelas: " + strNumParcelas);
            holder.txtDescricaoCompra.setText("Parc.R$: " + strValorParcela + "  - Compra: " + strDataCompra);

        }
    }

    @Override
    // Determina o número de itens a ser exibido na lista.
    public int getItemCount() {
        return dados.size();
    }

    public class ViewHolderLancamento extends RecyclerView.ViewHolder {

        public TextView txtDataUltimaParcela;
        public TextView txtDescricaoCompra;

        public ViewHolderLancamento(@NonNull View itemView, final Context context) {

            super(itemView);

            txtDataUltimaParcela = (TextView) itemView.findViewById(R.id.txtDataUltimaParcela);
            txtDescricaoCompra   = (TextView) itemView.findViewById(R.id.txtDescricaoCompra);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Lancamento lancamento = dados.get(getLayoutPosition());
                    Intent i = new Intent(context, ActivityLancamentos.class);
                    i.putExtra( "LANCAMENTO", lancamento );
                    ((AppCompatActivity) context).startActivityForResult(i, 0);
                    ((AppCompatActivity) context).finish();
                    // Toast.makeText(context, "Escolheu", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
