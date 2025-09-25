package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class HerzoDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int = R.drawable.station_herzo
  override fun getCaption() = getString(R.string.city_herzo_full)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.herzo_regen)
    addDiagram(DiagramEnum.herzo_wind)
    addDiagram(DiagramEnum.herzo_30days)
    addDiagram(DiagramEnum.herzo_lastyear)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.herzo_diagram_activity_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean =
      when (item.itemId) {
        R.id.action_herzo_rain -> updateViewPager(0)
        R.id.action_herzo_wind -> updateViewPager(1)
        R.id.action_herzo_30days -> updateViewPager(2)
        R.id.action_herzo_last_year -> updateViewPager(3)
        else -> false
      }
}
