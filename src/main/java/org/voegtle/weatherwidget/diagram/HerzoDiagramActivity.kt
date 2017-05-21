package org.voegtle.weatherwidget.diagram

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.util.StringUtil

class HerzoDiagramActivity : DiagramActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addDiagram(DiagramEnum.herzo_regen)
        addDiagram(DiagramEnum.herzo_wind)
        addDiagram(DiagramEnum.herzo_30days)
        addDiagram(DiagramEnum.herzo_lastyear)
        val intent = intent
        if (intent != null) {
            val title = intent.getStringExtra(HerzoDiagramActivity::class.java.name)
            if (StringUtil.isNotEmpty(title)) {
                setTitle(title)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.herzo_diagram_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCustomItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_herzo_rain -> return updateViewPager(0)
            R.id.action_herzo_wind -> return updateViewPager(1)
            R.id.action_herzo_30days -> return updateViewPager(2)
            R.id.action_herzo_last_year -> return updateViewPager(3)
        }
        return false
    }
}
