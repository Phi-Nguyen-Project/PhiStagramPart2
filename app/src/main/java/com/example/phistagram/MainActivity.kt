package com.example.phistagram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.parse.*
import java.io.File
import java.lang.Exception

/**
 * Let user create a post by taking photo with their camera.
 */
class MainActivity : AppCompatActivity() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Setting the description of the post
        // 2. A button to launch camera to take picture
        // 3. An image view to show the picture that user has taken
        // 4. A button to save the post and send it to our parse sever


        findViewById<Button>(R.id.btnSubmit).setOnClickListener{
            // Send post to sever
            // Get description from user input in description box
            val description = findViewById<EditText>(R.id.description).text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null){
                submitPost(description,user, photoFile!!)
            }else{

            }
        }

        findViewById<Button>(R.id.btnTakePicture).setOnClickListener{
            // Launch camera app to let user take picture
            onLaunchCamera()
        }

        // 1. Setting the description of the post
        // 2. A button to launch the camera to take the picture
        // 3. The image view to show the picture that user has taken
        // 4. A button to save and send the post to our parse sever


        queryPosts()

    }

    // Send user post to our parse sever
    fun submitPost(description: String, user: ParseUser, file: File){
        // Create the post object
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground { exception ->
            if (exception != null) {
                // Something has gone wrong
                Log.e(TAG, "Error While saving Post")
                exception.printStackTrace()
            }else{
                Log.e(TAG,"successfully saved post")
            }
        }
    }

    // Function to launch the camera app
    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if( resultCode == RESULT_OK){
                // By this time, we have the photo file on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)

                // Load the taken image to the preview
                val ivPreview: ImageView = findViewById(R.id.imageView)
                ivPreview.setImageBitmap(takenImage)
            }else{
                Toast.makeText(this, "Image was not taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }


    // Query for all post in our server
    fun queryPosts(){
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all posts objects
        query.include(Post.KEY_USER)

        query.findInBackground(object : FindCallback<Post>{
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null ){
                    // Something has gone wrong
                    Log.e(TAG,"Error fetching posts")
                }else{
                    if (posts != null){
                        for (post in posts) {
                            Log.i(TAG, "Post: " + post.getDescription() + " , username: "+
                                    post.getUser()?.username)
                        }
                    }
                }
            }

        })
    }

    companion object{
        const val TAG = "MainActivity"
    }

}