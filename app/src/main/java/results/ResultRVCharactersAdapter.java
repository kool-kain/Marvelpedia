package results;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dani.marvelpedia.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dani on 13/1/17.
 */

public class ResultRVCharactersAdapter extends RecyclerView.Adapter {
    ArrayList<Result> listaR;
    Context context;

    public ResultRVCharactersAdapter(ArrayList<Result> arrayList, Context context){
        this.listaR = arrayList;
        this.context = context;
    }


    @Override
    public ResultRVCharactersAdapter.ResultCharactersViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_detailed_char,parent,false);
        ResultRVCharactersAdapter.ResultCharactersViewHolder rvh
                = new ResultRVCharactersAdapter.ResultCharactersViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ResultRVCharactersAdapter.ResultCharactersViewHolder rvh
                = (ResultRVCharactersAdapter.ResultCharactersViewHolder) holder;
        //Se emplea librería Picasso para cargar imagen
        Picasso.with(context)
                .load(listaR.get(position).getImage())
                .placeholder(R.mipmap.shield_logo)
                .error(R.mipmap.shield_logo)
                .into( rvh.resultImage);

        //Configura posicion del cardview como su tag
        rvh.cardView.setTag(position);

        //Se asigna listener para cuando se clicka el cardview
        rvh.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int pulse = (int)v.getTag();

                //Al pulsar en el cardview del comic, se muestra el titulo del mismo
                Snackbar.make(v, listaR.get(pulse).getDescription(), Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return listaR.size();
    }

    public static class ResultCharactersViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView resultImage;

        public ResultCharactersViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardview_detailed_char);
            resultImage = (ImageView) itemView.findViewById(R.id.detailed_img_char);
        }
    }
}
