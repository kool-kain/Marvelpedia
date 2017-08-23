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
import results.ResultRVComicsAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import utils.DigestUtil;


/**
 * Created by Dani on 4/1/17.
 */

public class DetailedInfoCharActivity extends AppCompatActivity {
    private TextView textView;
    private ImageView imageView;
    private RecyclerView rv;
    private ResultRVComicsAdapter resultRVCA;
    private ArrayList<Result> listaR;
    Context context;
    int idM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_info_characters);

        //Carga de los elementos de la activity que muestra la información detallada del personaje
        imageView = (ImageView) findViewById(R.id.img_det_char);
        textView = (TextView) findViewById(R.id.char_det_descrip);
        rv = (RecyclerView) findViewById(R.id.recycler_det_comics);

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

            //Efectua la llamada a la API de marvel para obtener el personaje y comics indicados por el Id
            callService();
        }
        else{
            //Si no se recuperó correctamente el id entre actividades se informa del problema
            textView.setText("No hay imagen para mostrar");
            Snackbar.make(rv, "Error interno al mostrar detalles del personaje", Snackbar.LENGTH_LONG)
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
        Call<RetrofitMarvelResponse> request = service.getComicsByIdCharacter(idM,com.example.dani.marvelpedia.MainActivity.PUB_API_KEY,timeStamp+"",hash);

        //Con enqueue() añadimos la peticion a la cola de peticiones de Retrofit, que se encargará de gestionar las peticiones y lanzarlas
        request.enqueue(new Callback<RetrofitMarvelResponse>() {

            @Override
            public void onResponse(Response<RetrofitMarvelResponse> response) {
                //El Objeto Response de retrofit que nos devuelve, contiene nuestro Objeto en su Body()
                // lo asignamos a nuestro objeto respuestaMarvel y ya podremos operar con él
                RetrofitMarvelResponse respuestaMarvel=response.body();

                //Obtenemos imagen, nombre y descripción del personaje buscado (primer resultado) y su lista comics
                listaR = new ArrayList<Result>();
                if (!(respuestaMarvel.getStatus().equals(com.example.dani.marvelpedia.MainActivity.RESP_OK)) &&
                        respuestaMarvel.getData() != null){
                    //Recorre y almacena la lista de comics en las que el personaje aparece
                    int size = respuestaMarvel.getData().getResults().size();
                    if (size > 0) {
                        ResultsAPI comics;
                        String textPart, imgLink;
                        for (int i = 0; i < size; i++) {
                            comics = respuestaMarvel.getData().getResults().get(i);
                            textPart = comics.getTitle();
                            imgLink = comics.getThumbnail().getPath() + com.example.dani.marvelpedia.MainActivity.IMAGE_SIZE +
                                    comics.getThumbnail().getExtension();
                            listaR.add(new Result(comics.getId(), imgLink, textPart));
                        }
                    }
                    else{
                        Snackbar.make(rv, "No hay cómics para este personaje" ,
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
        resultRVCA = new ResultRVComicsAdapter(listaR, context);
        GridLayoutManager glm = new GridLayoutManager(context,3);
        rv.setLayoutManager(glm);
        rv.setAdapter(resultRVCA);
    }
}
