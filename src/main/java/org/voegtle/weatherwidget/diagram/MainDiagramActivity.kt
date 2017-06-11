package org.voegtle.weatherwidget.diagram

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import org.voegtle.weatherwidget.R

class MainDiagramActivity : DiagramActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addDiagram(DiagramEnum.temperature7days)
        addDiagram(DiagramEnum.pb_bali_leo)
        addDiagram(DiagramEnum.family_weather)
        addDiagram(DiagramEnum.average7days)
        addDiagram(DiagramEnum.rain)
        addDiagram(DiagramEnum.summerdays)
        addDiagram(DiagramEnum.summerdays2016)
        addDiagram(DiagramEnum.winterdays)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.diagram_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCustomItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_7_days -> return updateViewPager(0)

            R.id.action_7_days_owl -> return updateViewPager(1)

            R.id.action_family_weather -> return updateViewPager(2)

            R.id.action_7_days_average -> return updateViewPager(3)

            R.id.action_rain -> return updateViewPager(4)

            R.id.action_summerdays -> return updateViewPager(5)

            R.id.action_summerdays2016 -> return updateViewPager(6)

            R.id.action_winterdays -> return updateViewPager(7)
        }
        return false
    }

}
