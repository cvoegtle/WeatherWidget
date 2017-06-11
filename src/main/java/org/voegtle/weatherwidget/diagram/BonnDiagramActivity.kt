package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class BonnDiagramActivity : DiagramActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addDiagram(DiagramEnum.bonn_2days)
        addDiagram(DiagramEnum.bonn_wind)
        addDiagram(DiagramEnum.bonn_30days)
        addDiagram(DiagramEnum.bonn_lastyear)
        addDiagram(DiagramEnum.bonn_year)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bonn_diagram_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onCustomItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_2_days -> return updateViewPager(0)

            R.id.action_wind -> return updateViewPager(1)

            R.id.action_30_days -> return updateViewPager(2)

            R.id.action_last_year -> return updateViewPager(3)

            R.id.action_year -> return updateViewPager(4)
        }
        return false
    }
}
