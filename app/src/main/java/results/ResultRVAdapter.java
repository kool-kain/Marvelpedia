package results;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.example.dani.marvelpedia.DetailedInfoCharActivity;
import com.example.dani.marvelpedia.DetailedInfoComicActivity;
import com.example.dani.marvelpedia.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import layout.FgCharacters;

/**
 * Created by Dani on 23/12/16.
 */

public class ResultRVAdapter extends RecyclerView.Adapter {
    ArrayList<Result> listaR;
    Context context;
    char parent;
    public final static char parentCh = 'h';
    public final static char parentCo =  'c';

    public ResultRVAdapter(ArrayList<Result> arrayList, Context context, char parent){
        this.listaR = arrayList;
        this.context = context;
        this.parent = parent;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_result,parent,false);
        ResultViewHolder rvh = new ResultViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ResultViewHolder rvh = (ResultViewHolder) holder;
        //Se emplea librería Picasso para cargar imagen
        Picasso.with(context)
                .load(listaR.get(position).getImage())
                .placeholder(R.mipmap.shield_logo)
                .error(R.mipmap.shield_logo)
                .into( rvh.resultImage);

        rvh.resultDescription.setText(listaR.get(position).getDescription());

        //Configura posicion del cardview como su tag
        rvh.cardView.setTag(position);

        setAnimation(rvh.cardView);

        //Se asigna listener para cuando se clicka el cardview
        rvh.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int pulse = (int)v.getTag();
                Bundle extras = new Bundle();
                //Pasamos parametros del personaje/comic (id, nombre/título y descripcion)
                extras.putString(com.example.dani.marvelpedia.MainActivity.ARG_PARAM1,
                        String.valueOf(listaR.get(pulse).getId()));
                extras.putString(com.example.dani.marvelpedia.MainActivity.ARG_PARAM2,
                        listaR.get(pulse).getDescription());
                extras.putString(com.example.dani.marvelpedia.MainActivity.ARG_PARAM3,
                        listaR.get(pulse).getImage());
                Intent intent;

                if (parent == parentCh) {
                    //El adaptador proviene del fragment Characters
                    intent = new Intent(context, DetailedInfoCharActivity.class);
                    intent.putExtras(extras);
                    context.startActivity(intent);
                }

                if (parent == parentCo) {
                    //El adaptador proviene del fragment Comcis
                    intent = new Intent(context, DetailedInfoComicActivity.class);
                    intent.putExtras(extras);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void setAnimation(View viewToanimate){
        Animation animation = AnimationUtils.loadAnimation(context,android.R.anim.slide_in_left);
        viewToanimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return listaR.size();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView resultImage;
        TextView resultDescription;

        public ResultViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardview_result);
            resultImage = (ImageView) itemView.findViewById(R.id.image_result);
            resultDescription = (TextView) itemView.findViewById(R.id.description_result);
        }
    }
}
