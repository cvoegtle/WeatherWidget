package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class MainDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int = R.drawable.station_all

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.temperature7days)
    addDiagram(DiagramEnum.pb_bali_leo)
    addDiagram(DiagramEnum.family_weather)
    addDiagram(DiagramEnum.average7days)
    addDiagram(DiagramEnum.rain)
    addDiagram(DiagramEnum.summerdays)
    addDiagram(DiagramEnum.summerdays2024)
    addDiagram(DiagramEnum.winterdays)
    addDiagram(DiagramEnum.winterdays2024)
  }

  override fun getMenu(): List<Pair<Int, () -> Unit>> {
    return listOf(
      Pair(R.string.action_7_days) { updatePage(0) },
      Pair(R.string.action_7_days_owl) { updatePage(1) },
      Pair(R.string.action_family_weather) { updatePage(2) },
      Pair(R.string.action_7_days_average) { updatePage(3) },
      Pair(R.string.action_rain) { updatePage(4) },
      Pair(R.string.action_summer_days) { updatePage(5) },
      Pair(R.string.action_summer_days2024) { updatePage(6) },
      Pair(R.string.action_winter_days) { updatePage(7) },
      Pair(R.string.action_winter_days2024) { updatePage(8) }
    )
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean = false
}
