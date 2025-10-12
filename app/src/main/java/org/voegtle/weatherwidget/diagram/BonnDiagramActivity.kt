package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class BonnDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int = R.drawable.station_bonn

  override fun getCaption() = getString(R.string.city_bonn_full)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.bonn_2days)
    addDiagram(DiagramEnum.bonn_wind)
    addDiagram(DiagramEnum.bonn_30days)
    addDiagram(DiagramEnum.bonn_lastyear)
    addDiagram(DiagramEnum.bonn_year)
  }

  override fun getMenu(): List<Pair<Int, () -> Unit>> {
    return listOf(
      Pair(R.string.action_2_days) { updatePage(0) },
      Pair(R.string.action_wind) { updatePage(1) },
      Pair(R.string.action_30_days) { updatePage(2) },
      Pair(R.string.action_last_year) { updatePage(3) },
      Pair(R.string.action_year) { updatePage(4) }
    )
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean = false
}
