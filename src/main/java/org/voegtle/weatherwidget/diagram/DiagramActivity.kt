package org.voegtle.weatherwidget.diagram

import android.Manifest
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.MenuItem
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.base.ThemedActivity
import org.voegtle.weatherwidget.util.StringUtil
import org.voegtle.weatherwidget.util.UserFeedback

import java.io.*
import java.util.ArrayList
import java.util.Date

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
        val viewPager = findViewById(R.id.pager) as ViewPager
        viewPager.adapter = pagerAdapter

        val currentItem = diagramCache.readCurrentDiagram(this.javaClass.name)
        viewPager.currentItem = currentItem
    }

    override fun onPause() {
        val viewPager = findViewById(R.id.pager) as ViewPager
        val diagramCache = DiagramCache(this)

        diagramCache.saveCurrentDiagram(this.javaClass.name, viewPager.currentItem)
        viewPager.removeAllViews()
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
            pagerAdapter.add(DiagramFragment.newInstance(diagramId))
        }
        return pagerAdapter
    }

    protected fun addDiagram(diagramId: DiagramEnum) {
        diagramIdList.add(diagramId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val viewPager = findViewById(R.id.pager) as ViewPager
        when (item.itemId) {
            R.id.action_reload -> {
                val index = viewPager.currentItem
                val fragment = pagerAdapter!!.getItem(index)
                fragment.reload()
                return true
            }
            R.id.action_share -> {
                shareCurrentImage(viewPager.currentItem)
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

        val filename = writeImageToFile(diagramIndex)
        if (StringUtil.isNotEmpty(filename)) {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/png"
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filename))
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
        val pngFiles = wetterWolkeDir.list { dir, filename -> filename.toLowerCase().endsWith(".png") }
        for (filename in pngFiles) {
            File(wetterWolkeDir.toString() + File.separator + filename).delete()
        }
    }

    private fun writeImageToFile(diagramIndex: Int): String? {
        val diagramEnum = diagramIdList[diagramIndex]
        val diagramCache = DiagramCache(this)
        val image = diagramCache.asPNG(diagramEnum)
        var filename: String? = wetterWolkeDirectory.toString() + File.separator + Date().time + "-" + diagramEnum.filename
        val f = File(filename)
        try {
            if (f.exists()) {
                f.delete()
            }
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(image)
        } catch (e: IOException) {
            Log.e(DiagramActivity::class.java.toString(), "failed to write image", e)
            filename = null
        }

        return filename
    }

    private fun requestStoragePermission(diagramId: Int) {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                diagramId)
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
        val viewPager = findViewById(R.id.pager) as ViewPager
        viewPager.setCurrentItem(index, true)
        return true
    }

    protected abstract fun onCustomItemSelected(item: MenuItem): Boolean

}
