package org.voegtle.weatherwidget.diagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_digrams.*
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.base.ThemedActivity
import org.voegtle.weatherwidget.util.StringUtil
import org.voegtle.weatherwidget.util.UserFeedback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


abstract class DiagramActivity : ThemedActivity() {
  protected var diagramIdList = ArrayList<DiagramEnum>()

  protected var pagerAdapter: DiagramFragmentPagerAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_digrams)
  }

  override fun onResume() {
    super.onResume()
    val diagramCache = DiagramCache(this)

    this.pagerAdapter = createPageAdapter()
    pager.adapter = pagerAdapter

    val currentItem = diagramCache.readCurrentDiagram(this.javaClass.name)
    pager.currentItem = currentItem
  }

  override fun onPause() {
    val diagramCache = DiagramCache(this)

    diagramCache.saveCurrentDiagram(this.javaClass.name, pager.currentItem)
    pager.removeAllViews()
    cleanupFragments()
    super.onPause()
  }

  private fun cleanupFragments() {
    val fm = fragmentManager
    val fragmentTransaction = fm!!.beginTransaction()
    for (i in 0..pagerAdapter!!.count - 1) {
      fragmentTransaction.remove(pagerAdapter!!.getItem(i))
    }
    fragmentTransaction.commit()
  }

  private fun createPageAdapter(): DiagramFragmentPagerAdapter {
    val pagerAdapter = DiagramFragmentPagerAdapter(fragmentManager)
    for (diagramId in diagramIdList) {
      pagerAdapter.add(DiagramFragment(diagramId))
    }
    return pagerAdapter
  }

  protected fun addDiagram(diagramId: DiagramEnum) {
    diagramIdList.add(diagramId)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_reload -> {
        val index = pager.currentItem
        val fragment = pagerAdapter!!.getItem(index)
        fragment.reload()
        return true
      }
      R.id.action_share -> {
        shareCurrentImage(pager.currentItem)
        return true
      }

      else -> return onCustomItemSelected(item)
    }
  }

  protected fun shareCurrentImage(diagramIndex: Int) {
    // Assume thisActivity is the current activity
    val permissionCheck = ContextCompat.checkSelfPermission(this,
                                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
      requestStoragePermission(diagramIndex)
      return
    }

    ensureWetterWolkeDirectory()

    clearImages()

    val file = writeImageToFile(diagramIndex)
    file?.let {
      val share = Intent(Intent.ACTION_SEND)
      share.type = "image/png"
      val imageUri = FileProvider.getUriForFile(this,
                                                this.getApplicationContext().getPackageName()
                                                    + ".org.voegtle.weatherwidget.diagram.DiagramProvider", it)
      share.putExtra(Intent.EXTRA_STREAM, imageUri)
      startActivity(Intent.createChooser(share, "Wetterwolke Diagramm teilen"))
    }
  }

  private fun ensureWetterWolkeDirectory() {
    val folder = wetterWolkeDirectory
    if (!folder.exists()) {
      folder.mkdirs()
    }
  }

  private val wetterWolkeDirectory: File
    get() = File(Environment.getExternalStorageDirectory().toString() + File.separator + "WetterWolke")

  private fun clearImages() {
    val wetterWolkeDir = wetterWolkeDirectory
    wetterWolkeDir.list { _, filename -> filename.toLowerCase().endsWith(".png") }?.forEach {
        File(wetterWolkeDir.toString() + File.separator + it).delete()
    }
  }

  private fun writeImageToFile(diagramIndex: Int): File? {
    val diagramEnum = diagramIdList[diagramIndex]
    val diagramCache = DiagramCache(this)
    val image = diagramCache.asPNG(diagramEnum)
    val filename: String = "$wetterWolkeDirectory${File.separator}${Date().time}-${diagramEnum.filename}"
    val f = File(filename)
    try {
      f.createNewFile()
      FileOutputStream(f).use {
        it.write(image)
      }
    } catch (e: IOException) {
      Log.e(DiagramActivity::class.java.toString(), "failed to write image", e)
      return null
    }

    return f
  }

  private fun requestStoragePermission(diagramId: Int) {
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), diagramId)
  }


  override fun onRequestPermissionsResult(diagramId: Int, permissions: Array<String>, grantResults: IntArray) {
    if (grantResults.isNotEmpty()) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        shareCurrentImage(diagramId)
      } else {
        UserFeedback(this).showMessage(R.string.message_permission_required, true)
      }
    }
  }

  protected fun updateViewPager(index: Int): Boolean {
    pager.setCurrentItem(index, true)
    return true
  }

  protected abstract fun onCustomItemSelected(item: MenuItem): Boolean

}
