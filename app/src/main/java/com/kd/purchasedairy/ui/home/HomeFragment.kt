package com.kd.purchasedairy.ui.home

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kd.purchasedairy.Constant
import com.kd.purchasedairy.R
import com.kd.purchasedairy.ui.product.ProductAdapter
import com.kd.purchasedairy.ui.product.ProductModel
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    var listProduct: ArrayList<String> = arrayListOf()
    var listProductDetail: ArrayList<ProductModel> = arrayListOf()
    val TAG = "Home Insert"
    var listShop = arrayListOf<String>()
    var listTimeline = arrayListOf<TimelineModel>()
    var addDialog: Dialog? = null
    lateinit var adapter: TimelineAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        root.timelineAdd.setOnClickListener {
            dialog()
        }
        adapter = TimelineAdapter(listTimeline)
        root.recyclerProduct.adapter = adapter
        Constant.getTimelineRef()
//            .orderBy("created")
            .addSnapshotListener { querySnapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    return@addSnapshotListener
                }
                for (document in querySnapshot!!.documentChanges) {
                    when (document.type) {
                        DocumentChange.Type.ADDED -> {
                            val data = document.document.toObject(TimelineModel::class.java)
                            listTimeline.add(data)
                            if (this::adapter.isInitialized)
                                adapter.updateList(listTimeline)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val data = document.document.toObject(TimelineModel::class.java)

                        }
                        DocumentChange.Type.REMOVED -> {
                            val data = document.document.toObject(TimelineModel::class.java)
                            if (listTimeline.contains(data)) {
                                listTimeline.remove(data)
                                if (this::adapter.isInitialized)
                                    adapter.updateList(listTimeline)
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
        addDialog?.setContentView(R.layout.dialog_add_timeline)
        val btnSubmit = addDialog?.findViewById<Button>(R.id.submit)
        val price = addDialog?.findViewById<EditText>(R.id.price)
        val shopSpinner = addDialog?.findViewById<Spinner>(R.id.shopSpinner)
        val productSpinner = addDialog?.findViewById<Spinner>(R.id.productSpinner)
        listProduct = arrayListOf()
        listProduct.add("Please Select")
        listShop = arrayListOf()
        listShop.add("Please Select")

        Constant.getShopRef().get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    listShop.add(document.id)

                    Log.e("Document", document.id + " => " + document.data)
                }
                val spinnerArrayAdapter: ArrayAdapter<String>? = activity?.let { it1 ->
                    ArrayAdapter<String>(
                        it1, R.layout.spinner_item,
                        listShop
                    )
                } //selected item will look like a spinner set from XML

                spinnerArrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                shopSpinner?.adapter = spinnerArrayAdapter
            }
        }
        Constant.getProdRef().get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    listProduct.add(document.id)
                    listProductDetail.add(document.toObject(ProductModel::class.java))

                    Log.e("Document", document.id + " => " + document.data)
                }
                val spinnerArrayAdapter: ArrayAdapter<String>? = activity?.let { it1 ->
                    ArrayAdapter<String>(
                        it1, R.layout.spinner_item,
                        listProduct
                    )
                } //selected item will look like a spinner set from XML

                spinnerArrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                productSpinner?.adapter = spinnerArrayAdapter
            }
        }

        btnSubmit?.setOnClickListener {
            if (TextUtils.isEmpty(price?.text) || productSpinner?.selectedItemPosition == 0 || shopSpinner?.selectedItemPosition == 0) {
                return@setOnClickListener
            }
            insetData(
                listProduct[productSpinner?.selectedItemPosition!!],
                price?.text.toString(),
                listShop[shopSpinner?.selectedItemPosition!!],
                listProductDetail[productSpinner.selectedItemPosition-1].specification
            )
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

    private fun insetData(name: String, price: String, shop: String, spec: String) {
        val value = hashMapOf(
            Constant.PRODUCT_PRICE to price.toInt(),
            "shop" to shop,
            Constant.PRODUCT_NAME to name,
            Constant.TIMELINE_ID to listProduct.size,
            "spec" to spec
        )
        Constant.getTimelineRef().document(name).set(value).addOnSuccessListener {
            addDialog?.dismiss()
        }.addOnFailureListener {
            Log.e("ProductScreen", "dataAdditionError")
        }
    }
}