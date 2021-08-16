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

import com.example.acessoscartoes.dominio.entidades.Cartao;

import java.util.List;

public class CartoesAdapter extends RecyclerView.Adapter<CartoesAdapter.ViewHolderCartao> {

    private List<Cartao> dados;

    public CartoesAdapter(List<Cartao> dados) {
        this.dados = dados;
    }

    @NonNull
    @Override
    public CartoesAdapter.ViewHolderCartao onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.linha_cartoes, parent, false);

        ViewHolderCartao holderCartao = new ViewHolderCartao(view, parent.getContext());

        return holderCartao;

    }

    @Override
    public void onBindViewHolder(@NonNull CartoesAdapter.ViewHolderCartao holder, int position) {
        String cNumCartao;
        int nTamNumCartao;
        if ( (dados != null) && (dados.size() > 0) ) {
            Cartao cartao = dados.get(position);
            cNumCartao = cartao.num_cartao;
            nTamNumCartao = cNumCartao.length();
            if (nTamNumCartao >= 4) {
                holder.txtNomeCartao.setText(cartao.nome_cartao + "   **** " + cNumCartao.substring(nTamNumCartao - 4, nTamNumCartao));
            } else {
                holder.txtNomeCartao.setText(cartao.nome_cartao + "   **** " + cNumCartao);
            }
            holder.txtMelhorDiaCompra.setText("Melhor dia: " + cartao.melhor_dia);
        }

    }

    @Override
    public int getItemCount() {
        return dados.size();
    }

    public class ViewHolderCartao extends RecyclerView.ViewHolder{

        public TextView txtNomeCartao;
        public TextView txtMelhorDiaCompra;

        public ViewHolderCartao(@NonNull View itemView, final Context context) {
            // Usando 'final' por que é um objeto anônimo.

            super(itemView);

            txtNomeCartao      = (TextView) itemView.findViewById(R.id.txtNomeCartao);
            txtMelhorDiaCompra = (TextView) itemView.findViewById(R.id.txtMelhorDiaCompra);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (dados.size() > 0) {
                        Cartao cartao = dados.get(getLayoutPosition());    // Retorna a posição do item selecionado na lista.
                        // Toast.makeText(context, cartao.nome_cartao, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(context, CadCartaoActivity.class);
                        i.putExtra("CARTAO", cartao);
                        ((AppCompatActivity)context).startActivityForResult(i, 0);
                    }
                }
            });
        }
    }
}
