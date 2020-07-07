package com.kd.purchasedairy.ui.product

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.solver.widgets.ResolutionNode.REMOVED
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.kd.purchasedairy.Constant
import com.kd.purchasedairy.R
import kotlinx.android.synthetic.main.fragment_home.view.recyclerProduct
import kotlinx.android.synthetic.main.fragment_slideshow.view.*


class ProductFragment : Fragment() {

    var listProduct: ArrayList<ProductModel> = arrayListOf()
    var addDialog: Dialog? = null
    lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
//        fetchProduct(root)
        root.productAdd.setOnClickListener {
            dialog()
        }
        adapter = ProductAdapter(listProduct)
        root.recyclerProduct.adapter = adapter
        Constant.getProdRef()
            .addSnapshotListener { querySnapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    return@addSnapshotListener
                }
                for (document in querySnapshot!!.documentChanges) {
                    when (document.type) {
                        DocumentChange.Type.ADDED -> {
                            val data = document.document.toObject(ProductModel::class.java)
                            listProduct.add(data)
                            if (this::adapter.isInitialized)
                                adapter.updateList(listProduct)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val data = document.document.toObject(ProductModel::class.java)

                        }
                        DocumentChange.Type.REMOVED -> {
                            val data = document.document.toObject(ProductModel::class.java)
                            if (listProduct.contains(data)) {
                                listProduct.remove(data)
                                if (this::adapter.isInitialized)
                                    adapter.updateList(listProduct)
                            }
                        }

                    }
                }
            }
        return root
    }

    private fun dialog() {
        addDialog = activity?.let { Dialog(it) }
        val lp2 = WindowManager.LayoutParams()
        lp2.copyFrom(addDialog?.window?.attributes)
        addDialog?.setContentView(R.layout.dialog_add_product)
        val btnSubmit = addDialog?.findViewById<Button>(R.id.submit)
        val name = addDialog?.findViewById<EditText>(R.id.name)
        val price = addDialog?.findViewById<EditText>(R.id.price)
        btnSubmit?.setOnClickListener {
            if (TextUtils.isEmpty(name?.text) || TextUtils.isEmpty(price?.text)) {
                Toast.makeText(activity, "Kindly Enter Both Feild", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            insetData(name?.text.toString(), price?.text.toString())
        }
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp2.gravity = Gravity.BOTTOM
        lp2.windowAnimations = R.style.DialogAnimation
        addDialog?.window!!.attributes = lp2
        val params = addDialog?.window!!.attributes
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        params.dimAmount = .6f
        addDialog?.window!!.attributes = params
        addDialog?.window!!.setBackgroundDrawableResource(R.drawable.top_rounded_border)
        addDialog?.show()
    }

    private fun insetData(name: String, price: String) {
        val value = hashMapOf(
            Constant.PRODUCT_PRICE to price.toInt(),
            Constant.PRODUCT_NAME to name,
            Constant.PRODUCT_ID to listProduct.size
        )
        Constant.getProdRef().document(name).set(value).addOnSuccessListener {
            Log.e("ProductScreen", "dataAdded")
            addDialog?.dismiss()
        }.addOnFailureListener {
            Log.e("ProductScreen", "dataAdditionError")
        }
    }

    fun fetchProduct(root: View) {
        Constant.getProdRef().get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val data = document.toObject(ProductModel::class.java)
                    listProduct.add(data)
                    Log.e("Document List", document.id)
                }
            }
        }
    }
}