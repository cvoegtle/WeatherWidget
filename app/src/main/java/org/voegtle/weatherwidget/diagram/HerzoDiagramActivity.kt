package org.voegtle.weatherwidget.diagram

import android.os.Bundle
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

  override fun getMenu(): List<Pair<Int, () -> Unit>> {
    return listOf(
      Pair(R.string.action_rain) { updatePage(0) },
      Pair(R.string.action_wind) { updatePage(1) },
      Pair(R.string.action_30_days) { updatePage(2) },
      Pair(R.string.action_last_year) { updatePage(3) }
    )
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean = false
}
