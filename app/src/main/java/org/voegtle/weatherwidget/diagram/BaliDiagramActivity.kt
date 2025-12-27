package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class BaliDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int = R.drawable.station_bali

  override fun getCaption() = getString(R.string.city_bali_full)

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

  override fun getMenu(): List<Pair<Int, () -> Unit>> {
    return listOf(
      Pair(R.string.action_bali_7days) { updatePage(0) },
      Pair(R.string.action_wind) { updatePage(1) },
      Pair(R.string.action_humidity) { updatePage(2) },
      Pair(R.string.action_power) { updatePage(3) },
      Pair(R.string.action_bali_solar_daily) { updatePage(4) },
      Pair(R.string.action_bali_solar_monthly) { updatePage(5) },
      Pair(R.string.action_30_days) { updatePage(6) },
      Pair(R.string.action_bali_paderborn) { updatePage(7) },
      Pair(R.string.action_last_year) { updatePage(8) }
    )
  }


  override fun onCustomItemSelected(item: MenuItem): Boolean = false

}
