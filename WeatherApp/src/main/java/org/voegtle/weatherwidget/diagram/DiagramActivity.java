package org.voegtle.weatherwidget.diagram;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import org.voegtle.weatherwidget.R;

import java.util.ArrayList;

public abstract class DiagramActivity extends Activity {
  protected ArrayList<DiagramEnum> diagramIdList = new ArrayList<DiagramEnum>();

  protected ViewPager viewPager;
  private DiagramCache diagramCache;
  protected DiagramFragmentPagerAdapter pagerAdapter;

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

    this.pagerAdapter = createPageAdapter();
    this.viewPager.setAdapter(pagerAdapter);

    int currentItem = diagramCache.readCurrentDiagram(this.getClass().getName());
    viewPager.setCurrentItem(currentItem);
  }

  @Override
  protected void onPause() {
    diagramCache.saveCurrentDiagram(this.getClass().getName(), viewPager.getCurrentItem());
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

  private DiagramFragmentPagerAdapter createPageAdapter() {
    DiagramFragmentPagerAdapter pagerAdapter = new DiagramFragmentPagerAdapter(getFragmentManager());
    for (DiagramEnum diagramId : diagramIdList) {
      pagerAdapter.add(DiagramFragment.newInstance(diagramId));
    }
    return pagerAdapter;
  }

  protected void addDiagram(DiagramEnum diagramId) {
    diagramIdList.add(diagramId);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_reload:
        int index = viewPager.getCurrentItem();
        DiagramFragment fragment = pagerAdapter.getItem(index);
        fragment.reload();
        return true;

      default:
        return onCustomItemSelected(item);
    }
  }

  abstract protected boolean onCustomItemSelected(MenuItem item);

}
