package com.example.foldAR.java.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * A class providing persistent EIS preference across instances using {@code
 * android.content.SharedPreferences}.
 */
public class EisSettings {
  public static final String SHARED_PREFERENCE_ID = "SHARED_PREFERENCE_EIS_OPTIONS";
  public static final String SHARED_PREFERENCE_EIS_ENABLED = "eis_enabled";
  private boolean eisEnabled = false;
  private SharedPreferences sharedPreferences;

  /** Creates shared preference entry for EIS setting. */
  public void onCreate(Context context) {
    sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_ID, Context.MODE_PRIVATE);
    eisEnabled = sharedPreferences.getBoolean(SHARED_PREFERENCE_EIS_ENABLED, false);
  }

  /** Returns saved EIS state. */
  public boolean isEisEnabled() {
    return eisEnabled;
  }

  /** Sets and saves the EIS using {@code android.content.SharedPreferences} */
  public void setEisEnabled(boolean enable) {
    if (enable == eisEnabled) {
      return;
    }

    eisEnabled = enable;
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(SHARED_PREFERENCE_EIS_ENABLED, eisEnabled);
    editor.apply();
  }
}
