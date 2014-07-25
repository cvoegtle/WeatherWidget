package org.voegtle.weatherwidget.diagram;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.voegtle.weatherwidget.R;

public class DiagramActivity extends Activity {
  private ViewPager viewPager;
  private DiagramCache diagramCache;
  private DiagramFragmentPagerAdapter pagerAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_digrams);
    this.viewPager = (ViewPager) findViewById(R.id.pager);
  }

  @Override
  protected void onResume() {
    super.onResume();
    this.diagramCache = new DiagramCache(this);

    this.pagerAdapter = getPageAdapter();
    this.viewPager.setAdapter(pagerAdapter);

    int currentItem = diagramCache.readCurrentDiagram();
    viewPager.setCurrentItem(currentItem);
  }

  @Override
  protected void onPause() {
    diagramCache.saveCurrentDiagram(viewPager.getCurrentItem());
    viewPager.removeAllViews();
    cleanupFragments();
    super.onPause();
  }

  private void cleanupFragments() {
    FragmentManager fm = getFragmentManager();
    FragmentTransaction fragmentTransaction = fm.beginTransaction();
    for (int i = 0; i < pagerAdapter.getCount(); i++) {
      fragmentTransaction.remove(pagerAdapter.getItem(i));
    }
    fragmentTransaction.commit();
  }

  private DiagramFragmentPagerAdapter getPageAdapter() {
    DiagramFragmentPagerAdapter pagerAdapter = new DiagramFragmentPagerAdapter(getFragmentManager());
    for (DiagramEnum diagramId : DiagramEnum.values()) {
      pagerAdapter.add(new DiagramFragment(diagramCache, diagramId));
    }
    return pagerAdapter;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.diagram_activity_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_reload:
        int index = viewPager.getCurrentItem();
        DiagramFragment fragment = pagerAdapter.getItem(index);
        fragment.reload();
        return true;
      case R.id.action_7_days:
        viewPager.setCurrentItem(0, true);
        return true;

      case R.id.action_7_days_average:
        viewPager.setCurrentItem(1, true);
        return true;

      case R.id.action_summerdays:
        viewPager.setCurrentItem(2, true);
        return true;
    }
    return false;
  }
}
