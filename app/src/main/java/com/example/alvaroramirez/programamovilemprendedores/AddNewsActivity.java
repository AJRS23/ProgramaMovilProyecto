package com.example.alvaroramirez.programamovilemprendedores;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.InputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNewsActivity extends AppCompatActivity {

    EditText edtContent;
    EditText edtName;
    Button btnAdd;
    ImageButton btnImage;
    //ImageView img_post;

    Date date1 = new Date();
    String date = "";


    Intent intent;
    private static int IMG_RESULT = 1;
    String picturePath;
    Bitmap bitmap;

    InputStream in = null;
    String photo = "";
    String photo_thumbnail = "";

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        edtContent = findViewById(R.id.edt_contentNews);
        btnAdd = findViewById(R.id.btn_addNew);
        edtName = findViewById(R.id.edt_nameNews);



    }

    public void addClicked(View view){

        if (!TextUtils.isEmpty(edtContent.getText()) && ! TextUtils.isEmpty(edtName.getText())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date date1 = new Date();

            date = dateFormat.format(date1);

            ExecutePost executePost = new ExecutePost(edtName.getText().toString(),
                    edtContent.getText().toString(), date );

            executePost.execute();


        }
        else {
            edtContent.setError("Campo Requerido");
            edtName.setError("Campo Requerido");
        }
    }

    public class ExecutePost extends AsyncTask<String,Void,String> {
        private String title;
        private String idCommunity = "2";
        private String description;
        private String photo = ""; //Url image
        private String date;
        private boolean approved = true;
        private boolean isPosted = false;

        public ExecutePost(String title, String description,String date) {

            this.title = title;
            this.description = description;
            this.date = date;

        }



        @Override
        protected String doInBackground(String... strings) {


            API_Access api = API_Access.getInstance();
            String[] keys = {"idCommunity","title", "description", "date","photo","approved"};
            String[] values = {idCommunity,title,description,date,photo,Boolean.toString(approved)};
            isPosted = api.post_put_base(keys,values,0,"POST",0);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isPosted) {

                Toast.makeText(AddNewsActivity.this, "Publicacion exitosa", Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
                intent.putExtra("idCommunity",CommunityActivity.idCommunity);
                intent.putExtra("nameCommunity",CommunityActivity.nameCommunity);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                finish();
            } else
                Toast.makeText(AddNewsActivity.this, "Publicacion fallida", Toast.LENGTH_SHORT).show();

        }
    }


}

