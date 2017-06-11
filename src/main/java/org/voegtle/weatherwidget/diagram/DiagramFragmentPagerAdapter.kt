package org.voegtle.weatherwidget.diagram

import android.app.FragmentManager
import android.support.v13.app.FragmentPagerAdapter

import java.util.ArrayList

class DiagramFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

  private val fragments = ArrayList<DiagramFragment>()

  fun add(fragment: DiagramFragment) {
    fragments.add(fragment)
  }

  override fun getItem(i: Int): DiagramFragment {
    return fragments[i]
  }

  override fun getCount(): Int {
    return fragments.size
  }
}
