package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class MainDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int? = R.drawable.station_all

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.temperature7days)
    addDiagram(DiagramEnum.pb_bali_leo)
    addDiagram(DiagramEnum.family_weather)
    addDiagram(DiagramEnum.average7days)
    addDiagram(DiagramEnum.rain)
    addDiagram(DiagramEnum.summerdays)
    addDiagram(DiagramEnum.summerdays2022)
    addDiagram(DiagramEnum.winterdays)
    addDiagram(DiagramEnum.winterdays2021)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.diagram_activity_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean =
      when (item.itemId) {
        R.id.action_7_days -> updateViewPager(0)
        R.id.action_7_days_owl -> updateViewPager(1)
        R.id.action_family_weather -> updateViewPager(2)
        R.id.action_7_days_average -> updateViewPager(3)
        R.id.action_rain -> updateViewPager(4)
        R.id.action_summerdays -> updateViewPager(5)
        R.id.action_summerdays2022 -> updateViewPager(6)
        R.id.action_winterdays -> updateViewPager(7)
        R.id.action_winterdays2021 -> updateViewPager(8)
        else -> false
      }
}
