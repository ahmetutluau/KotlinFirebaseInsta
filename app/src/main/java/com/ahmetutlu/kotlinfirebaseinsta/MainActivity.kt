package com.ahmetutlu.kotlinfirebaseinsta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth= FirebaseAuth.getInstance()

        //anlık kullanıcı diye değişken oluşturup eğer o an zaten giriş yapmış bir kullanıcı varsa direk feedactiye gönderiyoruz
        val currentUser=auth.currentUser
        if (currentUser!=null){
            intent=Intent(applicationContext,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    //kullanıcı girişi
    fun signInClick(view: View){
        auth.signInWithEmailAndPassword(emailText.text.toString(),passwordText.text.toString()).addOnCompleteListener {task->
            if (task.isSuccessful){
                Toast.makeText(applicationContext, "Welcome:${auth.currentUser?.email.toString()}", Toast.LENGTH_SHORT).show()
                //yukardaki toast mesajında hangi kullanıcı girerse ona hoşgeldin demesi gereken bir kod yazdık
                val intent=Intent(applicationContext,FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception->
            if (exception!=null){
                Toast.makeText(applicationContext,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }
        }

    }


    //kullanıcı kayıdı,burdaki veriler firebase Authentication a aktarılır
    fun signUpClick(view: View){
        var email=emailText.text.toString()
        var password=passwordText.text.toString()

        //allta lamda gösterimini kullandık , internetten veri çekerken daha verimli olduğu için
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val intent=Intent(applicationContext,FeedActivity::class.java)
                startActivity(intent)
                finish()// kullanıcının kayıt yaptıktan sonra geri dönmemesi için finish() ekliyorz
            }
        }.addOnFailureListener { exception-> //burda exception sayesinde mantıklı toast mesajları gösterebiliyoruz
            if (exception!=null){
                Toast.makeText(applicationContext,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }
        }

    }
}