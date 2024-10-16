package org.voegtle.weatherwidget.diagram

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.voegtle.weatherwidget.R

class DiagramFragment : Fragment {

  private var diagramManager: DiagramManager? = null
  private var diagramId: DiagramEnum? = null
  private val placeHolderId: Int?

  constructor() : super() {
    placeHolderId = null
  }

  @SuppressLint("ValidFragment") constructor(diagramId: DiagramEnum, placeHolderId: Int?) {
    val bundle = Bundle()
    bundle.putInt(DiagramEnum::class.java.name, diagramId.id)
    this.arguments = bundle
    this.placeHolderId = placeHolderId
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ensureResources()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_diagram, container, false)
  }

  override fun onResume() {
    super.onResume()
    ensureResources()
    diagramManager!!.onResume()
    diagramManager!!.updateDiagram(diagramId!!)
  }

  override fun onPause() {
    diagramManager!!.onPause()
    super.onPause()
  }

  private fun ensureResources() {
    if (diagramManager == null) {
      this.diagramManager = DiagramManager(this, placeHolderId)
      diagramId = DiagramEnum.byId(requireArguments().getInt(DiagramEnum::class.java.name, -1))
    }
  }

  fun reload() {
    diagramManager!!.updateDiagram(diagramId!!, true)
  }

}
