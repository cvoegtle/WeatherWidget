package org.voegtle.weatherwidget.state;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class StateCache {
  private final static String STATE_CACHE = "STATE";
  private final static String STATE_AGE = "AGE";
  private final static String STATE = "STATE";
  private final static String RAIN_DATA = "RAINDATA";

  private final SharedPreferences statePreferences;

  public StateCache(Context context) {
    statePreferences = context.getSharedPreferences(STATE_CACHE, 0);
  }

  public State read(int id) {
    State state = new State(id);
    long age = statePreferences.getLong(getKey(STATE_AGE, id), -1);
    if (age > 0) {
      state.setAge(new Date(age));
    }
    state.setExpanded(statePreferences.getBoolean(getKey(STATE, id), false));
    state.setRainData(statePreferences.getString(getKey(RAIN_DATA, id), ""));

    return state;
  }

  public void save(State state) {
    SharedPreferences.Editor editor = statePreferences.edit();
    editor.putLong(getKey(STATE_AGE, state.getId()), state.getAge().getTime());
    editor.putBoolean(getKey(STATE, state.getId()), state.isExpanded());
    editor.putString(getKey(RAIN_DATA, state.getId()), state.getRainData());
    editor.commit();
  }

  private String getKey(String prefix, int id) {
    return prefix + id;
  }
}
