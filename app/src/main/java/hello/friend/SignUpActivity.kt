package hello.friend

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SignUpActivity : AppCompatActivity () {

    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var accountAlready: TextView
    private lateinit var btnSelectPhoto: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var filepath: Uri
    private var storageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        supportActionBar?.hide()

        editName = findViewById(R.id.editName)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        accountAlready = findViewById(R.id.accountAlready)

        accountAlready.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener {
            val name = editName.text.toString()
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()

            signUp(name, email, password)
        }

        storageRef = FirebaseStorage.getInstance().reference

        btnSelectPhoto.setOnClickListener {
            uploadFile()
        }
    }

        private fun uploadFile() {

            val intent = Intent()
            intent.setType("images/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Photo"), 111)

        }

    private fun signUp(name: String, email: String, password: String) {
        // Create a user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Add a user to the database
                    addUserToDatabase(name,email,auth.currentUser?.uid!!)
                    // Log in a user
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    finish()
                    startActivity(intent)

                    uploadImageToFirebaseStorage()

                } else {
                    Toast.makeText(this@SignUpActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        dbRef = FirebaseDatabase.getInstance("https://hello-friend-app-default-rtdb.europe-west1.firebasedatabase.app/").reference
        // Create a unique id for every user
        dbRef.child("User").child(uid).setValue(User(name,email,uid))
    }

    private fun uploadImageToFirebaseStorage() {

        //val filename = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("gs://hello-friend-app.appspot.com")

        storageRef.putFile(filepath)
            .addOnSuccessListener {
                Log.d("SignUpActivity", "Uploaded image: ${it.metadata?.path}")
            }
        storageRef.downloadUrl.addOnSuccessListener {
            Log.d("SignUpActivity", "File location: $it")

        }

    }
}