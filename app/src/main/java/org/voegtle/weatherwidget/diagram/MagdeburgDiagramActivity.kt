package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class MagdeburgDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int = R.drawable.station_magdeburg
  override fun getCaption() = getString(R.string.city_magdeburg_full)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.magdeburg_regen)
    addDiagram(DiagramEnum.magdeburg_wind)
    addDiagram(DiagramEnum.magdeburg_humidity)
    addDiagram(DiagramEnum.magdeburg_paderborn_freiburg)
    addDiagram(DiagramEnum.magedburg_30days)
    addDiagram(DiagramEnum.magedburg_lastyear)
  }

  override fun getMenu(): List<Pair<Int, () -> Unit>> {
    return listOf(
      Pair(R.string.action_rain) { updatePage(0) },
      Pair(R.string.action_wind) { updatePage(1) },
      Pair(R.string.action_humidity) { updatePage(2) },
      Pair(R.string.action_compare_md_with_pb_fr) { updatePage(3) },
      Pair(R.string.action_30_days) { updatePage(4) },
      Pair(R.string.action_last_year) { updatePage(5) }
    )
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean = false
}
