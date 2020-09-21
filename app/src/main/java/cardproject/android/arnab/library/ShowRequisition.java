package cardproject.android.arnab.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ShowRequisition extends AppCompatActivity implements View.OnClickListener
{
    String information;
    long id;
    RelativeLayout srWait,reqMsg;
    LinearLayout ll;
    int numReq=0;
    TextView issueTotal, issueId;
    ArrayList<Long> bookIds;
    Button issue2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_requisition);

        Intent intent=this.getIntent();
        information=intent.getStringExtra("info");
        String temp[]=information.split("@");
        id=Long.parseLong(temp[0]);

        Toolbar toolbar3 = findViewById(R.id.toolbar3);
        srWait=findViewById(R.id.srWait);
        ll= findViewById(R.id.linev);
        issueId=findViewById(R.id.issueId);
        issueTotal=findViewById(R.id.issueTotal);
        issue2=findViewById(R.id.issue2);
        reqMsg=findViewById(R.id.reqMsg);


        toolbar3.setTitle("Issue books");
        toolbar3.setNavigationIcon(R.drawable.back);
        toolbar3.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        int resid = R.drawable.libbg;
        ImageView imageView1 = findViewById(R.id.imageView3);
        Glide
                .with(this)
                .load(resid).into(imageView1);

        issueId.setText(id+"");

        reqMsg.setVisibility(View.GONE);


        getIndivRequisition(getApplicationContext(),id);
        getTotalBooksTaken(getApplicationContext(),id);

        issue2.setOnClickListener(this);
        makeScreenUnresponsive();
    }

    void getIndivRequisition(final Context mContext, long canId)
    {
        //Toast.makeText(mContext,"got:"+canId+"",Toast.LENGTH_SHORT).show();
        Response.Listener<String> classesResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                srWait.setVisibility(View.GONE);
                makeWindowResponsive();
                //erMsg.setText(response);
                try
                {
                    JSONArray array = new JSONArray(response);
                    //Toast.makeText(mContext,"len="+response+"",Toast.LENGTH_SHORT).show();
                    numReq=array.length();
                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject req = array.getJSONObject(i);

                        View v= LayoutInflater.from(mContext).inflate(R.layout.book_item,null);
                        ll.addView(v);
                        TextView name=findViewById(R.id.bookname);
                        TextView auth =findViewById(R.id.bookauth);
                        TextView sub=findViewById(R.id.booksub);
                        EditText id2=findViewById(R.id.bookid);
                        name.setText("Name : "+req.getString("bookName"));
                        auth.setText("Author : "+req.getString("authorName"));
                        sub.setText("Subject : "+req.getString("subjectName"));
                        name.setId(i*10);
                        auth.setId(i*10+1);
                        sub.setId(i*10+2);
                        id2.setId(i*10+3);

                    }
                    if(numReq==0)
                    {
                        reqMsg.setVisibility(View.VISIBLE);
                        issue2.setEnabled(false);
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

    private void getTotalBooksTaken(final Context mContext, final long id)
    {
        Response.Listener<String> classesResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                srWait.setVisibility(View.GONE);
                makeWindowResponsive();
                //erMsg.setText(response);
                try
                {
                    jsonResponse=new JSONObject(response);

                    issueTotal.setText(jsonResponse.getLong("taken")+"");


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                }
            }
        };
        String INDIV_REQUISITION_URL= String.format("http://arnabbanerjee.dx.am/RequestGetTotalBooksTaken.php?id=%1$d",id);
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(INDIV_REQUISITION_URL,classesResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
    }

    @Override
    public void onClick(View v)
    {
        if(v.equals(issue2))
        {
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String fromDate = df.format(date);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, +15);
            String toDate=dateFormat.format(cal.getTime());


            bookIds=new ArrayList<Long>();
            for(int i=0;i<numReq;i++)
            {
                EditText t=findViewById(i*10+3);
                if(!TextUtils.isEmpty(t.getText()))
                {
                    bookIds.add(Long.parseLong(t.getText().toString().trim()));
                }
            }
            issueBooks(getApplicationContext(),bookIds,id,fromDate,toDate);
            srWait.setVisibility(View.VISIBLE);
            makeScreenUnresponsive();
        }
    }

    private void issueBooks(final Context mContext, ArrayList<Long> bookIds, long candidateId, String fromDate, String toDate)
    {
        Response.Listener<String> bookResponseListener=new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                srWait.setVisibility(View.GONE);
                makeWindowResponsive();
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        Toast.makeText(mContext,"Issued",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        Toast.makeText(mContext,"Some books are already issued",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(mContext,response,Toast.LENGTH_LONG).show();
                    finish();
//                    err.setText(e.toString());
                }


            }
        };
        RequestIssueBook requestIssueBook=new RequestIssueBook(bookIds,candidateId,fromDate,toDate,bookResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestIssueBook);
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
