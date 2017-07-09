package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class FreiburgDiagramActivity : DiagramActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.freiburg_2days)
    addDiagram(DiagramEnum.freiburg_wind)
    addDiagram(DiagramEnum.freiburg_30days)
    addDiagram(DiagramEnum.freiburg_lastyear)
    addDiagram(DiagramEnum.freiburg_year)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.freiburg_diagram_activity_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean =
      when (item.itemId) {
        R.id.action_rain -> updateViewPager(0)
        R.id.action_wind -> updateViewPager(1)
        R.id.action_30_days -> updateViewPager(2)
        R.id.action_last_year -> updateViewPager(3)
        R.id.action_year -> updateViewPager(4)
        else -> false
      }
}
