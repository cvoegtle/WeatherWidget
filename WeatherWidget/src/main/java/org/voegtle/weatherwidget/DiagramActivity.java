package org.voegtle.weatherwidget;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.voegtle.weatherwidget.diagram.DiagramEnum;
import org.voegtle.weatherwidget.persistence.DiagramCache;

public class DiagramActivity extends FragmentActivity {
  private ViewPager viewPager;
  private DiagramCache diagramCache;
  private DiagramFragmentPagerAdapter pagerAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.diagramCache = new DiagramCache(this);

    setContentView(R.layout.activity_digrams);
    this.viewPager = (ViewPager) findViewById(R.id.pager);
    this.pagerAdapter = getPageAdapter();
    this.viewPager.setAdapter(pagerAdapter);
  }

  @Override
  protected void onStart() {
    super.onStart();
    int currentItem = diagramCache.readCurrentDiagram();
    viewPager.setCurrentItem(currentItem);
  }

  @Override
  protected void onStop() {
    super.onStop();
    diagramCache.saveCurrentDiagram(viewPager.getCurrentItem());
  }

  private DiagramFragmentPagerAdapter getPageAdapter() {
    DiagramFragmentPagerAdapter pagerAdapter = new DiagramFragmentPagerAdapter(getSupportFragmentManager());
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
