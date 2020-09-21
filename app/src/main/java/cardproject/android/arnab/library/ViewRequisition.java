package cardproject.android.arnab.library;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class ViewRequisition extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    RecyclerView recyclerView;
    ItemAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    String department="123";
    int grade=123;
    Spinner dropdown,dropdown2;
    ArrayList<Item> itemArrayList;
    RelativeLayout waitScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requisition);

        Toolbar toolbar = findViewById(R.id.toolbar);
        dropdown = findViewById(R.id.spinner);
        dropdown2 = findViewById(R.id.spinner2);
        recyclerView = findViewById(R.id.rec);
        waitScreen=findViewById(R.id.waitScreen);


        toolbar.setTitle("Requisition List");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        int resid = R.drawable.libbg;
        ImageView imageView1 = findViewById(R.id.imageView);
        Glide
                .with(this)
                .load(resid).into(imageView1);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(date);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday=dateFormat.format(cal.getTime());

        deleteRequisition(getApplicationContext(),yesterday);


        String[] items = new String[]{"Year", "1st", "2nd", "3rd", "4th"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.spinnertext, items);
        dropdown.setAdapter(adapter2);

        String[] items2 = new String[]{"Stream", "Computer Science", "Electronics and Communications",
                "Information Technology", "Biotechnology"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, R.layout.spinnertext, items2);
        dropdown2.setAdapter(adapter3);
        dropdown.setOnItemSelectedListener(this);
        dropdown2.setOnItemSelectedListener(this);

        makeScreenUnresponsive();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(date);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday=dateFormat.format(cal.getTime());

        deleteRequisition(getApplicationContext(),yesterday);
        getWholeRequisition(getApplicationContext(),grade,department);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
//        String text=parent.getItemAtPosition(position).toString();
//        if(!text.equals("Year")&&!text.equals("Stream"))
//            Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
        if(parent.equals(dropdown))
        {
            int grd[]={123,1,2,3,4};
            grade=grd[position];
            waitScreen.setVisibility(View.VISIBLE);
            makeScreenUnresponsive();
            getWholeRequisition(getApplicationContext(),grade,department);
        }
        else if(parent.equals(dropdown2))
        {
            String dept[]={"123","cse","ece","it","bt"};
            department=dept[position];
            waitScreen.setVisibility(View.VISIBLE);
            makeScreenUnresponsive();
            getWholeRequisition(getApplicationContext(),grade,department);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void deleteRequisition(final Context mContext, String yesterday)
    {
        Response.Listener<String> deleteResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        //Toast.makeText(mContext,"OTP changed",Toast.LENGTH_SHORT).show();
                        ViewRequisition.this.getWholeRequisition(mContext,grade,department);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    //Toast.makeText(mContext,"OTP not changed",Toast.LENGTH_SHORT).show();
                }


            }
        };
        RequestDeleteRequisition requestDeleteRequisition=new RequestDeleteRequisition(yesterday,deleteResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestDeleteRequisition);
    }

    void getWholeRequisition(final Context mContext, int grade, String department)
    {
        Response.Listener<String> classesResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                //erMsg.setText(response);
                itemArrayList = new ArrayList<>();
                waitScreen.setVisibility(View.GONE);
                makeWindowResponsive();
                try
                {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    //Toast.makeText(mContext,success+"",Toast.LENGTH_SHORT).show();
                    if(success)
                    {
                        String msg="";
                        for (String key : iterate(jsonResponse.keys()))
                        {
                            if(!key.equalsIgnoreCase("success"))
                            {
                                itemArrayList.add(new Item(R.drawable.book, key, jsonResponse.optInt(key)+""));

                            }
                        }
                        recyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(mContext);
                        adapter = new ItemAdapter(itemArrayList);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                Intent intent = new Intent(getBaseContext(), IndividualRequisition.class);
                                intent.putExtra("id", itemArrayList.get(position).getMtxt1());
                                intent.putExtra("bookNum", itemArrayList.get(position).getMtxt2());
                                startActivity(intent);
                            }
                        });

                    }
                    else
                        Toast.makeText(mContext,"false",Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                }
            }
        };
        String TOTAL_REQUISITION_URL= String.format("http://arnabbanerjee.dx.am/RequestGetFullReq.php?grade=%1$d&department=%2$s&",
                grade,department);
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(TOTAL_REQUISITION_URL,classesResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
    }

    private <T> Iterable<T> iterate(final Iterator<T> i){
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return i;
            }
        };
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
