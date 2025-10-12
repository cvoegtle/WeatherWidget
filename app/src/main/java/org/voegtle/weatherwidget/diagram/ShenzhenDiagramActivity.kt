package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class ShenzhenDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int = R.drawable.station_shenzhen
  override fun getCaption() = getString(R.string.city_shenzhen_full)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.shenzhen_7days)
    addDiagram(DiagramEnum.shenzhen_30days)
    addDiagram(DiagramEnum.shenzhen_lastyear)
  }

  override fun getMenu(): List<Pair<Int, () -> Unit>> {
    return listOf(
      Pair(R.string.action_7_days) { updatePage(0) },
      Pair(R.string.action_30_days) { updatePage(1) },
      Pair(R.string.action_last_year) { updatePage(2) }
    )
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean = false
}
