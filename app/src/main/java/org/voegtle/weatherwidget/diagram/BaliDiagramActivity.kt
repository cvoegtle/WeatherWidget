package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class BaliDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int = R.drawable.station_bali

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.bali_7days)
    addDiagram(DiagramEnum.bali_wind)
    addDiagram(DiagramEnum.bali_humidity)
    addDiagram(DiagramEnum.bali_power)
    addDiagram(DiagramEnum.bali_solar_average)
    addDiagram(DiagramEnum.bali_solar_production)
    addDiagram(DiagramEnum.bali_30days)
    addDiagram(DiagramEnum.bali_paderborn)
    addDiagram(DiagramEnum.bali_lastyear)
  }


  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.bali_diagram_activity_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }


  override fun onCustomItemSelected(item: MenuItem): Boolean =
      when (item.itemId) {
        R.id.action_bali_7days -> updateViewPager(0)
        R.id.action_bali_wind -> updateViewPager(1)
        R.id.action_bali_humidity -> updateViewPager(2)
        R.id.action_bali_solar_production -> updateViewPager(3)
        R.id.action_bali_solar_daily -> updateViewPager(4)
        R.id.action_bali_solar_monthly -> updateViewPager(5)
        R.id.action_30_days -> updateViewPager(6)
        R.id.action_bali_paderborn -> updateViewPager(7)
        R.id.action_last_year -> updateViewPager(8)
        else -> false
      }

}
