package org.voegtle.weatherwidget.diagram

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

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
