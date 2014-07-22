package org.voegtle.weatherwidget;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class DiagramFragmentPagerAdapter extends FragmentPagerAdapter {

  private ArrayList<DiagramFragment> fragments = new ArrayList<DiagramFragment>();

  public DiagramFragmentPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  public void add(DiagramFragment fragment) {
    fragments.add(fragment);
  }

  @Override
  public DiagramFragment getItem(int i) {
    return fragments.get(i);
  }

  @Override
  public int getCount() {
    return fragments.size();
  }
}
