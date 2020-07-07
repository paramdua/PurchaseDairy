package com.kd.purchasedairy.ui.shop

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.kd.purchasedairy.Constant
import com.kd.purchasedairy.R
import kotlinx.android.synthetic.main.fragment_gallery.view.*


class ShopFragment : Fragment() {

    private val AUTOCOMPLETE_REQUEST_CODE = 22
    private val TAG: String = ShopFragment::class.java.simpleName
    lateinit var adapter: ShopAdapter
    var listProduct: ArrayList<ShopModel> = arrayListOf()
    var addDialog: Dialog? = null
    lateinit var placesClient: PlacesClient
    var name: EditText? = null
    var lat: TextView? = null
    var lng: TextView? = null
    var phn: EditText? = null
    var address: EditText? = null
    var latLng: LatLng? = LatLng(0.0, 0.0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        adapter = ShopAdapter(listProduct)
        root.recyclerShop.adapter = adapter
        Constant.getShopRef()
            .addSnapshotListener { querySnapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    return@addSnapshotListener
                }
                for (document in querySnapshot!!.documentChanges) {
                    when (document.type) {
                        DocumentChange.Type.ADDED -> {
                            val data = document.document.toObject(ShopModel::class.java)
                            listProduct.add(data)
                            if (this::adapter.isInitialized)
                                adapter.updateList(listProduct)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val data = document.document.toObject(ShopModel::class.java)

                        }
                        DocumentChange.Type.REMOVED -> {
                            val data = document.document.toObject(ShopModel::class.java)
                            if (listProduct.contains(data)) {
                                listProduct.remove(data)
                                if (this::adapter.isInitialized)
                                    adapter.updateList(listProduct)
                            }
                        }

                    }
                }
            }
        root.shopAdd.setOnClickListener {
            dialog()
        }
        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            activity?.let {
                Places.initialize(it, apiKey)
                placesClient = Places.createClient(it)
            }
        }

        return root
    }

    fun onSearchCalled(query: String) {
        // Set the fields to specify which types of place data to return.
        val fields: List<Place.Field> = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.PHONE_NUMBER
        )
        val token = AutocompleteSessionToken.newInstance()
        val bounds = RectangularBounds.newInstance(
            LatLng(30.9010, 75.8573),  //dummy lat/lng
            LatLng(31.9010, 76.8573)
        )
//        val request =
//            FindAutocompletePredictionsRequest.builder()
//                // Call either setLocationBias() OR setLocationRestriction().
////                .setLocationBias(bounds)
//                //.setLocationRestriction(bounds)
//                .setOrigin(LatLng(30.9010, 75.8573))
//                .setCountry("IN")
//                .setSessionToken(token)
//                .setQuery(query)
//                .build()
//        placesClient.findAutocompletePredictions(request)
//            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
//                for (prediction in response.autocompletePredictions) {
//                    Log.i(TAG, prediction.placeId)
//                    Log.i(TAG, prediction.getPrimaryText(null).toString())
//                }
//            }.addOnFailureListener { exception: Exception? ->
//                if (exception is ApiException) {
//                    Log.e(TAG, "Place not found: " + exception.statusCode)
//                }
//            }
        // Start the autocomplete intent.
        val intent = activity?.let {
            Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
//                .setCountry("IN")

//                .setTypeFilter(TypeFilter.ADDRESS)
                .setLocationBias(
                    RectangularBounds.newInstance(
                        LatLng(30.9010, 75.8573),
                        LatLng(30.9320, 76.9016)

                    )
                )
                .build(it)
        }
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun dialog() {
        addDialog = activity?.let { Dialog(it) }
        val lp2 = WindowManager.LayoutParams()
        lp2.copyFrom(addDialog?.window?.attributes)
        addDialog?.setContentView(R.layout.dialog_add_shop)
        val btnSubmit = addDialog?.findViewById<Button>(R.id.submit)
        val search = addDialog?.findViewById<TextView>(R.id.search)

        name = addDialog?.findViewById<EditText>(R.id.name)
        lat = addDialog?.findViewById(R.id.lat)
        lng = addDialog?.findViewById(R.id.lng)

        address = addDialog?.findViewById<EditText>(R.id.address)
        phn = addDialog?.findViewById<EditText>(R.id.phn)
        btnSubmit?.setOnClickListener {
            if (TextUtils.isEmpty(name?.text) || TextUtils.isEmpty(address?.text)
                || TextUtils.isEmpty(phn?.text)
            ) {
                Toast.makeText(activity, "Kindly Enter All Fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            insetData(name?.text.toString(), phn?.text.toString(), address?.text.toString())
        }
        search?.setOnClickListener {
//            if (!TextUtils.isEmpty(name?.text))
            onSearchCalled(name?.text.toString())
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

    private fun insetData(name: String, phn: String, address: String) {
        val value = hashMapOf(
            Constant.SHOP_PHN to phn,
            Constant.SHOP_NAME to name,
            Constant.SHOP_ADDRESS to address,
            Constant.SHOP_LAT to latLng?.latitude,
            Constant.SHOP_LNG to latLng?.longitude,
            Constant.SHOP_ID to listProduct.size
        )
        Constant.getShopRef().document(name).set(value).addOnSuccessListener {
            Log.e("ProductScreen", "dataAdded")
            addDialog?.dismiss()
        }.addOnFailureListener {
            Log.e("ProductScreen", "dataAdditionError")
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                Log.i(
                    TAG,
                    "Place: " + place.name + ", " + place.id + ", " + place.address
                )
                name?.setText(place.name)
                phn?.setText(place.phoneNumber)
                address?.setText(place.address)
                latLng = place.latLng
                lat?.text = "Latitude : " + latLng?.latitude.toString()
                lng?.text = "Longitude : " + latLng?.longitude.toString()
                Toast.makeText(
                    activity,
                    "ID: " + place.id + "address:" + place.address + "Name:" + place.name + " latlong: " + place.latLng,
                    Toast.LENGTH_LONG
                ).show()
                val address = place.address
                // do query with address
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Toast.makeText(
                    activity,
                    "Error: " + status.statusMessage,
                    Toast.LENGTH_LONG
                ).show()
                Log.i(TAG, status.statusMessage)
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}