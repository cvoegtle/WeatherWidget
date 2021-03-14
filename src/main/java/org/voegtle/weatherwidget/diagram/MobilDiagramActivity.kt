package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.util.StringUtil

class MobilDiagramActivity : DiagramActivity() {
  override val placeHolderId: Int? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addDiagram(DiagramEnum.mobil_7days)
    addDiagram(DiagramEnum.mobil_power)
    intent.let {
      val title = it.getStringExtra(MobilDiagramActivity::class.java.name)
      if (StringUtil.isNotEmpty(title)) {
        setTitle(title)
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.mobil_diagram_activity_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }


  override fun onCustomItemSelected(item: MenuItem): Boolean =
      when (item.itemId) {
        R.id.action_instant_7days -> updateViewPager(0)
        R.id.action_instant_power -> updateViewPager(1)
        else -> false
      }
}
