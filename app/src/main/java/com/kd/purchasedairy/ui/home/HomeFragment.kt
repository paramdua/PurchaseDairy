package com.kd.purchasedairy.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kd.purchasedairy.R
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    var listProduct: ArrayList<ProductModel> = arrayListOf()
    val TAG = "Home Insert"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        val db = Firebase.firestore
        db.collection("Product").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val data = document.toObject(ProductModel::class.java)
                    listProduct.add(data)
                    Log.e("Document List", document.id)
                }
                root.recyclerProduct.adapter = ProductAdapter(listProduct)
            }
        }

//        val city = hashMapOf(
//            "price" to 460,
//            "ProductId" to "2"
//        )
//        db.collection("Product")
//            .document("Nut").set(city)
//            .addOnSuccessListener {
//                Log.d(TAG, "DocumentSnapshot added with ID")
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error adding document", e)
//            }
        return root
    }
}