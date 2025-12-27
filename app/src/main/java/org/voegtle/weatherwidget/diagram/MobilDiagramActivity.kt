package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.MenuItem
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.util.StringUtil

class MobilDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int = R.drawable.station_paderborn
  override fun getCaption() = getString(R.string.city_mobil_full)


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.mobil_7days)
    addDiagram(DiagramEnum.mobil_wind)
    addDiagram(DiagramEnum.mobil_30days)
    addDiagram(DiagramEnum.mobil_freiburg)
    intent.let {
      val title = it.getStringExtra(MobilDiagramActivity::class.java.name)
      if (StringUtil.isNotEmpty(title)) {
        setTitle(title)
      }
    }
  }

  override fun getMenu(): List<Pair<Int, () -> Unit>> {
    return listOf(
      Pair(R.string.action_7_days) { updatePage(0) },
      Pair(R.string.action_wind) { updatePage(1) },
      Pair(R.string.action_30_days) { updatePage(2) },
      Pair(R.string.action_instant_freiburg) { updatePage(3) }
    )
  }

  override fun onCustomItemSelected(item: MenuItem): Boolean = false
}
