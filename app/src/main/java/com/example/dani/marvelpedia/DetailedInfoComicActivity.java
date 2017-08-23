package com.example.dani.marvelpedia;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import model.ResultsAPI;
import model.RetrofitMarvelResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import results.Result;
import results.ResultRVCharactersAdapter;
import results.ResultRVComicsAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import utils.DigestUtil;

/**
 * Created by Dani on 5/1/17.
 */

public class DetailedInfoComicActivity extends AppCompatActivity{
    private TextView textView;
    private ImageView imageView;
    private RecyclerView rv;
    private ResultRVCharactersAdapter resultRVChA;
    private ArrayList<Result> listaR;
    Context context;
    int idM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_info_comic);

        //Carga de los elementos de la activity que muestra la información detallada del personaje
        imageView = (ImageView) findViewById(R.id.img_det_comic);
        textView = (TextView) findViewById(R.id.comic_det_descrip);
        rv = (RecyclerView) findViewById(R.id.recycler_det_chars);

        //se recuperan los argumentos del intent padre para mostrar layout del personaje y llamar a la API
        Bundle extras =  getIntent().getExtras();
        if (extras != null) {
            idM = Integer.parseInt(extras.getString(com.example.dani.marvelpedia.MainActivity.ARG_PARAM1));
            textView.setText(extras.getString(com.example.dani.marvelpedia.MainActivity.ARG_PARAM2));
            //Se emplea librería Picasso para cargar imagen del personaje
            Picasso.with(context)
                    .load(extras.getString(com.example.dani.marvelpedia.MainActivity.ARG_PARAM3))
                    .placeholder(R.drawable.shield_logo)
                    .resize(140,180)
                    .error(R.drawable.shield_logo)
                    .into(imageView);

            //Efectua la llamada a la API de marvel para obtener el comic y personajes asociados indicados por el Id
            callService();
        }
        else{
            //Si no se recuperó correctamente el id entre actividades se informa del problema
            textView.setText("No hay imagen para mostrar");
            Snackbar.make(rv, "Error interno al mostrar detalles del cómic", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public void callService(){
        OkHttpClient client = new OkHttpClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //Creamos un objeto Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(com.example.dani.marvelpedia.MainActivity.URL_BASE) //Le asignaremos la URL Base
                .addConverterFactory(GsonConverterFactory.create()) //Añadimos el GSONConverter para que parsee los datos automaticamente con GSON
                .build();

        //Inicializamos el Service de Retrofit
        CustomRetrofitService service = retrofit.create(CustomRetrofitService.class);
        //Obtenemos los parametros necesarios para autenticarnos en la API de Marvel
        //Primero el TimeStamp del momento actual en milisegundos
        long timeStamp = System.currentTimeMillis();
        //Despues generamos el hash a partir del timestamp y las claves publico y privadas
        String hash= DigestUtil.md5(timeStamp +  com.example.dani.marvelpedia.MainActivity.PRIV_API_KEY +
                com.example.dani.marvelpedia.MainActivity.PUB_API_KEY);
        //Creamos e inicializamos una nueva petición para obtener el tiempo enviando nombre personaje introducido y "name" para que ordene los resultados por nombre
        Call<RetrofitMarvelResponse> request = service.getCharactersByIdComic(idM,com.example.dani.marvelpedia.MainActivity.PUB_API_KEY,timeStamp+"",hash);

        //Con enqueue() añadimos la peticion a la cola de peticiones de Retrofit, que se encargará de gestionar las peticiones y lanzarlas
        request.enqueue(new Callback<RetrofitMarvelResponse>() {

            @Override
            public void onResponse(Response<RetrofitMarvelResponse> response) {
                //El Objeto Response de retrofit que nos devuelve, contiene nuestro Objeto en su Body()
                // lo asignamos a nuestro objeto respuestaMarvel y ya podremos operar con él
                RetrofitMarvelResponse respuestaMarvel=response.body();

                listaR = new ArrayList<Result>();
                if (!(respuestaMarvel.getStatus().equals(com.example.dani.marvelpedia.MainActivity.RESP_OK)) &&
                        respuestaMarvel.getData() != null){
                    //Recorre y almacena la lista de personajes del comic
                    int size = respuestaMarvel.getData().getResults().size();
                    if (size > 0) {
                        String textPart, imgLink;
                        ResultsAPI character;
                        for (int i = 0; i < size; i++) {
                            character = respuestaMarvel.getData().getResults().get(i);
                            textPart = character.getName();
                            imgLink = character.getThumbnail().getPath() + com.example.dani.marvelpedia.MainActivity.IMAGE_SIZE +
                                    character.getThumbnail().getExtension();
                            listaR.add(new Result(character.getId(), imgLink, textPart));
                        }
                    }
                    else{
                        Snackbar.make(rv, "No hay personajes registrados para este cómic" ,
                                Snackbar.LENGTH_LONG).show();
                    }
                }
                else{
                    Snackbar.make(rv, "Su consulta no ha podido ser procesada" ,
                            Snackbar.LENGTH_LONG).show();
                }
                showResults();

            }

            @Override
            public void onFailure(Throwable t) {
                listaR = new ArrayList<Result>();
                //En caso de que la petición falle pintamos en el log la traza del error y mostramos un Snackbar informativo para el usuario
                t.printStackTrace();
                Snackbar.make(rv, "No se ha podido conectar", Snackbar.LENGTH_LONG)
                        .show();
                showResults();
            }
        });
    }

    public void showResults(){
        //Carga la lista de resultados obtenidos para mostrarlos en pantalla
        resultRVChA= new ResultRVCharactersAdapter(listaR, context);
        GridLayoutManager glm = new GridLayoutManager(context,3);
        rv.setLayoutManager(glm);
        rv.setAdapter(resultRVChA);
    }
}
