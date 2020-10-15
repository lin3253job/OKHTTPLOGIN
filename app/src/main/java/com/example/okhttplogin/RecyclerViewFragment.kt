package com.example.okhttplogin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import kotlinx.android.synthetic.main.item_view.view.*
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
class RecyclerViewFragment : Fragment() {

    var token: String? = null
    var data: String? = null
    private val scope = MainScope()
    var stockList = mutableListOf<StockContent>()
    val api =
        "http://present810209.twf.node.tw/api/getstockcode.php?token=60a6e15904f7843790228a17a1c1 3b08"
    private lateinit var stockAdapter: StockAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = activity?.getPreferences(0)?.getString("token", "").toString()
        if (token != "") {

            scope.launch {
                data = dataConnection(token!!)

                val itemType = object : TypeToken<List<StockContent>>() {}.type
                val gson = Gson()
                val stockList = (gson.fromJson<List<StockContent>>(
                    data,
                    itemType
                ) as List<StockContent>).toMutableList()
                Log.e("data", "stockList${stockList.size}")

                rvData.adapter = StockAdapter(stockList,scope)
            }

            rvData.layoutManager =LinearLayoutManager(activity)


        }


    }

    private suspend fun dataConnection(string: String) = withContext(Dispatchers.IO) {
        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType: MediaType? = MediaType.parse("application/x-www-form-urlencoded")
        val body = RequestBody.create(mediaType, "token=$data")

        val request = Request.Builder()
            .url(api)
            .method("POST", body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

//        Log.e("request",request.body().toString())

        val response: Response = client.newCall(request).execute()
        val successful = response.isSuccessful

        if (successful) {
            try {
                data = response.body()!!.string()

                val json = JSONObject(data)
                token = json.optString("data")
                Log.e("token=====", token!!)


            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(requireActivity(), "連線失敗", Toast.LENGTH_SHORT).show()
        }
        response.close()
        token


    }

}

 class StockAdapter(
     private val mData:MutableList<StockContent>,
     private val scope: CoroutineScope
 ) : RecyclerView.Adapter<StockAdapter.MyViewHolder>() {

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
         val v = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
         return MyViewHolder(v, scope)
     }

     override fun getItemCount(): Int {
         return mData.size
     }

     override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       val stockContent = mData[position]

         holder.tvCode.text = stockContent.stockcode
         holder.tvName.text = stockContent.stockname

     }

     class MyViewHolder(v: View, private val scope: CoroutineScope):RecyclerView.ViewHolder(v){


         var job: Job? = null

         val tvCode: TextView = itemView.tvCode
         val tvName: TextView = itemView.tvName


         fun loadImage(url: String) {
             job?.cancel()
             job = scope.launch {

             }
         }

     }



 }
