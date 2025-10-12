package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class FreiburgDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int = R.drawable.station_freiburg
  override fun getCaption() = getString(R.string.city_freiburg_full)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.freiburg_2days)
    addDiagram(DiagramEnum.freiburg_wind)
    addDiagram(DiagramEnum.freiburg_sun)
    addDiagram(DiagramEnum.freiburg_30days)
    addDiagram(DiagramEnum.freiburg_lastyear)
    addDiagram(DiagramEnum.freiburg_year)
  }

  override fun getMenu(): List<Pair<Int, () -> Unit>> {
    return listOf(
      Pair(R.string.action_rain) { updatePage(0) },
      Pair(R.string.action_wind) { updatePage(1) },
      Pair(R.string.action_sun) { updatePage(2) },
      Pair(R.string.action_30_days) { updatePage(3) },
      Pair(R.string.action_last_year) { updatePage(4) },
      Pair(R.string.action_year) { updatePage(5) }
    )
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean = false
}
