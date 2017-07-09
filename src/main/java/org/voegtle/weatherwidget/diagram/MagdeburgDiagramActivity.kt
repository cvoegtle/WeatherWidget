package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.util.StringUtil

class MagdeburgDiagramActivity : DiagramActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.magdeburg_regen)
    addDiagram(DiagramEnum.magdeburg_humidity)
    addDiagram(DiagramEnum.magdeburg_paderborn_freiburg)
    addDiagram(DiagramEnum.magedburg_30days)
    addDiagram(DiagramEnum.magedburg_lastyear)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.magdeburg_diagram_activity_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean =
      when (item.itemId) {
        R.id.action_magdeburg_rain -> updateViewPager(0)
        R.id.action_magdeburg_humidity -> updateViewPager(1)
        R.id.action_md_pb_fr -> updateViewPager(2)
        R.id.action_md_30days -> updateViewPager(3)
        R.id.action_md_last_year -> updateViewPager(4)
        else -> false
      }
}
