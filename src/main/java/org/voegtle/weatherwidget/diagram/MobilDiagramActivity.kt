package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.util.StringUtil

class MobilDiagramActivity : DiagramActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addDiagram(DiagramEnum.mobil_7days)
        val intent = intent
        if (intent != null) {
            val title = intent.getStringExtra(MobilDiagramActivity::class.java.name)
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


    override fun onCustomItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_instant_7days -> return updateViewPager(0)
        }
        return false
    }
}
