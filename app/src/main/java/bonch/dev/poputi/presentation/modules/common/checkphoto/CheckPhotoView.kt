package bonch.dev.poputi.presentation.modules.common.checkphoto

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.poputi.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.check_photo_activity.*

class CheckPhotoView : AppCompatActivity() {

    private val PHOTO = "PHOTO"
    private val TITLE = "TITLE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_photo_activity)

        val photo = Uri.parse(intent.getStringExtra(PHOTO))
        val title = intent.getStringExtra(TITLE)
        setPhoto(photo)
        setTitle(title)

        setListeners()
    }


    private fun setPhoto(photo: Uri) {
        Glide.with(applicationContext).load(photo).into(photo_container)
    }


    private fun setTitle(title: String?) {
        if (title == null) {
            title_check_photo.visibility = View.GONE
        } else {
            title_check_photo.text = title
        }
    }


    private fun setListeners() {
        back.setOnClickListener {
            finish()
        }

        send_photo.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}