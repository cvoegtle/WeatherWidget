package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class Paderborn20DiagramActivity : DiagramActivity() {
  override val placeHolderId: Int? = R.drawable.station_paderborn

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.paderborn20_solarradiation)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.paderborn20_diagram_activity_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean =
    when (item.itemId) {
      R.id.action_solarradiation -> updateViewPager(0)
      else -> false
    }
}
