package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class PaderbornDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int = R.drawable.station_paderborn20
  override fun getCaption() = getString(R.string.city_paderborn_full)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.paderborn_2days)
    addDiagram(DiagramEnum.paderborn20_solarradiation)
    addDiagram(DiagramEnum.paderborn20_barometer)
    addDiagram(DiagramEnum.paderborn_30days)
    addDiagram(DiagramEnum.paderborn_lastyear)
    addDiagram(DiagramEnum.paderborn_year)
  }

  override fun getMenu(): List<Pair<Int, () -> Unit>> {
    return listOf(
      Pair(R.string.action_2_days) { updatePage(0) },
      Pair(R.string.action_solarradiation) { updatePage(1) },
      Pair(R.string.action_barometer) { updatePage(2) },
      Pair(R.string.action_30_days) { updatePage(3) },
      Pair(R.string.action_last_year) { updatePage(4) },
      Pair(R.string.action_year) { updatePage(5) }
    )
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean = false
}
