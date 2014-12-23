package org.voegtle.weatherwidget.location;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.voegtle.weatherwidget.R;

public class LocationView extends LinearLayout {
  private final Context context;
  private TextView captionView;
  private TextView dataView;
  private TextView moreDataView;
  private ImageButton resizeButton;
  private ImageButton diagramButton;
  private ImageButton forecastButton;

  private boolean expanded;
  private Drawable imageExpand;
  private Drawable imageCollapse;
  private OnClickListener externalClickListener;
  private OnClickListener diagramListener;
  private OnClickListener forecastListener;

  public LocationView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;

    LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    li.inflate(R.layout.view_location, this, true);

    captionView = (TextView) findViewById(R.id.caption);
    dataView = (TextView) findViewById(R.id.data);
    moreDataView = (TextView) findViewById(R.id.more_data);

    imageCollapse = context.getResources().getDrawable(R.drawable.ic_action_collapse);
    imageExpand = context.getResources().getDrawable(R.drawable.ic_action_expand);

    TypedArray attributes = context.getTheme().obtainStyledAttributes(
        attrs, R.styleable.LocationView, 0, 0);

    try {
      initializeTextViews(attributes);
      initializeButtons(attributes);
    } finally {
      attributes.recycle();
    }
  }

  private void initializeButtons(TypedArray attributes) {
    resizeButton = (ImageButton) findViewById(R.id.resize_button);
    resizeButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View arg0) {
        setExpanded(!expanded);
        if (externalClickListener != null) {
          externalClickListener.onClick(arg0);
        }
      }
    });

    boolean diagrams = attributes.getBoolean(R.styleable.LocationView_diagrams, false);
    diagramButton = (ImageButton) findViewById(R.id.diagram_button);
    diagramButton.setVisibility(diagrams ? View.VISIBLE : View.GONE);
    diagramButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (diagramListener != null) {
          diagramListener.onClick(LocationView.this);
        }
      }
    });

    boolean forecast = attributes.getBoolean(R.styleable.LocationView_forecast, true);
    forecastButton = (ImageButton) findViewById(R.id.forecast_button);
    forecastButton.setVisibility(forecast ? View.VISIBLE : View.GONE);
    forecastButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (forecastListener != null)
          forecastListener.onClick(LocationView.this);
      }
    });

  }

  public void setOnClickListener(OnClickListener listener) {
    externalClickListener = listener;
  }

  public void setDiagramsOnClickListener(OnClickListener listener) {
    diagramListener = listener;
  }

  public void setForecastOnClickListener(OnClickListener listener) {
    forecastListener = listener;
  }

  private void initializeTextViews(TypedArray attributes) {
    String caption = attributes.getString(R.styleable.LocationView_caption);
    captionView.setText(caption);
    String data = attributes.getString(R.styleable.LocationView_data);
    dataView.setText(data);

  }

  public void setCaption(String caption) {
    captionView.setText(caption);
  }

  public void setData(String data) {
    dataView.setText(data);
  }

  public void setMoreData(String moreData) {
    moreDataView.setText(moreData);
  }

  public void configureSymbols(boolean useDarkSymbols) {
    if (useDarkSymbols) {
      imageCollapse = context.getResources().getDrawable(R.drawable.ic_action_collapse_dark);
      imageExpand = context.getResources().getDrawable(R.drawable.ic_action_expand_dark);

      Drawable imageForecastDark = context.getResources().getDrawable(R.drawable.ic_action_forecast_dark);
      forecastButton.setImageDrawable(imageForecastDark);
      Drawable imageDiagramDark = context.getResources().getDrawable(R.drawable.ic_action_picture_dark);
      diagramButton.setImageDrawable(imageDiagramDark);
    }
    setExpanded(expanded);
  }

  public void setTextColor(int color) {
    captionView.setTextColor(color);
//    dataView.setTextColor(color);
//    moreDataView.setTextColor(color);
  }

  public boolean isExpanded() {
    return expanded;
  }

  public void setExpanded(boolean expanded) {
    this.expanded = expanded;
    resizeButton.setImageDrawable(expanded ? imageCollapse : imageExpand);
    moreDataView.setVisibility(expanded ? View.VISIBLE : View.GONE);
  }
}
