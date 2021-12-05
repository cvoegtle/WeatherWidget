package org.voegtle.weatherwidget.diagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.base.ThemedActivity
import org.voegtle.weatherwidget.databinding.ActivityDigramsBinding
import org.voegtle.weatherwidget.util.UserFeedback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


abstract class DiagramActivity : ThemedActivity() {
  protected var diagramIdList = ArrayList<DiagramEnum>()

  protected var pagerAdapter: DiagramFragmentPagerAdapter? = null
  private lateinit var binding: ActivityDigramsBinding


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityDigramsBinding.inflate(layoutInflater)

    setContentView(binding.root)
  }

  override fun onResume() {
    super.onResume()
    val diagramCache = DiagramCache(this)

    this.pagerAdapter = createPageAdapter()
    binding.pager.adapter = pagerAdapter

    val currentItem = diagramCache.readCurrentDiagram(this.javaClass.name)
    binding.pager.currentItem = currentItem
  }

  override fun onPause() {
    val diagramCache = DiagramCache(this)

    diagramCache.saveCurrentDiagram(this.javaClass.name, binding.pager.currentItem)
    binding.pager.removeAllViews()
    cleanupFragments()
    super.onPause()
  }

  private fun cleanupFragments() {
    val fm = supportFragmentManager
    val fragmentTransaction = fm!!.beginTransaction()
    for (i in 0..pagerAdapter!!.count - 1) {
      fragmentTransaction.remove(pagerAdapter!!.getItem(i))
    }
    fragmentTransaction.commit()
  }

  private fun createPageAdapter(): DiagramFragmentPagerAdapter {
    val pagerAdapter = DiagramFragmentPagerAdapter(supportFragmentManager)
    diagramIdList.forEach { diagramId -> pagerAdapter.add(DiagramFragment(diagramId, placeHolderId)) }
    return pagerAdapter
  }

  protected fun addDiagram(diagramId: DiagramEnum) {
    diagramIdList.add(diagramId)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_reload -> {
        val index = binding.pager.currentItem
        val fragment = pagerAdapter!!.getItem(index)
        fragment.reload()
        return true
      }
      R.id.action_share -> {
        shareCurrentImage(binding.pager.currentItem)
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
                                                this.applicationContext.packageName
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
    get() = File(this.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + "WetterWolke")

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
    binding.pager.setCurrentItem(index, true)
    return true
  }

  protected abstract fun onCustomItemSelected(item: MenuItem): Boolean
  protected abstract val placeHolderId: Int?

}
