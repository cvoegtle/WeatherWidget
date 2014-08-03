package org.voegtle.weatherwidget.location;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.voegtle.weatherwidget.R;

public class LocationView extends RelativeLayout {
  private TextView captionView;
  private TextView dataView;

  public LocationView(Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    addView(li.inflate(R.layout.view_location, null));


    captionView = (TextView) findViewById(R.id.caption);
    dataView = (TextView) findViewById(R.id.data);

    TypedArray attributes = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.LocationView,
        0, 0);

    try {
      String caption = attributes.getString(R.styleable.LocationView_caption);
      captionView.setText(caption);
      String data = attributes.getString(R.styleable.LocationView_data);
      dataView.setText(data);
    } finally {
      attributes.recycle();
    }
  }

  public void setCaption(String caption) {
    captionView.setText(caption);
  }

  public void setData(String data) {
    dataView.setText(data);
  }

  public void setTextColor(int color) {
    captionView.setTextColor(color);
    dataView.setTextColor(color);
  }

}
