package com.ongraph.realestate.ui.activities

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.response.GeneralResponse
import com.ongraph.realestate.bean.response.ProfileResponse
import com.ongraph.realestate.callbacks.AppCallBackListner
import com.ongraph.realestate.rest.ApiClient
import com.ongraph.realestate.rest.ApiInterface
import com.ongraph.realestate.utils.AppConstants
import com.ongraph.realestate.utils.AppUtils
import com.ongraph.realestate.utils.DialogUtils
import com.ongraph.realestate.utils.SharedPrefsHelper
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*

class CompleteProfileActivity : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog
    private var destination: File? = null
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)

        initValues()
    }

    private fun initValues() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait")
        progressDialog.setCancelable(false)

        rlLogout.visibility = View.GONE
        btnSave.visibility = View.VISIBLE
        etFName.isEnabled = true
        etLName.isEnabled = true
        etRole.isEnabled = true
        etDesc.isEnabled = true

        ivEdit.setImageResource(R.mipmap.camera_icon)

        ivProfile.setOnClickListener {
            uploadImage()
        }

        btnSave.setOnClickListener {
            AppUtils.hideKeyboard(this)
            if (validate()) {
                if (AppUtils.isConnected(this)) {
                    editProfileApi()
                } else {
                    AppUtils.showToast(
                        findViewById(android.R.id.content), getString(R.string.chk_network)
                    )
                }
            }
        }
    }

    private fun uploadImage() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermissionForCamera()) {
                    openCameraAndGalleryChooser()
                } else
                    requestPermissionForCamera()
            } else {
                openCameraAndGalleryChooser()
            }
        } catch (e: Exception) {
            AppUtils.showToast(findViewById(android.R.id.content), "Camera Permission error")
            e.printStackTrace()
        }
    }

    private fun requestPermissionForCamera(): Boolean {

        val camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val read =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val write =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val permissions = ArrayList<String>()

        if (camera != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (write != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissions.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    permissions.toTypedArray(), AppConstants.CAMERA_GALLERY_REQUEST_CODE
                )
            }
        } else {
            uploadImage()
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppConstants.CAMERA_GALLERY_REQUEST_CODE) {
            val hasCameraPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            )
            val hasWriteExternalPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val hasReadExternalPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (hasCameraPermission == PackageManager.PERMISSION_GRANTED && hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED
                && hasReadExternalPermission == PackageManager.PERMISSION_GRANTED
            ) {
                uploadImage()
            }
        }
    }

    private fun checkPermissionForCamera(): Boolean {
        val cameraPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        val readStoragePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        val writeExternalStorage =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return !(cameraPermission != PackageManager.PERMISSION_GRANTED || readStoragePermission != PackageManager.PERMISSION_GRANTED || writeExternalStorage != PackageManager.PERMISSION_GRANTED)
    }

    private fun openCameraAndGalleryChooser() {
        val options = arrayOf<CharSequence>(
            resources.getString(R.string.camera),
            resources.getString(R.string.gallery),
            resources.getString(R.string.alert_dialog_cancel)
        )
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.select_option))
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == resources.getString(R.string.camera) -> {
                    dialog.dismiss()
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, AppConstants.CAMERA_REQUEST_CODE)
                }
                options[item] == resources.getString(R.string.gallery) -> {
                    dialog.dismiss()
                    val pickPhoto =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, AppConstants.GALLERY_REQUEST_CODE)
                }
                options[item] == resources.getString(R.string.alert_dialog_cancel) -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            AppConstants.CAMERA_REQUEST_CODE -> run {
                try {
                    bitmap = data!!.extras!!.get("data") as Bitmap
                    destination = AppUtils.getImageFile(bitmap, "image_" + UUID.randomUUID())
//                    bitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, false)
                    ivProfile.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            AppConstants.GALLERY_REQUEST_CODE -> {
                try {
                    val selectedImage = data!!.data
                    if (selectedImage != null) {
                        try {
                            bitmap =
                                MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                            destination =
                                AppUtils.getImageFile(bitmap, "image_" + UUID.randomUUID())
//                            bitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, false)
                            ivProfile.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun validate(): Boolean {
        if (TextUtils.isEmpty(etFName.text.toString().trim())) {
            etFName.error = getString(R.string.valid_fname)
            etFName.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(etLName.text.toString().trim())) {
            etLName.error = getString(R.string.valid_lname)
            etLName.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(etRole.text.toString().trim())) {
            etRole.error = getString(R.string.valid_role)
            etRole.requestFocus()
            return false
        }
        /*if (TextUtils.isEmpty(etDesc.text.toString().trim())) {
            etDesc.error = getString(R.string.valid_bg)
            etDesc.requestFocus()
            return false
        }*/
        return true
    }

    private fun editProfileApi() {
        progressDialog.show()
        try {
            var imageFile: MultipartBody.Part? = null
            val map: HashMap<String, RequestBody> = HashMap()
            map["firstName"] =
                RequestBody.create(MediaType.parse("text/plain"), etFName!!.text.toString().trim())
            map["lastName"] =
                RequestBody.create(MediaType.parse("text/plain"), etLName!!.text.toString().trim())
            map["role"] =
                RequestBody.create(MediaType.parse("text/plain"), etRole.text.toString().trim())
            map["description"] =
                RequestBody.create(
                    MediaType.parse("text/plain"),
                    etDesc.text.toString().trim()
                )

            try {
                if (destination != null) {
                    val requestFile: RequestBody =
                        RequestBody.create(MediaType.parse("image/png"), destination)
                    imageFile = MultipartBody.Part.createFormData(
                        "profilepic",
                        destination!!.name + ".png",
                        requestFile
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val call: Call<ResponseBody>
            val apiService = ApiClient.getClient()!!.create(ApiInterface::class.java)
            if (imageFile != null)
                call = apiService.editProfile(map, imageFile)
            else
                call = apiService.editProfile(map)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>?,
                    response: Response<ResponseBody>?
                ) {
                    progressDialog.dismiss()
                    try {
                        val gson = Gson()
                        if (response!!.body() != null && response.isSuccessful) {
                            val mResponse = gson.fromJson(
                                response.body()!!.string(), ProfileResponse::class.java
                            )
                            DialogUtils.showAlertDialog(
                                this@CompleteProfileActivity,
                                mResponse.getMessage()!!,
                                object : AppCallBackListner.DialogClickCallback {
                                    override fun onButtonClick() {
                                        startActivity(
                                            Intent(
                                                this@CompleteProfileActivity,
                                                HomeActivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                                })
                            SharedPrefsHelper.getInstance().saveLoginData(mResponse.getData())
                            SharedPrefsHelper.getInstance()
                                .save("isProfileSetup", mResponse.getData()!!.isProfileSetup)
                            SharedPrefsHelper.getInstance()
                                .save(
                                    "isEmailVerified",
                                    SharedPrefsHelper.getInstance().loginData.isEmailVerified
                                )
                        } else if (response.errorBody() != null) {
                            val error = gson.fromJson(
                                response.errorBody()!!.string(),
                                GeneralResponse::class.java
                            )
                            if (error.getMessage() != null) {
                                AppUtils.showToast(
                                    findViewById(android.R.id.content),
                                    error.getMessage()!!
                                )
                            } else {
                                AppUtils.showToast(
                                    findViewById(android.R.id.content),
                                    getString(R.string.somethingWrong)
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppUtils.showToast(
                            findViewById(android.R.id.content),
                            getString(R.string.somethingWrong)
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    progressDialog.dismiss()
                    t!!.message
                    AppUtils.showToast(findViewById(android.R.id.content), t.message.toString())
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

