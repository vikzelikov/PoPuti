package bonch.dev.presentation.modules.passanger.profile.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.R
import bonch.dev.domain.utils.Constants
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.check_photo_activity.*

class CheckPhotoView : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_photo_activity)

        val photo = Uri.parse(intent.getStringExtra(Constants.PHOTO))
        setPhoto(photo)

        setListeners()
    }


    private fun setPhoto(photo: Uri) {
        Glide.with(applicationContext).load(photo).into(photo_container)
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