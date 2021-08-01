 package com.ahmetutlu.kotlinfirebaseinsta

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_upload.*
import java.lang.Exception
import java.util.*

class UploadActivity : AppCompatActivity() {
    var selectedPicture: Uri?=null // Uri resmin telefonda nerde olduğunu verir
    var selectedBitmap: Bitmap?=null
    private lateinit var db:FirebaseFirestore
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()
    }
    fun imageViewClicked(view: View){
        //resim izni varmı bakıyoruz,izin verilmemişse izin istiyoruz verilmişsede galeriye gidiyoruz
    if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
    }else{
        val intentToGalery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intentToGalery,2)
    }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==1){
            if (grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //altta galeriye gider
                val intentToGalery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentToGalery,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode==2 && resultCode==Activity.RESULT_OK && data!= null){
            selectedPicture=data.data
            try {
                if(selectedPicture!=null){
                    if (Build.VERSION.SDK_INT>28){
                        val source=ImageDecoder.createSource(this.contentResolver,selectedPicture!!)
                        selectedBitmap=ImageDecoder.decodeBitmap(source)
                        uploadImageView.setImageBitmap(selectedBitmap)
                    }else{
                        selectedBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,selectedPicture)
                        uploadImageView.setImageBitmap(selectedBitmap)
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    fun uploadClicked(view: View){
        //UUID(universal unique id)->image name
        val uuid=UUID.randomUUID() //rastgele bir şekilde storage a kaydedilen resimlere isim atar
        val imageName="$uuid.jpg"

        val storage=FirebaseStorage.getInstance()
        val reference=storage.reference //reference burda storage a fotoyu göndermeye yarar
        // child herzaman alt satır demektir,yani burda images ın altın satırına image.jpg ekledik
        val imagesReference=reference.child("images").child(imageName)

        if(selectedPicture!=null){
            imagesReference.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot->
                //Database-Firestore
                //görsel indirme linki almak
                var uploadedPictureReference=FirebaseStorage.getInstance().reference.child("images").child(imageName)
                uploadedPictureReference.downloadUrl.addOnSuccessListener { uri->
                    val dowloadUrl=uri.toString()//uri kullanarak resmin url sini alıyoruz
                    println(dowloadUrl)

                    //database de veri kaydederken hasMapOf ile kaydetmemiz lazım
                    val postMap= hashMapOf<String,Any>()
                    postMap.put("dowloadUrl",dowloadUrl)
                    postMap.put("userEmail",auth.currentUser!!.email.toString())
                    postMap.put("comment",uploadCommentText.text.toString())
                    postMap.put("date",Timestamp.now()) //Timestamp firebase in kendi kodu burda amaç verileri tarihe göre sıralamak

                    db.collection("Posts").add(postMap).addOnCompleteListener { task->
                        if (task.isComplete && task.isSuccessful){
                            //eğer veriler başarıyla kaydedildiyse
                            finish()
                        }
                    }.addOnFailureListener { exception-> // kaydedilmediyse mantıklı bir cevap ver
                        Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}