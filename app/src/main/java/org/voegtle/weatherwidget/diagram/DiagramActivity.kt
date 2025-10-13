package org.voegtle.weatherwidget.diagram

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.preferences.WeatherPreferencesReader
import org.voegtle.weatherwidget.ui.theme.WeatherWidgetTheme
import org.voegtle.weatherwidget.util.UserFeedback
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Date


abstract class DiagramActivity : AppCompatActivity() {
    protected var diagramIdList = ArrayList<DiagramEnum>()
    private val selectedPage = mutableStateOf(0)
    private val diagramUpdateState = mutableStateMapOf<DiagramEnum, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val configuration = WeatherPreferencesReader(this).read()
        val diagramCache = DiagramCache(this)
        selectedPage.value = diagramCache.readCurrentDiagram(this.javaClass.name)

        setContent {
            WeatherWidgetTheme (appTheme = configuration.appTheme){
                DiagramScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    fun DiagramScreen() {
        val pagerState = rememberPagerState(initialPage = selectedPage.value, pageCount = { diagramIdList.size })
        val coroutineScope = rememberCoroutineScope()
        var menuExpanded by remember { mutableStateOf(false) }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                selectedPage.value = page
            }
        }

        LaunchedEffect(selectedPage.value) {
            if (pagerState.currentPage != selectedPage.value) {
                pagerState.animateScrollToPage(selectedPage.value)
            }
        }


        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(getCaption()) },
                    navigationIcon = {
                        IconButton(onClick = { onBackPressedDispatcher.onBackPressed() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                updateDiagram(diagramIdList[pagerState.currentPage], true)
                            }
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(id = R.string.action_reload))
                        }
                        IconButton(onClick = { shareCurrentImage(pagerState.currentPage) }) {
                            Icon(Icons.Default.Share, contentDescription = stringResource(id = R.string.action_share))
                        }
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            getMenu().forEach { (titleRes, action) ->
                                DropdownMenuItem(text = { Text(stringResource(id = titleRes)) }, onClick = {
                                    action()
                                    menuExpanded = false
                                })
                            }
                        }
                    })
            }
        ) { innerPadding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.padding(innerPadding),
                userScrollEnabled = true
            ) { page ->
                val diagramId = diagramIdList[page]
                val updateCount = diagramUpdateState[diagramId] ?: 0
                DiagramPage(diagramId, updateCount)
            }
        }
    }

    @Composable
    fun DiagramPage(diagramId: DiagramEnum, updateCount: Int) {
        var diagram by remember { mutableStateOf<Diagram?>(null) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(diagramId, updateCount) {
            val diagramCache = DiagramCache(this@DiagramActivity)
            diagram = diagramCache.read(diagramId)
            if (diagram == null) {
                coroutineScope.launch {
                    updateDiagram(diagramId, false)
                }
            }
        }

        if (diagram != null) {
            ZoomableImage(
                bitmap = drawableToBitmap(diagram!!.image).asImageBitmap(),
                contentDescription = diagramId.toString(),
            )
        } else {
            Image(
                painter = painterResource(id = placeHolderId),
                contentDescription = diagramId.toString(),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ZoomableImage(
        bitmap: androidx.compose.ui.graphics.ImageBitmap,
        contentDescription: String,
        modifier: Modifier = Modifier,
    ) {
        val scope = rememberCoroutineScope()

        val scale = remember { Animatable(1f) }
        val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
                scope.launch {
                    scale.snapTo((scale.value * zoomChange).coerceIn(1f, 3f))

                    if (scale.value > 1f) {
                        val maxOffsetX = (constraints.maxWidth * (scale.value - 1)) / 2f
                        val maxOffsetY = (constraints.maxHeight * (scale.value - 1)) / 2f
                        val newOffset = offset.value + offsetChange
                        offset.snapTo(
                            Offset(
                                x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
                                y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
                            )
                        )
                    } else {
                        offset.snapTo(Offset.Zero)
                    }
                }
            }

            Image(
                bitmap = bitmap,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                scope.launch {
                                    if (scale.value > 1f) {
                                        scale.animateTo(1f)
                                        offset.animateTo(Offset.Zero)
                                    } else {
                                        scale.animateTo(2f)
                                    }
                                }
                            }

                        )
                    }
                    .graphicsLayer(
                        scaleX = scale.value,
                        scaleY = scale.value,
                        translationX = offset.value.x,
                        translationY = offset.value.y
                    )
                    .transformable(
                        state = transformableState,
                        canPan = { scale.value > 1f }
                    ),
                contentScale = ContentScale.Fit,
            )
        }
    }


    private suspend fun updateDiagram(diagramId: DiagramEnum, force: Boolean) {
        val diagramManager = DiagramManager(this)
        diagramManager.updateDiagram(diagramId, force)
        diagramUpdateState[diagramId] = (diagramUpdateState[diagramId] ?: 0) + 1
    }


    protected open fun getCaption() = getString(R.string.action_diagrams)


    override fun onPause() {
        super.onPause()
        val diagramCache = DiagramCache(this)
        diagramCache.saveCurrentDiagram(this.javaClass.name, selectedPage.value)
    }

    fun addDiagram(diagramId: DiagramEnum) {
        diagramIdList.add(diagramId)
        diagramUpdateState[diagramId] = 0
    }

    protected fun updatePage(index: Int): Boolean {
        selectedPage.value = index
        return true
    }

    abstract fun getMenu(): List<Pair<Int, () -> Unit>>

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { // Behandelt Klick auf den Zurück-Pfeil in der Toolbar
                onBackPressedDispatcher.onBackPressed() // Moderne Art der Zurück-Navigation
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
        values.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DOWNLOADS + File.separator + "WetterWolke"
        )

        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
        val outputStream = contentResolver.openOutputStream(uri!!)

        outputStream?.let {
            try {
                writeImageToStream(diagramIndex, it)
            } finally {
                it.close()
            }
        }

        val share = Intent(Intent.ACTION_SEND)
        share.type = mimeType
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, "Wetterwolke Diagramm"))

    }

    private fun shareImageLegacy(diagramIndex: Int): Boolean {
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
            startActivity(Intent.createChooser(share, getString(R.string.action_share)))
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
        get() = File(this.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            .toString() + File.separator + "WetterWolke")

    private fun clearImages() {
        val wetterWolkeDir = wetterWolkeDirectory
        wetterWolkeDir.list { _, filename -> filename.lowercase().endsWith(".png") }?.forEach {
            File(wetterWolkeDir.toString() + File.separator + it).delete()
        }
    }

    private fun writeImageToFile(diagramIndex: Int): File? {
        val diagramEnum = diagramIdList[diagramIndex]
        val diagramCache = DiagramCache(this)
        val diagram = diagramCache.read(diagramEnum)
        if (diagram != null) {
            val image = drawableToBitmap(diagram.image)
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val filename: String =
                "$wetterWolkeDirectory${File.separator}${Date().time}-${diagramEnum.filename}"
            val f = File(filename)
            try {
                f.createNewFile()
                FileOutputStream(f).use {
                    it.write(baos.toByteArray())
                }
            } catch (e: IOException) {
                Log.e(DiagramActivity::class.java.toString(), "failed to write image", e)
                return null
            }
            return f
        }
        return null
    }

    private fun writeImageToStream(diagramIndex: Int, out: OutputStream) {
        val diagramEnum = diagramIdList[diagramIndex]
        val diagramCache = DiagramCache(this)
        val diagram = diagramCache.read(diagramEnum)
        if (diagram != null) {
            val image = drawableToBitmap(diagram.image)
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, baos)
            out.write(baos.toByteArray())
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap =
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun requestStoragePermission(diagramId: Int) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            diagramId
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // Changed diagramId to requestCode to match super signature
        if (grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Assuming requestCode here is the diagramIndex we passed to requestStoragePermission
                shareCurrentImage(requestCode)
            } else {
                UserFeedback(this).showMessage(R.string.message_permission_required, true)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    protected abstract fun onCustomItemSelected(item: MenuItem): Boolean
    protected abstract val placeHolderId: Int

}
