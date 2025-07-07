package org.voegtle.weatherwidget.diagram

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.base.ThemedActivity
import org.voegtle.weatherwidget.databinding.ActivityDiagramsBinding
import org.voegtle.weatherwidget.util.UserFeedback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Date


abstract class DiagramActivity : ThemedActivity() {
  protected var diagramIdList = ArrayList<DiagramEnum>()

  protected var pagerAdapter: DiagramFragmentPagerAdapter? = null
  private lateinit var binding: ActivityDiagramsBinding


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityDiagramsBinding.inflate(layoutInflater)

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
    for (i in 0..< pagerAdapter!!.count) {
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      shareImage(diagramIndex)
    } else {
      shareImageLegacy(diagramIndex)
    }
  }

  private fun shareImage(diagramIndex: Int) {
    val mimeType = "image/png"

    val values = ContentValues()

    values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Wetter Diagramm")
    values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + File.separator + "WetterWolke")

    val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
    val outputStream = contentResolver.openOutputStream(uri!!)

    outputStream?.let {
      writeImageToStream(diagramIndex, outputStream)
      outputStream.close()
    }

    val share = Intent(Intent.ACTION_SEND)
    share.type = mimeType
    share.putExtra(Intent.EXTRA_STREAM, uri)
    startActivity(Intent.createChooser(share, "Wetterwolke Diagramm"))

  }

  private fun shareImageLegacy(diagramIndex: Int): Boolean {
    // Assume thisActivity is the current activity
    val permissionCheck = ContextCompat.checkSelfPermission(
      this,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
      requestStoragePermission(diagramIndex)
      return true
      }

    ensureWetterWolkeDirectory()

    clearImages()

    val file = writeImageToFile(diagramIndex)
    file?.let {
      val share = Intent(Intent.ACTION_SEND)
      share.type = "image/png"
      val imageUri = FileProvider.getUriForFile(
        this,
        this.applicationContext.packageName
                + ".org.voegtle.weatherwidget.diagram.DiagramProvider", it
      )
      share.putExtra(Intent.EXTRA_STREAM, imageUri)
      startActivity(Intent.createChooser(share, "Wetterwolke Diagramm teilen"))
      }
    return false
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
    wetterWolkeDir.list { _, filename -> filename.lowercase().endsWith(".png") }?.forEach {
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

  private fun writeImageToStream(diagramIndex: Int, out: OutputStream) {
    val diagramEnum = diagramIdList[diagramIndex]
    val diagramCache = DiagramCache(this)
    val image = diagramCache.asPNG(diagramEnum)
    out.write(image)
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
    super.onRequestPermissionsResult(diagramId, permissions, grantResults)
  }

  protected fun updateViewPager(index: Int): Boolean {
    binding.pager.setCurrentItem(index, true)
    return true
  }

  protected abstract fun onCustomItemSelected(item: MenuItem): Boolean
  protected abstract val placeHolderId: Int

}
