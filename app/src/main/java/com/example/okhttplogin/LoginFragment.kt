package com.example.okhttplogin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {
    private val urlApi = "http://present810209.twf.node.tw/api/login.php"
    private val user: User? = null
    private val scope = MainScope()
    var name = user?.name
    private var password = user?.password
    private var token: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvName.setOnClickListener {
            etName.setText("cccccc")
            etPassword.setText("123456")
        }

        btSend.setOnClickListener {
            name = etName.text.toString()
            password = etPassword.text.toString()
            if (name == "" || password == "") {
                Toast.makeText(requireActivity(), "請完整輸入", Toast.LENGTH_SHORT).show()
            } else {
                scope.launch {
                    userConnect(name!!, password!!)

                    activity?.getPreferences(0)?.edit()?.putString("token",token)?.apply()
                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_recyclerViewFragment)
                }
            }
        }


    }

    private suspend fun userConnect(name: String, password: String) = withContext(Dispatchers.IO) {


        val client = OkHttpClient().newBuilder()
            .build()

        val mediaType: MediaType? = MediaType.parse("application/x-www-form-urlencoded")
        val body = RequestBody.create(mediaType, "username=$name&password=$password")
        val request = Request.Builder()
            .url(urlApi)
            .method("POST", body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
        val response: Response = client.newCall(request).execute()
        val successful = response.isSuccessful

        if (successful) {
            try {
                val string = response.body()!!.string()
                val json = JSONObject(string)
                token = json.optString("data")
//                Log.e("123123",token)

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

