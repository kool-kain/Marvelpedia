package results;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.dani.marvelpedia.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dani on 5/1/17.
 */

public class ResultRVComicsAdapter extends RecyclerView.Adapter{
    ArrayList<Result> listaR;
    Context context;

    public ResultRVComicsAdapter(ArrayList<Result> arrayList, Context context){
        this.listaR = arrayList;
        this.context = context;
    }


    @Override
    public ResultComicsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_detailed_comic,parent,false);
        ResultComicsViewHolder rvh = new ResultComicsViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ResultComicsViewHolder rvh = (ResultComicsViewHolder) holder;
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

                //Al pulsar en el cardview del personaje, se muestra el nombre del mismo
                Snackbar.make(v, listaR.get(pulse).getDescription(), Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return listaR.size();
    }

    public static class ResultComicsViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView resultImage;

        public ResultComicsViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardview_detailed_comic);
            resultImage = (ImageView) itemView.findViewById(R.id.detailed_img_comic);
        }
    }
}
