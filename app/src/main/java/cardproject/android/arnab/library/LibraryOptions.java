package cardproject.android.arnab.library;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class LibraryOptions extends AppCompatActivity implements View.OnClickListener
{
    Button reqBtn,issueBtn,retBtn;
    ImageView bgImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_options);

        reqBtn=findViewById(R.id.reqBtn);
        issueBtn=findViewById(R.id.issueBtn);
        retBtn=findViewById(R.id.retBtn);
        bgImg = findViewById(R.id.bgImg);

        Glide.with(getApplicationContext()).load(this.getResources().getDrawable(R.drawable.libbg)).into(bgImg);

        reqBtn.setOnClickListener(this);
        issueBtn.setOnClickListener(this);
        retBtn.setOnClickListener(this);

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

    @Override
    public void onClick(View v)
    {
        if(v.equals(reqBtn))
        {
            Intent intent=new Intent(getApplicationContext(),ViewRequisition.class);
            startActivity(intent);
        }
        else if(v.equals(issueBtn))
        {
            Intent intent=new Intent(getApplicationContext(), IssueBooks.class);
            startActivity(intent);
        }
        else if(v.equals(retBtn))
        {
            Intent intent=new Intent(getApplicationContext(), ReturnScanner.class);
            startActivity(intent);
        }

    }
}
