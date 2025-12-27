package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class LeoDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int = R.drawable.station_leopoldshoehe
  override fun getCaption() = getString(R.string.city_leo_full)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.leo_regen)
    addDiagram(DiagramEnum.leo_wind)
    addDiagram(DiagramEnum.leo_barometer)
    addDiagram(DiagramEnum.leo_power)
    addDiagram(DiagramEnum.leo_solar_average)
    addDiagram(DiagramEnum.leo_solar_production)
    addDiagram(DiagramEnum.leo_30days)
    addDiagram(DiagramEnum.leo_lastyear)
  }

  override fun getMenu(): List<Pair<Int, () -> Unit>> {
    return listOf(
      Pair(R.string.action_rain) { updatePage(0) },
      Pair(R.string.action_wind) { updatePage(1) },
      Pair(R.string.action_barometer) { updatePage(2) },
      Pair(R.string.action_solar) { updatePage(3) },
      Pair(R.string.action_solar_daily) { updatePage(4) },
      Pair(R.string.action_solar_monthly) { updatePage(5) },
      Pair(R.string.action_30_days) { updatePage(6) },
      Pair(R.string.action_last_year) { updatePage(7) }
    )
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean = false
}
