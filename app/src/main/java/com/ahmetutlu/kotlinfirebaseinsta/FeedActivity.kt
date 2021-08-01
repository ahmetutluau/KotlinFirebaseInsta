package com.ahmetutlu.kotlinfirebaseinsta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmetutlu.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var db: FirebaseFirestore//database imizi burdada tanımladık
    private lateinit var postArrayList: ArrayList<Post>

    var userEmailFromFB:ArrayList<String> = ArrayList()
    var userCommentFromFB:ArrayList<String> = ArrayList()
    var userImageFromFB:ArrayList<String> = ArrayList()

    var adapter:FeedRecylerAdapter?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        auth= FirebaseAuth.getInstance()//auth u burda da tanımlıyoruz
        db= FirebaseFirestore.getInstance()

        postArrayList=ArrayList<Post>()

        getDataFromFirestore() // oluşturduğun fonksiyonu oncreate altında göstermen lazım

        //RecyclerView-> kullanmamızın sebebi listview'ün çok resimli ve geri dönüşlü uygulamalarda verimsiz kalması

        var layoutManager=LinearLayoutManager(this)//recyclerview'ün layoutManager'ı listview gibi hareket etmesini sağlar
        recyclerView.layoutManager=layoutManager

        adapter=FeedRecylerAdapter(userEmailFromFB,userCommentFromFB,userImageFromFB)
        recyclerView.adapter= adapter

    }

    // aşağıdaki onCreateOptionsMenu ve onOptionsItemSelected menüyü feedactiviteye bağlamamıza yarar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.options_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.add_post){//eğer Add Post a tıklarsan upload activiteye gider
            //upload
            val intent=Intent(applicationContext,UploadActivity::class.java)
            startActivity(intent)
        }else if (item.itemId==R.id.logout){// eğer logout a tıklarsan çıkış yapar sonrada mainactivite ye gider
            //logout
            auth.signOut()
            val intent=Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()

        }
        return super.onOptionsItemSelected(item)
    }
    //Verileri çekmek
    fun getDataFromFirestore() { //collectionPath veri kaydettiğimiz yerde yazan isimle aynı aynı olmalı
        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if(exception!=null){
                Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }else{
                if (snapshot!=null){
                    if (!snapshot.isEmpty){ // eğer !'i başına koyarsan olumsuzluk verir

                        //for döngüsüne girmeden önce aynı görsellerin dönmemesi için temizleme işlemi yapılır
                        postArrayList.clear()

                        val documents=snapshot.documents
                        for (documents in documents){//tırnak içine anahtar kelime(field) yazıyoruz,verileri kaydettiğimiz isimleri
                            //Casting
                            val comment=documents.get("comment") as String
                            val userEmail=documents.get("userEmail") as String
                            val dowloadUrl=documents.get("dowloadUrl") as String
                            //val timestamp=documents.get("date") as com.google.firebase.Timestamp
                            //val date=timestamp.toDate()//timestamp'i date'e çeviriyoruz

                            println(comment)

                            val post=Post(userEmail,comment,dowloadUrl)
                            postArrayList.add(post)


                        }
                        adapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}