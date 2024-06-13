package com.example.photoeditor.dashBoard

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.BaseActivity
import com.example.photoeditor.dashBoard.Adapter.EditingToolsAdapter
import com.example.photoeditor.dashBoard.Adapter.FilterListener
import com.example.photoeditor.dashBoard.Adapter.FilterViewAdapter
import com.example.photoeditor.dashBoard.Adapter.ToolType
import com.example.photoeditor.MainActivity
import com.example.photoeditor.others.Constant
import com.example.photoeditor.others.FileSaveHelper
import com.example.photoeditor.R
import com.example.photoeditor.saveImage.ImageSaveActivity
import com.example.photoeditor.databinding.ActivityDashboardScreenBinding
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropFragment
import com.yalantis.ucrop.UCropFragmentCallback
import dagger.hilt.android.AndroidEntryPoint
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.PhotoFilter
import ja.burhanrashid52.photoeditor.SaveFileResult
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.ViewType
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


@AndroidEntryPoint
class DashboardScreen : BaseActivity(), View.OnClickListener, EditingToolsAdapter.OnItemSelected,
    FilterListener, OnPhotoEditorListener, UCropFragmentCallback {
    private val binding by lazy { ActivityDashboardScreenBinding.inflate(layoutInflater) }

    lateinit var mPhotoEditor: PhotoEditor
    private lateinit var mPhotoEditorView: PhotoEditorView
    private lateinit var mTxtCurrentTool: TextView
    private lateinit var mRvTools: RecyclerView
    private lateinit var mRvFilters: RecyclerView
    private val mEditingToolsAdapter = EditingToolsAdapter(this)
    private val mFilterViewAdapter = FilterViewAdapter(this)
    private lateinit var mRootView: ConstraintLayout
    private lateinit var mWonderFont: Typeface
    private var mIsFilterVisible = false
    private val mConstraintSet = ConstraintSet()
    private var degree = 0F
    private lateinit var mSaveFileHelper: FileSaveHelper
    private lateinit var fragment: UCropFragment
    var mShowLoader : Boolean = false
    val requestMode = 1
    var imageBitmap : Bitmap? = null
    var crop = false
    private val mimeTypes = arrayOf("image/png", "image/jpeg", "image/jpg", "image/webp", "image/gif")

    companion object {

        private const val TAG = "EditImageActivity"
        const val FILE_PROVIDER_AUTHORITY = "com.burhanrashid52.photoediting.fileprovider"
        private const val CAMERA_REQUEST = 52
        private const val RC_CROP_IMAGE = 102
        const val ACTION_NEXTGEN_EDIT = "action_nextgen_edit"
        const val PINCH_TEXT_SCALABLE_INTENT_KEY = "PINCH_TEXT_SCALABLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        imageUri = createImageUri()!!
        imageBitmap = uriToBitmap(imageUri)
        onClickEvent()

        mWonderFont = Typeface.createFromAsset(assets, "beyond_wonderland.ttf")

        val llmTools = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvTools.layoutManager = llmTools
        mRvTools.adapter = mEditingToolsAdapter

        val llmFilters = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvFilters.layoutManager = llmFilters
        mRvFilters.adapter = mFilterViewAdapter

        val pinchTextScalable = intent.getBooleanExtra(PINCH_TEXT_SCALABLE_INTENT_KEY, true)
        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
            .setPinchTextScalable(pinchTextScalable) // set flag to make text scalable when pinch
            //.setDefaultTextTypeface(mTextRobotoTf)
            //.setDefaultEmojiTypeface(mEmojiTypeFace)
            .build() // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this)
        mSaveFileHelper = FileSaveHelper(this)

    }


    private fun onClickEvent() {
        mPhotoEditorView = binding.photoEditorView
        mTxtCurrentTool = binding.txtCurrentTool
        mRvTools = binding.rvConstraintTools
        mRvFilters = binding.rvFilterView
        mRootView = binding.rootView

        binding.imgCamera.setOnClickListener(this)

        binding.imgGallery.setOnClickListener(this)

        binding.imgClose.setOnClickListener(this)

        binding.dashbackIV.setOnClickListener(this)

        binding.imgSave.setOnClickListener(this)

        binding.logoutdashIV.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imgCamera -> {

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        val permission = arrayOf<String>(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        requestPermissions(permission, 1001)

                    } else {
                        openCamera()
                    }
                } else {
                    openCamera()
                }*/

                requestCameraPermission()
            }

            R.id.imgGallery ->{
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permissions, 1001)
                    } else {
                        chooseImageGallery()
                    }
                } else {
                    chooseImageGallery()
                }*/

                takePhotoFromGallery()
            }

            R.id.imgClose ->{
                binding.rvConstraintTools.visibility = View.VISIBLE
                binding.rvFilterView.visibility = View.GONE
            }

            R.id.dashbackIV -> {
                binding.linearLayout3.visibility = View.VISIBLE
                binding.chooseTV.visibility = View.VISIBLE
                binding.logoutdashTV.visibility = View.VISIBLE
                binding.logoutdashIV.visibility = View.VISIBLE
                binding.displayLL.visibility = View.GONE
            }
            R.id.imgSave -> {
                saveImage()
            }
            R.id.logoutdashIV -> {
                dialogbox()
            }
        }
    }

    private fun takePhotoFromGallery() {
        val galleryIntent = if (Build.VERSION.SDK_INT >= 33) {
            Intent(MediaStore.ACTION_PICK_IMAGES)
        } else {
            Intent(Intent.ACTION_PICK)
        }
        galleryIntent.type = "image/*"
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        resultLauncher.launch(galleryIntent)
    }
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        if (it.data?.data != null){
            val photo = it.data!!.data?.let { it1 -> uriToBitmap(it1) }
            imageBitmap = photo
            mPhotoEditorView.source.setImageBitmap(photo)
            imageUri = it.data!!.data!!
            imageBitmap = uriToBitmap(imageUri)
            Constant.imageBitmap = photo
            if (photo != null){
                binding.linearLayout3.visibility = View.GONE
                binding.chooseTV.visibility = View.GONE
                binding.logoutdashTV.visibility = View.GONE
                binding.logoutdashIV.visibility = View.GONE
                binding.displayLL.visibility = View.VISIBLE
            }
        }
    }

    private var isReadPermissionGranted = false

    private fun requestGalleryPermission(){
        isReadPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest : MutableList<String> = ArrayList()
        if (!isReadPermissionGranted){
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionRequest.isNotEmpty()){
            galleryLauncher1.launch(permissionRequest.toTypedArray())
        }else{
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getImage.launch(galleryIntent)
        }
    }
    private val galleryLauncher1 : ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){

        isReadPermissionGranted = it[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
        /*if (isReadPermissionGranted){

            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getImage.launch(galleryIntent)

        }*/
        if (isReadPermissionGranted){
            val galleryIntent = if (Build.VERSION.SDK_INT >= 33) {
                Intent(MediaStore.ACTION_PICK_IMAGES)
            } else {
                Intent(Intent.ACTION_PICK)
            }
            galleryIntent.type = "image/*"
            galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            getImage.launch(galleryIntent)
        }



    }
    private var getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data?.data != null){
            val photo = it.data!!.data?.let { it1 -> uriToBitmap(it1) }
            imageBitmap = photo
            mPhotoEditorView.source.setImageBitmap(photo)
            imageUri = it.data!!.data!!
            imageBitmap = uriToBitmap(imageUri)
            Constant.imageBitmap = photo
            if (photo != null){
                binding.linearLayout3.visibility = View.GONE
                binding.chooseTV.visibility = View.GONE
                binding.logoutdashTV.visibility = View.GONE
                binding.logoutdashIV.visibility = View.GONE
                binding.displayLL.visibility = View.VISIBLE
            }
        }
    }

    private var isCameraPermissionGranted = false

    private fun requestCameraPermission(){
        isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val permissionRequest : MutableList<String> = ArrayList()
        if (!isCameraPermissionGranted){
            permissionRequest.add(android.Manifest.permission.CAMERA)
        }
        if (permissionRequest.isNotEmpty()){
            cameraLauncher.launch(permissionRequest.toTypedArray())
        }else{
            contract.launch(imageUri)
        }
    }

    private val cameraLauncher : ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){
        isCameraPermissionGranted = it[android.Manifest.permission.CAMERA] ?: isCameraPermissionGranted

        if (isCameraPermissionGranted){
            contract.launch(imageUri)
        }

    }




    private fun dialogbox(){
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Do you want to exit")
        dialog.setTitle("Alert!!")
        dialog.setCancelable(true)
        dialog.setPositiveButton("Yes"){
            _,_ ->
            val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)
            val edit = sharedPreferences.edit()
            edit.putString("login",null)
            edit.apply()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        dialog.setNegativeButton("No"){
            _,_ ->
        }
        dialog.create()
        dialog.show()

    }
    private fun chooseImageGallery(){
        galleryContract.launch("image/*")
    }
    private fun saveImage() {
        val fileName = System.currentTimeMillis().toString() + ".png"
        val hasStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (hasStoragePermission || FileSaveHelper.isSdkHigherThan28()) {
            showLoading("Saving...")
            mSaveFileHelper.createFile(fileName, object : FileSaveHelper.OnFileCreateResult {

                @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
                override fun onFileCreateResult(
                    created: Boolean,
                    filePath: String?,
                    error: String?,
                    uri: Uri?
                ) {
                    lifecycleScope.launch {
                        if (created && filePath != null) {
                            val saveSettings = SaveSettings.Builder()
                                .setClearViewsEnabled(true)
                                .setTransparencyEnabled(true)
                                .build()

                            val result = mPhotoEditor.saveAsFile(filePath, saveSettings)

                            if (result is SaveFileResult.Success) {
                                mSaveFileHelper.notifyThatFileIsNowPubliclyAvailable(contentResolver)
                                hideLoading()
                                showSnackbar("Image Saved Successfully")
                                if (uri != null) {
                                    imageBitmap = uriToBitmap(uri)
                                    imageBitmap = uriToBitmap(imageUri)
                                }
                                mPhotoEditorView.source.setImageBitmap(imageBitmap)

                                Constant.imageUri = uri

                                val intent = Intent(this@DashboardScreen, ImageSaveActivity::class.java)
                                startActivity(intent)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    binding.linearLayout3.visibility = View.VISIBLE
                                    binding.chooseTV.visibility = View.VISIBLE
                                    binding.logoutdashTV.visibility = View.VISIBLE
                                    binding.logoutdashIV.visibility = View.VISIBLE
                                    binding.displayLL.visibility = View.GONE
                                }, 1000)


                            } else {
                                hideLoading()
                                showSnackbar("Failed to save Image")
                            }
                        } else {
                            hideLoading()
                            error?.let { showSnackbar(error) }
                        }
                    }
                }
            })
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun openCamera() {
        contract.launch(imageUri)
    }


    lateinit var imageUri : Uri

    private fun createImageUri() : Uri?{
        val image = File(applicationContext.filesDir, "camera_photo.png")
        return FileProvider.getUriForFile(applicationContext,"com.example.photoeditor.dashBoard.FileProvider",image)
    }

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){
        val photo = uriToBitmap(imageUri)
        imageBitmap = photo
        mPhotoEditorView.source.setImageBitmap(photo)
        Constant.imageBitmap = photo
        if (photo != null){
            binding.linearLayout3.visibility = View.GONE
            binding.chooseTV.visibility = View.GONE
            binding.logoutdashTV.visibility = View.GONE
            binding.logoutdashIV.visibility = View.GONE
            binding.displayLL.visibility = View.VISIBLE
        }

    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream : InputStream? = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        }catch (e : Exception){
            Log.d("uriToBitmaperror","$e")
            null
        }
    }

    private val galleryContract = registerForActivityResult(ActivityResultContracts.GetContent()){
        val photo = it?.let { it1 -> uriToBitmap(it1) }
        imageBitmap = photo
        mPhotoEditorView.source.setImageBitmap(photo)
        imageUri = it!!
        imageBitmap = uriToBitmap(imageUri)
        Constant.imageBitmap = photo
        if (photo != null){
            binding.linearLayout3.visibility = View.GONE
            binding.chooseTV.visibility = View.GONE
            binding.logoutdashTV.visibility = View.GONE
            binding.logoutdashIV.visibility = View.GONE
            binding.displayLL.visibility = View.VISIBLE
        }
    }

    override fun onToolSelected(toolType: ToolType) {
        when (toolType) {
            ToolType.TEXT -> {
                mTxtCurrentTool.text = "Text"
                val textEditorDialogFragment = TextEditorDialogFragment.show(this)
                textEditorDialogFragment.setOnTextEditorListener(object :
                    TextEditorDialogFragment.TextEditorListener {
                    override fun onDone(inputText: String, colorCode: Int) {
                        val styleBuilder = TextStyleBuilder()
                        styleBuilder.withTextColor(colorCode)
                        mPhotoEditor.addText(inputText, styleBuilder)
                        mTxtCurrentTool.setText("Text")
                    }
                })
            }

            ToolType.FILTER -> {
                mTxtCurrentTool.text = "Filter"
//                showFilter(true)
                binding.rvConstraintTools.visibility = View.GONE
                binding.rvFilterView.visibility = View.VISIBLE
            }

            ToolType.CROP -> {
                mTxtCurrentTool.text = "Crop"
                if (crop){
                    val uri = imageBitmap?.let { saveImgToCache(it) }
                        Log.d("uriImage","$uri")
                        if (uri != null){
                            imageUri = uri
                        }
                }

                imageUri?.let {
                    startCrop(it)
                }
                imageBitmap = uriToBitmap(imageUri)
            }
            ToolType.ROTATE -> {
                mTxtCurrentTool.text = "Rotate"
                Log.d("imageUri","$imageUri")

                val bitmap = uriToBitmap(imageUri)!!
                rotateImg(bitmap)
            }
        }
    }
    private fun bitmapToUri(imageBitmap: Bitmap?) {
        val cacheDir = baseContext.cacheDir
        val f = File(cacheDir, "pic")

        try {
            val out = FileOutputStream(
                f
            )
            imageBitmap?.compress(
                Bitmap.CompressFormat.JPEG,
                100, out
            )
            out.flush()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun rotateImg(bitmap: Bitmap){
        val matrix = Matrix()
        matrix.postScale(1F,1F)
        if (degree >= 360F) {
            degree = 0F
        }
        degree += 90F
        Log.d("degree","$degree")
        matrix.postRotate(degree)

        val bitmap2 = Bitmap.createBitmap(bitmap, 0, 0,bitmap.width, bitmap.height, matrix, true)

        mPhotoEditorView.source.setImageURI(null)
        mPhotoEditorView.source.setImageBitmap(bitmap2)
        imageBitmap = bitmap2
        crop = true
//        val uri = saveImgToCache(bitmap2)
//        Log.d("uriImage","$uri")
//        if (uri != null){
//            imageUri = uri
//        }

    }

    private fun saveImgToCache(bitmap: Bitmap): Uri?{
        var cachePath : File?
        val fileName : String = "crop.jpg"
        try {
            cachePath = File(applicationContext.filesDir,".")
            cachePath.mkdirs()

            val stream = FileOutputStream("$cachePath/$fileName")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            //return getImageUri(cachePath,fileName)
            val newFile = File(cachePath, fileName)
            return FileProvider.getUriForFile(applicationContext, "com.example.photoeditor.dashBoard.FileProvider",newFile)
        }catch (e : Exception){
            Log.e(TAG, "saveImgToCache error: $e")
        }
        return null
    }

    private fun startCrop(uri: Uri) {
        var destinationFileName = "SampleCropImage"

        destinationFileName += ".jpg"
        // Assuming cacheDir is a File object representing the directory where you want to save the file
        val cacheDir: File = applicationContext.cacheDir

        // Check if the cache directory exists, if not, create it
        if (!cacheDir.exists()) {
            cacheDir.mkdir() // This creates the directory if it doesn't exist
        }

        // Now, create the file using the cache directory and the destination file name
        val destinationFile = File(cacheDir, destinationFileName)

        // Assuming uri is the source Uri for the file you want to crop
        val sourceUri = Uri.parse(uri.toString())

        // Now, you can use the destinationFile and sourceUri to create the UCrop object
        val uCrop = UCrop.of(sourceUri, Uri.fromFile(destinationFile))
        setupFragment(uCrop)
        uCrop.start(this)

    }



    fun setupFragment(uCrop: UCrop) {
        fragment = uCrop.getFragment(uCrop.getIntent(this).extras)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, fragment, UCropFragment.TAG)
            .commitAllowingStateLoss()

        binding.displayLL.visibility = View.GONE
        binding.fragmentContainer.visibility = View.GONE
    }

    /*private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                //mProfileUri = fileUri
                imageUri = fileUri
                imageBitmap = uriToBitmap(imageUri)
                mPhotoEditorView.source.setImageBitmap(imageBitmap)

                //Constant.imageBitmap = photo
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("dddd","$resultCode")
        if (resultCode == RESULT_OK) {
            if (requestCode == requestMode) {
                val selectedUri = data?.data
                if (selectedUri != null) {
                    startCrop(selectedUri)
                } else {
                    Toast.makeText(this, "toast_cannot_retrieve_selected_image", Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                if (data != null) {
                    handleCropResult(data)
                }
            }
        }
        if (resultCode == 0) {
            binding.displayLL.visibility = View.VISIBLE
            binding.fragmentContainer.visibility = View.GONE
            if (data != null) {
                handleCropError(data)
            }
        }
    }


    override fun onFilterSelected(photoFilter: PhotoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter)
    }

    override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onAddViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
        val textEditorDialogFragment =
            TextEditorDialogFragment.show(this, text.toString(), colorCode)
        textEditorDialogFragment.setOnTextEditorListener(object :
            TextEditorDialogFragment.TextEditorListener {
            override fun onDone(inputText: String, colorCode: Int) {
                val styleBuilder = TextStyleBuilder()
                styleBuilder.withTextColor(colorCode)
                mPhotoEditor.editText(rootView, inputText, styleBuilder)
                mTxtCurrentTool.setText("Text")
            }
        })
    }

    override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onRemoveViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onStartViewChangeListener(viewType: ViewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onStopViewChangeListener(viewType: ViewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onTouchSourceImage(event: MotionEvent) {
        Log.d(TAG, "onTouchView() called with: event = [$event]")
    }

    override fun loadingProgress(showLoader: Boolean) {
        mShowLoader = showLoader
        supportInvalidateOptionsMenu()
    }

    override fun onCropFinish(result: UCropFragment.UCropResult?) {
        when (result!!.mResultCode) {
            RESULT_OK -> handleCropResult(result.mResultData)
            UCrop.RESULT_ERROR -> handleCropError(result.mResultData)
        }
        removeFragmentFromScreen()
    }
    private fun handleCropResult(result: Intent) {
        val resultUri = UCrop.getOutput(result)
        if (resultUri != null) {
            Constant.imageUri = resultUri

            imageUri = resultUri
            imageBitmap = uriToBitmap(imageUri)
            binding.displayLL.visibility = View.VISIBLE
            binding.fragmentContainer.visibility = View.GONE
            mPhotoEditorView.source.setImageBitmap(imageBitmap)

        } else {
            Toast.makeText(this, "toast_cannot_retrieve_cropped_image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleCropError(result: Intent) {
        binding.displayLL.visibility = View.VISIBLE
        binding.fragmentContainer.visibility = View.GONE
        val cropError = UCrop.getError(result)
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError)
            Toast.makeText(this, cropError.message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this,"unexpected_error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFragmentFromScreen() {
        supportFragmentManager.beginTransaction()
            .remove(fragment)
            .commit()
        binding.displayLL.setVisibility(View.VISIBLE)
    }

}