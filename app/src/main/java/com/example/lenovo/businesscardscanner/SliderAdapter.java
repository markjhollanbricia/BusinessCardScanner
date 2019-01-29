package com.example.lenovo.businesscardscanner;
        import android.content.Context;
        import android.support.annotation.NonNull;
        import android.support.v4.view.PagerAdapter;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    public int[] slide_image = {
            R.drawable.golo1,
            R.drawable.golo2,
            R.drawable.golo3
    };

    public String[] slide_headings = {
            "Scanner",
            "Mobile Application",
            "Our Company"
    };

    public String[] TextDesc = {
            " A scanner is a device that captures images from photographic prints, posters, magazine pages, and similar sources for computer editing and display.",
            "A mobile app or mobile application is a computer program or software application designed to run on a mobile device such as a phone/tablet or watch",
            "In more than 20 years of service in the curtain wall industry, ALT has advanced to become a one-stop provider, the leading curtain wall consultant in Asia. "
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView SlideImage = (ImageView) view.findViewById(R.id.slide_image);
        TextView Heading2 = (TextView) view.findViewById(R.id.textView);
        TextView desc = (TextView) view.findViewById(R.id.textView2);

        SlideImage.setImageResource(slide_image[position]);
        Heading2.setText(slide_headings[position]);
        desc.setText(TextDesc[position]);

        container.addView(view);
        return view;
    }
    public void destroyItem(ViewGroup container , int position , Object object){
        container.removeView((RelativeLayout)object);
    }
}
