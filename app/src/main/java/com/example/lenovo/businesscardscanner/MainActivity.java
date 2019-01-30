package com.example.lenovo.businesscardscanner;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ViewPager slidepager;
    private LinearLayout dotlayout;
    private SliderAdapter sp;

    private TextView[] mdots;

    int x;
    private Button btnprev;
    private Button btnnext;

    private int mCurrentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slidepager = (ViewPager) findViewById(R.id.pager);
        dotlayout = (LinearLayout) findViewById(R.id.slider);

        btnprev = (Button) findViewById(R.id.btnprev);
        btnnext = (Button) findViewById(R.id.btnnext);

        sp = new SliderAdapter(this);
        slidepager.setAdapter(sp);

        addDotsIndicator(0);

        slidepager.addOnPageChangeListener(viewListener);
    }
    public void addDotsIndicator(int position){
        mdots = new TextView[3];

        for(int i=0; i<mdots.length; i++){
            mdots[i] = new TextView(this);
            mdots[i].setText(Html.fromHtml("&#8226;" + "&#8226;" + "&#8226;"));
            mdots[i].setTextSize(35);
            mdots[i].setTextSize(20);
            mdots[i].setTextColor(getResources().getColor(R.color.transparent));

            dotlayout.addView(mdots[i]);
        }

        if(mdots.length >0){
            mdots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage = 1;

            if(i==0){
                btnprev.setEnabled(false);
                btnnext.setEnabled(true);
                btnprev.setVisibility(View.INVISIBLE);

                btnnext.setText("Next");
                btnprev.setText("");
            }else if (i == mdots.length - 1){
                btnnext.setEnabled(true);
                btnprev.setEnabled(true);
                btnprev.setVisibility(View.VISIBLE);

                btnnext.setText("Finish");
                btnprev.setText("Back");

                btnnext.setOnClickListener(
                        new Button.OnClickListener()
                        {
                            public void onClick(View v)
                            {
                                Intent myIntent = new Intent(v.getContext(), Home.class);
                                startActivity(myIntent);
                            }
                        }
                );
            }else{
                btnnext.setEnabled(true);
                btnprev.setEnabled(true);
                btnprev.setVisibility(View.VISIBLE);

                btnnext.setText("Next");
                btnprev.setText("Back");



            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
