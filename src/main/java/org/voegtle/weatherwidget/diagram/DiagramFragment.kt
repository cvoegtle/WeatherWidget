package org.voegtle.weatherwidget.diagram

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.voegtle.weatherwidget.R

class DiagramFragment : Fragment() {

  private var diagramManager: DiagramManager? = null
  private var diagramId: DiagramEnum? = null

  override fun onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    ensureResources()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View {
    return inflater.inflate(R.layout.fragment_diagram, container, false)
  }

  override fun onResume() {
    super.onResume()
    ensureResources()
    diagramManager!!.onResume()
    diagramManager!!.updateDiagram(diagramId)
  }

  override fun onPause() {
    diagramManager!!.onPause()
    super.onPause()
  }

  private fun ensureResources() {
    if (diagramManager == null) {
      this.diagramManager = DiagramManager(this)
      diagramId = DiagramEnum.byId(arguments.getInt(DiagramEnum::class.java.name, -1))
    }
  }


  fun reload() {
    diagramManager!!.updateDiagram(diagramId, true)
  }

  companion object {

    fun newInstance(diagramId: DiagramEnum): DiagramFragment {
      val newFragment = DiagramFragment()

      val bundle = Bundle()
      bundle.putInt(DiagramEnum::class.java.name, diagramId.id)
      newFragment.arguments = bundle

      return newFragment
    }
  }
}
