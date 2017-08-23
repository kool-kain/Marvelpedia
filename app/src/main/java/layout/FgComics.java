package layout;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.example.dani.marvelpedia.CustomRetrofitService;
import com.example.dani.marvelpedia.R;

import java.util.ArrayList;

import model.ResultsAPI;
import model.RetrofitMarvelResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import results.Result;
import results.ResultRVAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import utils.DigestUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class FgComics extends Fragment {

    private TextInputLayout characterWrapper;
    private RecyclerView rv;
    private ResultRVAdapter resultVA;
    private ArrayList<Result> listaR;
    private Context context;
    public static char salto ='\n';

    public FgComics() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla layout y carga elementos necesarios. Se activa escucha de botón
        View view =  inflater.inflate(R.layout.fragment_fg_comics, container, false);
        Button button = (Button) view.findViewById(R.id.search_comic);
        characterWrapper = (TextInputLayout) view.findViewById(R.id.text_comic);
        rv = (RecyclerView) view.findViewById(R.id.recycler_comic);
        context = container.getContext();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cierra el teclado virtual para tener visualización correcta de los resultados
                characterWrapper.getEditText().clearFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

                //Busca personaje en API Marvel y muestra listado con el Adapter
                String input = characterWrapper.getEditText().getText().toString();
                callService(input);
            }
        });

        return view;
    }

    public void callService(final String title){
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
        Call<RetrofitMarvelResponse> request = service.getComicByTitle(title,
                com.example.dani.marvelpedia.MainActivity.PUB_API_KEY,timeStamp+"",hash);

        //Con enqueue() añadimos la peticion a la cola de peticiones des Retrofit, que se encargará de gestionar las peticiones y lanzarlas
        request.enqueue(new Callback<RetrofitMarvelResponse>() {

            @Override
            public void onResponse(Response<RetrofitMarvelResponse> response) {
                //El Objeto Response de retrofit que nos devuelve, contiene nuestro Objeto en su Body()
                // lo asignamos a nuestro objeto respuestaMarvel y ya podremos operar con él
                RetrofitMarvelResponse respuestaMarvel=response.body();

                listaR = new ArrayList<Result>();
                if (!(respuestaMarvel.getStatus().equals(com.example.dani.marvelpedia.MainActivity.RESP_OK)) &&
                        respuestaMarvel.getData() != null){
                    int size = respuestaMarvel.getData().getCount();
                    if (size > 0 ) {
                        String textPart, imgLink;
                        ResultsAPI comics;
                        //Recorreremos la lista de personajes y obtenemos imagen y su nombre para añadirlo a la lista resultados
                        for (int i = 0; i < size; i++) {
                            comics = respuestaMarvel.getData().getResults().get(i);
                            textPart = comics.getTitle() + salto + salto + comics.getDescription();
                            imgLink = comics.getThumbnail().getPath() + com.example.dani.marvelpedia.MainActivity.IMAGE_SIZE +
                                    comics.getThumbnail().getExtension();
                            listaR.add(new Result(comics.getId(), imgLink, textPart));
                        }
                    }
                    else{
                        Snackbar.make(rv, "Ningún cómic encontrado" ,
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
                //En caso de que la petición falle pintamos en el log la traza del error y mostramos un Snackbar informativo para el usuario
                t.printStackTrace();
                listaR = new ArrayList<Result>();
                Snackbar.make(rv, "No se ha podido conectar con la base de datos", Snackbar.LENGTH_LONG)
                        .show();
                showResults();
            }
        });
    }

    public void showResults(){
        //Carga la lista de resultados obtenidos para mostrarlos en pantallas
        resultVA = new ResultRVAdapter(listaR, context, ResultRVAdapter.parentCo);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);
        rv.setAdapter(resultVA);
    }


}
