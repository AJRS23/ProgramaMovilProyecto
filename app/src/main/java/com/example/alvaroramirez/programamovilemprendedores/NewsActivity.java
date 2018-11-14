package com.example.alvaroramirez.programamovilemprendedores;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class NewsActivity extends AppCompatActivity {

    ListView lvNews;

    public static ArrayList<News> listNews = new ArrayList<News>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNewsActivity.class);
                //intent.putExtra("isSubcommunity", false);
                startActivity(intent);
            }
        });

        lvNews = findViewById(R.id.lvNews);

        ExecuteGetNews executeGetNews = new ExecuteGetNews();
        executeGetNews.execute();
    }

    @Override
    public void onResume() {

            ExecuteGetNews executeGetNews = new ExecuteGetNews();
            executeGetNews.execute();


        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void cargarNews(JSONObject jsonResult) {

        try {
            listNews.clear();

            JSONArray jsonNewsList  = jsonResult.getJSONArray("news");       //Importante

            for (int i = 0; i < jsonNewsList.length(); i++) {
                JSONObject jsonNew = (JSONObject) jsonNewsList.get(i);

                HttpGetBitmap request = new HttpGetBitmap();
                Bitmap newImage = null;
                try {
                    newImage = request.execute(jsonNew.getString("photo")).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if(newImage == null){
                    newImage = BitmapFactory.decodeResource( getApplicationContext().getResources(),
                            R.drawable.ic_launcher_background);
                }


                News newObject = new News(jsonNew.getInt("id"),
                        jsonNew.getString("title"), jsonNew.getString("description"), jsonNew.getString("date"),
                        jsonNew.getString("photo"),newImage,jsonNew.getBoolean("approved"));

                listNews.add(newObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvNews.setAdapter(new NewsAdapter());


    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class NewsAdapter extends BaseAdapter {

        public NewsAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return listNews.size();
        }

        @Override
        public Object getItem(int i) {
            return listNews.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.news_list_item, null);
            }


            final EditText txtTitle = view.findViewById(R.id.txtTitleNew);

            ImageView imgImageNew = view.findViewById(R.id.img_New);
            final EditText txtContent = view.findViewById(R.id.txtDescription);

            Button btnDeleteNews = view.findViewById(R.id.btn_EliminarNew);

            Button btnEditNews = view.findViewById(R.id.btn_Editar);


            final int idActual = listNews.get(i).getId();

            final String titleN = listNews.get(i).getTitle();

            //final String dateN = listNews.get(i).getDate().toString();

            final String description = listNews.get(i).getDescription();

            final Bitmap photoNews = listNews.get(i).getPhoto();

            final boolean isApprovedNews = listNews.get(i).isApproved();




            btnDeleteNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExecuteDeleteNews executeDeleteNews = new ExecuteDeleteNews(idActual);
                    executeDeleteNews.execute();

                }
            });

            btnEditNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExecuteEditNews executeEditNews = new ExecuteEditNews(idActual,txtTitle.getText().toString()
                            ,txtContent.getText().toString());
                    executeEditNews.execute();

                }
            });


            txtTitle.setText(titleN);
            txtContent.setText(description);


            if(listNews.get(i).getPhoto() == null)
                imgImageNew.setVisibility(View.GONE);

            imgImageNew.setImageBitmap(photoNews);

            //txtDate.setText(dateN);

            return view;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetNews extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        boolean isApproved = false;

        ExecuteGetNews(){

        }

        ExecuteGetNews(boolean status){
            this.isApproved = status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //rlLoaderEmisoras.setVisibility(View.VISIBLE);
            //rlLogin.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();

            String[] keys = {"id", "idUser"};
            String[] values = {"2", "1"};
            isOk = api.get_delete_base(keys, values, 1, "GET", 1);



            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarNews(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener las difusiones";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class ExecuteDeleteNews extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int id;



        ExecuteDeleteNews(int id){
            this.id = id;
        }


        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"id"};
            String[] values = {Integer.toString(id)};
            isOk = api.get_delete_base(keys, values, 3, "DELETE", 1);



            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                ExecuteGetNews executeGetNews = new ExecuteGetNews();
                executeGetNews.execute();



                Toast.makeText(getApplicationContext(), "Difusion eliminada", Toast.LENGTH_SHORT).show();

            }else{
                String mensaje = "Error al eliminar";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteEditNews extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        boolean isApproved = false;

        String id, title, description;



        ExecuteEditNews(int id, String title, String description){
            this.id = Integer.toString(id);
            this.title = title;
            this.description = description;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //rlLoaderEmisoras.setVisibility(View.VISIBLE);
            //rlLogin.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();

            String[] keys = {"id", "title", "description"};
            String[] values = {id, title,description};
            isOk = api.post_put_base(keys, values, 4, "PUT", 1);



            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                ExecuteGetNews executeGetNews = new ExecuteGetNews();
                executeGetNews.execute();

                String mensaje = "Difusion editada";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }else{
                String mensaje = "Error al editar la difusion";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
