package cardproject.android.arnab.library;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TakeReturn extends AppCompatActivity implements View.OnClickListener
{
    String information="";
    RelativeLayout retWait;
    long candidateId;
    LinearLayout ll;
    Button ret2;
    ArrayList<Long> bookIds;
    int numTaken=0;
    TextView idVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_return);

        Intent intent=this.getIntent();
        information=intent.getStringExtra("info");

        Toolbar toolbar3 = findViewById(R.id.toolbar4);
        retWait=findViewById(R.id.retWait);
        ll= findViewById(R.id.linel);
        ret2=findViewById(R.id.ret2);
        idVal=findViewById(R.id.idVal);


        String str[]=information.split("@");
        candidateId =Long.parseLong(str[0].trim());

        idVal.setText(candidateId+"");


        toolbar3.setTitle("Return books");
        toolbar3.setNavigationIcon(R.drawable.back);
        toolbar3.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        int resid = R.drawable.libbg;
        ImageView imageView1 = findViewById(R.id.imageView4);
        Glide
                .with(this)
                .load(resid).into(imageView1);

        getTakenBooks(getApplicationContext(),candidateId);

        ret2.setOnClickListener(this);

        makeScreenUnresponsive();

    }

    void getTakenBooks(final Context mContext, long canId)
    {
        //Toast.makeText(mContext,"got:"+canId+"",Toast.LENGTH_SHORT).show();
        Response.Listener<String> classesResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                retWait.setVisibility(View.GONE);
                makeWindowResponsive();
                //erMsg.setText(response);
                try
                {
                    JSONArray array = new JSONArray(response);
                    //Toast.makeText(mContext,"len="+response+"",Toast.LENGTH_SHORT).show();
                    numTaken=array.length();
                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject req = array.getJSONObject(i);

                        View v = LayoutInflater.from(mContext).inflate(R.layout.return_item, null);
                        ll.addView(v);
//                        TextView name = findViewById(R.id.bookname2);
//                        TextView auth = findViewById(R.id.bookauth2);
//                        TextView sub = findViewById(R.id.booksub2);
                        TextView id=findViewById(R.id.bookid2);
                        CheckBox checkBox=findViewById(R.id.checkBox);
//                        name.setText("Name : " + dummybooks[i]);
//                        auth.setText("Author : " + dummyauths[i]);
//                        sub.setText("Subject : " + dummysub[i]);
                        id.setText("Id: "+req.getLong("bookId"));
//                        name.setId(i*10);
//                        auth.setId(i*10+1);
//                        sub.setId(i*10+2);
                        id.setId(i*10+3);
                        checkBox.setId(i*10+4);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                }
            }
        };
        String INDIV_REQUISITION_URL= String.format("http://arnabbanerjee.dx.am/RequestGetTakenBooks.php?id=%1$d",canId);
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(INDIV_REQUISITION_URL,classesResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
    }

    @Override
    public void onClick(View v)
    {
        if(v.equals(ret2))
        {
            bookIds=new ArrayList<Long>();
            for(int i=0;i<numTaken;i++)
            {
                CheckBox t=findViewById(i*10+4);
                TextView txt=findViewById(i*10+3);
                if(t.isChecked())
                {
                    bookIds.add(Long.parseLong(txt.getText().toString().trim().substring(4)));
                }
            }
            returnBooks(getApplicationContext(),bookIds);
            retWait.setVisibility(View.VISIBLE);
            makeScreenUnresponsive();
        }
    }

    private void returnBooks(final Context mContext, ArrayList<Long> bookIds)
    {
        Response.Listener<String> bookResponseListener=new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                retWait.setVisibility(View.GONE);
                makeWindowResponsive();
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        Toast.makeText(mContext,"Got Return",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    finish();
//                    err.setText(e.toString());
                }


            }
        };
        RequestReturnBook requestReturnBook=new RequestReturnBook(bookIds,bookResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestReturnBook);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void makeScreenUnresponsive()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void makeWindowResponsive()
    {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
