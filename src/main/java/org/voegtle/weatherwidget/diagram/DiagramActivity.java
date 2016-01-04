package org.voegtle.weatherwidget.diagram;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.base.ThemedActivity;
import org.voegtle.weatherwidget.util.StringUtil;
import org.voegtle.weatherwidget.util.UserFeedback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public abstract class DiagramActivity extends ThemedActivity {
  protected ArrayList<DiagramEnum> diagramIdList = new ArrayList<>();

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
      case R.id.action_share:
        shareCurrentImage(viewPager.getCurrentItem());
        return true;

      default:
        return onCustomItemSelected(item);
    }
  }

  protected void shareCurrentImage(int diagramIndex) {
    // Assume thisActivity is the current activity
    int permissionCheck = ContextCompat.checkSelfPermission(this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE);
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
      requestStoragePermission(diagramIndex);
      return;
    }

    String filename = writeImageToFile(diagramIndex);
    if (StringUtil.isNotEmpty(filename)) {
      Intent share = new Intent(Intent.ACTION_SEND);
      share.setType("image/png");
      share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filename));
      startActivity(Intent.createChooser(share, "Wetterwolke Diagramm teilen"));
    }
  }

  private String writeImageToFile(int diagramIndex) {
    String filename = Environment.getExternalStorageDirectory() + File.separator + "wetterwolke_share.png";
    byte[] image = diagramCache.asPNG(diagramIdList.get(diagramIndex));
    File f = new File(filename);
    try {
      if (f.exists()) {
        f.delete();
      }
      f.createNewFile();
      FileOutputStream fo = new FileOutputStream(f);
      fo.write(image);
    } catch (IOException e) {
      Log.e(DiagramActivity.class.toString(), "failed to write image", e);
      filename = null;
    }
    return filename;
  }

  private void requestStoragePermission(int diagramId) {
    ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
        diagramId);
  }


  @Override
  public void onRequestPermissionsResult(int diagramId, String[] permissions, int[] grantResults) {
      if (grantResults.length > 0) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          shareCurrentImage(diagramId);
        } else {
          new UserFeedback(this).showMessage(R.string.message_permission_required, true);
        }
      }
  }

  abstract protected boolean onCustomItemSelected(MenuItem item);

}
