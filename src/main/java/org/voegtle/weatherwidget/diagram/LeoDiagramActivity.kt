package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.util.StringUtil

class LeoDiagramActivity : DiagramActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addDiagram(DiagramEnum.leo_regen)
        addDiagram(DiagramEnum.leo_wind)
        addDiagram(DiagramEnum.leo_solar)
        addDiagram(DiagramEnum.leo_solar_average)
        addDiagram(DiagramEnum.leo_solar_production)
        addDiagram(DiagramEnum.leo_30days)
        addDiagram(DiagramEnum.leo_lastyear)
        val intent = intent
        if (intent != null) {
            val title = intent.getStringExtra(LeoDiagramActivity::class.java.name)
            if (StringUtil.isNotEmpty(title)) {
                setTitle(title)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.leo_diagram_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCustomItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_leo_rain -> return updateViewPager(0)
            R.id.action_leo_wind -> return updateViewPager(1)
            R.id.action_leo_solar -> return updateViewPager(2)
            R.id.action_solar_daily -> return updateViewPager(3)
            R.id.action_solar_monthly -> return updateViewPager(4)
            R.id.action_leo_30days -> return updateViewPager(5)
            R.id.action_leo_last_year -> return updateViewPager(6)
        }
        return false
    }
}
