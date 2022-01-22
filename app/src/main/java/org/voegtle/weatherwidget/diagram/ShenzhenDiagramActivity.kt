package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class ShenzhenDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int? = R.drawable.station_shenzhen

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.shenzhen_7days)
    addDiagram(DiagramEnum.shenzhen_30days)
    addDiagram(DiagramEnum.shenzhen_lastyear)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.shenzhen_diagram_activity_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean =
    when (item.itemId) {
      R.id.action_7_days -> updateViewPager(0)
      R.id.action_30_days -> updateViewPager(1)
      R.id.action_last_year -> updateViewPager(2)
      else -> false
    }
}
