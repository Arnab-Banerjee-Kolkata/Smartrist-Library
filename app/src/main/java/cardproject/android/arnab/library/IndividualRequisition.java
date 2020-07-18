package cardproject.android.arnab.library;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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

public class IndividualRequisition extends AppCompatActivity
{
    ArrayList<String> bookNames, authors, subjects;
    long candidateId=0;
    LinearLayout l;
    RelativeLayout waitScreen2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_requisition);

        Toolbar toolbar2=findViewById(R.id.toolbar2);
        l=findViewById(R.id.line);
        waitScreen2=findViewById(R.id.waitSCreen2);


        toolbar2.setTitle("Requisition");
        toolbar2.setNavigationIcon(R.drawable.back);
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        int resid=R.drawable.libbg;
        ImageView imageView1=findViewById(R.id.imageView2);
        Glide
                .with(this)
                .load(resid).into(imageView1);
        String bookNum =getIntent().getStringExtra("bookNum");
        candidateId =Long.parseLong(getIntent().getStringExtra("id").trim());
        //Toast.makeText(this,getIntent().getStringExtra("id"),Toast.LENGTH_SHORT).show();
        LinearLayout linearLayout=findViewById(R.id.ll);
        TextView textView=findViewById(R.id.id);
        textView.setText(bookNum+"");
        TextView textView2=findViewById(R.id.name);
        textView2.setText(candidateId+"");

        getIndivRequisition(getApplicationContext(),candidateId);

        makeScreenUnresponsive();
    }

    void getIndivRequisition(final Context mContext,long canId)
    {
        //Toast.makeText(mContext,"got:"+canId+"",Toast.LENGTH_SHORT).show();
        Response.Listener<String> classesResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                waitScreen2.setVisibility(View.GONE);
                makeWindowResponsive();
                //erMsg.setText(response);
                try
                {
                    JSONArray array = new JSONArray(response);
                    //Toast.makeText(mContext,"len="+response+"",Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject req = array.getJSONObject(i);

                        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item,null);
                        TextView t1 =new TextView(mContext);
                        t1.setText((i+1)+".\nSubject:\t"+req.getString("subjectName")+"\nBook:\t"+"\""+
                                req.getString("bookName")+"\""+"\nAuthor:\t"+req.getString("authorName"));
                        //Toast.makeText(mContext,i+"",Toast.LENGTH_SHORT).show();
                        t1.setTextColor(getResources().getColor(android.R.color.white));
                        t1.setTextSize(15);
                        Typeface font = Typeface.createFromAsset(getAssets(),"century.ttf");
                        t1.setTypeface(font);
                        t1.setPadding(0,5,20,5);
                        l.addView(v);
                        LinearLayout l2=findViewById(R.id.linear);
                        l2.setId(i);
                        l2.addView(t1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                }
            }
        };
        String INDIV_REQUISITION_URL= String.format("http://arnabbanerjee.dx.am/RequestGetIndivReq.php?id=%1$d",canId);
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(INDIV_REQUISITION_URL,classesResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
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
