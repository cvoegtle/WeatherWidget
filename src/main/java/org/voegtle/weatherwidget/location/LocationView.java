package org.voegtle.weatherwidget.location;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.Statistics;
import org.voegtle.weatherwidget.data.StatisticsSet;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.util.DataFormatter;

public class LocationView extends LinearLayout {
  private final Context context;
  private TextView captionView;
  private ImageButton resizeButton;
  private ImageButton diagramButton;
  private ImageButton forecastButton;
  private GridLayout moreData;
  private TextView kwhCaptionView;

  private boolean expanded;
  private Drawable imageExpand;
  private Drawable imageCollapse;
  private OnClickListener externalClickListener;
  private OnClickListener diagramListener;
  private OnClickListener forecastListener;

  private DataFormatter formatter;


  public LocationView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    formatter = new DataFormatter();

    LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    li.inflate(R.layout.view_location, this, true);

    captionView = (TextView) findViewById(R.id.caption);
    moreData = (GridLayout) findViewById(R.id.more_data);
    kwhCaptionView = (TextView) findViewById(R.id.caption_kwh);

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

  }

  public void setTextSize(int textSize) {
    captionView.setTextSize(textSize + 1);
    updateTextSize((GridLayout) findViewById(R.id.grid_current), textSize);
    updateTextSize((GridLayout) findViewById(R.id.more_data), textSize);
  }

  private void updateTextSize(GridLayout container, int textSize) {
    for (int i = 0; i < container.getChildCount(); i++) {
      TextView view = (TextView) container.getChildAt(i);
      view.setTextSize(textSize);
    }
  }


  public void setCaption(String caption) {
    captionView.setText(caption);
  }

  public void setData(WeatherData data) {
    TextView temperature = (TextView) findViewById(R.id.temperature);
    temperature.setText(formatter.formatTemperatureForActivity(data));

    TextView humidity = (TextView) findViewById(R.id.humidity);
    humidity.setText(formatter.formatPercent(data.getHumidity()));

    setRainData(data.getRain(), R.id.label_rain_last_hour, R.id.rain_last_hour);
    setRainData(data.getRainToday(), R.id.label_rain_today, R.id.rain_today);
    setSolarData(data.getWatt(), R.id.label_solar_output, R.id.solar_output);
  }

  private void setSolarData(Float watt, int labelId, int dataId) {
    TextView solarLabel = (TextView) findViewById(labelId);
    TextView solar = (TextView) findViewById(dataId);
    if (watt != null && watt != 0.0) {
      solarLabel.setVisibility(View.VISIBLE);
      solar.setVisibility(View.VISIBLE);
      solar.setText(formatter.formatOutput(watt));
    } else {
      solarLabel.setVisibility(View.GONE);
      solar.setVisibility(View.GONE);
    }
  }

  private void setRainData(Float value, int labelId, int dataId) {
    TextView rainLabel = (TextView) findViewById(labelId);
    TextView rain = (TextView) findViewById(dataId);
    if (value != null) {
      rainLabel.setVisibility(View.VISIBLE);
      rain.setVisibility(View.VISIBLE);
      rain.setText(formatter.formatRain(value));
    } else {
      rainLabel.setVisibility(View.GONE);
      rain.setVisibility(View.GONE);

    }
  }

  public void setMoreData(Statistics statistics) {
    if (statistics == null) {
      statistics = new Statistics();
    }

    StatisticsSet today = statistics.get(Statistics.TimeRange.today);
    updateStatistics(today, R.id.today_rain, R.id.today_min_temperature, R.id.today_max_temperature, R.id.today_kwh);

    StatisticsSet yesterday = statistics.get(Statistics.TimeRange.yesterday);
    updateStatistics(yesterday, R.id.yesterday_rain, R.id.yesterday_min_temperature, R.id.yesterday_max_temperature, R.id.yesterday_kwh);

    StatisticsSet week = statistics.get(Statistics.TimeRange.last7days);
    updateStatistics(week, R.id.week_rain, R.id.week_min_temperature, R.id.week_max_temperature, R.id.week_kwh);

    StatisticsSet month = statistics.get(Statistics.TimeRange.last30days);
    updateStatistics(month, R.id.month_rain, R.id.month_min_temperature, R.id.month_max_temperature, R.id.month_kwh);
  }

  private void updateStatistics(StatisticsSet stats, int rainId, int minTemperatureId, int maxTemperatureId, int kwhId) {
    TextView rainView = (TextView) findViewById(rainId);
    TextView minView = (TextView) findViewById(minTemperatureId);
    TextView maxView = (TextView) findViewById(maxTemperatureId);
    TextView kwhView = (TextView) findViewById(kwhId);
    rainView.setText("");
    minView.setText("");
    maxView.setText("");
    kwhView.setText("");
    if (stats != null) {
      rainView.setText(formatter.formatRain(stats.getRain()));
      minView.setText(formatter.formatTemperature(stats.getMinTemperature()));
      maxView.setText(formatter.formatTemperature(stats.getMaxTemperature()));
      if (stats.getKwh() != null) {
        kwhView.setText(formatter.formatKwh(stats.getKwh()));
        kwhCaptionView.setVisibility(VISIBLE);
      }
    }
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
  }

  public boolean isExpanded() {
    return expanded;
  }

  public void setExpanded(boolean expanded) {
    this.expanded = expanded;
    resizeButton.setImageDrawable(expanded ? imageCollapse : imageExpand);
    moreData.setVisibility(expanded ? View.VISIBLE : View.GONE);
  }
}
