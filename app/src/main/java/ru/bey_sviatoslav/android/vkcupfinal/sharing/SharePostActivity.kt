package ru.bey_sviatoslav.android.vkcupfinal.sharing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vk.api.sdk.*
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKApiExecutionException
import com.vk.api.sdk.ui.VKConfirmationActivity.Companion.result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import ru.bey_sviatoslav.android.vkcupfinal.sharing.commands.VKWallPostCommand
import ru.bey_sviatoslav.android.vkcupfinal.sharing.utils.PathUtils
import ru.bey_sviatoslav.android.vkcupfinal.R
import ru.bey_sviatoslav.android.vkcupfinal.groups.GroupsActivity
import timber.log.Timber
import kotlinx.coroutines.launch


class SharePostActivity : AppCompatActivity(), View.OnClickListener {

    private val GALLERY_REQUEST_CODE = 1889
    private val CAMERA_REQUEST_CODE = 1888

    private lateinit var imageView: ImageView
    private lateinit var choosePhotoImageButton: ImageButton
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var photoImageView: ImageView
    private lateinit var commentEditText: EditText

    private lateinit var selectedImage: Uri
    private var ownerId: Int = 0

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            // token expired
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_post)

        supportActionBar?.hide()

        val intent = intent
        ownerId = intent.getIntExtra(GroupsActivity.GROUP_ID, 0)

        initViews()

        loginVK()

    }

    private fun initViews() {
        imageView = findViewById(R.id.image_view)
        choosePhotoImageButton = findViewById<ImageButton>(R.id.choose_photo_image_button).apply {
            setOnClickListener(this@SharePostActivity)
        }

        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_bottom_sheet_share,
            findViewById<LinearLayout>(R.id.bottom_sheet_container)
        )

        photoImageView = bottomSheetView.findViewById(R.id.image_view)
        commentEditText = bottomSheetView.findViewById(R.id.comment_edit_text)

        bottomSheetView.findViewById<ImageButton>(R.id.dismiss_image_button).setOnClickListener {
            commentEditText.text = null
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<ImageButton>(R.id.send_button).setOnClickListener {
            sharePost()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
    }

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.choose_photo_image_button -> {
                pickFromGallery()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                // User passed authorization
            }

            override fun onLoginFailed(errorCode: Int) {
                // User didn't pass authorization
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (resultCode == Activity.RESULT_OK) when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                photoImageView.setImageURI(data!!.data)
                selectedImage = Uri.parse(PathUtils.getPath(this@SharePostActivity, data.data!!))
                bottomSheetDialog.show()
            }
        }
    }

    private fun loginVK() {
        if (!VK.isLoggedIn()) {
            VK.login(this, arrayListOf(VKScope.WALL, VKScope.PHOTOS))
        }
        VK.addTokenExpiredHandler(tokenTracker)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"

        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun sharePost() {
        val comment = commentEditText.text.toString()
        val photos = ArrayList<Uri>()
        photos.add(selectedImage)
        val vkWallPostCommand = VKWallPostCommand(comment, photos, -ownerId)
        VK.execute(vkWallPostCommand, object : VKApiCallback<Int> {
            override fun success(result: Int) {
                Log.d("Share", result.toString())
                val a = 1
            }

            override fun fail(error: VKApiExecutionException) {
                Log.d("Share", error.toString())
            }
        })
    }
}
