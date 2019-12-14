package com.krayem.hearthstone.ui.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.krayem.hearthstone.di.DaggerViewModelInjector
import com.krayem.hearthstone.model.Card
import com.krayem.hearthstone.model.DefaultApiResponse
import com.krayem.hearthstone.model.ListApiResponse
import com.krayem.hearthstone.network.CardApi
import com.krayem.hearthstone.network.NetworkModule
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.StringReader
import javax.inject.Inject


class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    val allResponse:MutableLiveData<DefaultApiResponse> = MutableLiveData()

    @Inject
    lateinit var cardApi: CardApi

    private lateinit var subscription: Disposable

    init {
        DaggerViewModelInjector
            .builder()
            .networkModule(NetworkModule)
            .build()
            .inject(this)
    }


    fun getAll(){
        subscription = cardApi.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                allResponse.postValue(DefaultApiResponse.getLoading())
            }
            .doOnTerminate {
            }
            .subscribe(
                {
                    allResponse.postValue(ListApiResponse.getList(it.sortedWith(CardComparator())))
                },
                {
                    allResponse.postValue(DefaultApiResponse.getError())
                }
            )
    }

//    fun parseWithMoshi(context: Context){
//        viewModelScope.launch {
//            withContext(Dispatchers.IO){
//
//                val jsonfile: String =
//                    context.assets.open("cards.json").bufferedReader().use { it.readText() }
//                val all = JSONObject(jsonfile)
//
//                val basics = all.getJSONArray("Basic").toString()
//
//                val moshi: Moshi = Moshi.Builder().build()
//
//                val type = Types.newParameterizedType(
//                    MutableList::class.java,
//                    Card::class.java
//                )
//                val adapter: JsonAdapter<List<Card>> = moshi.adapter(type)
//
//                val cards: List<Card>? = adapter.fromJson(basics)
//                Log.e("done", cards?.size.toString())
//
//
//            }
//        }
//    }
//    fun parseJson(context: Context) {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                val jsonfile: String =
//                    context.assets.open("cards.json").bufferedReader().use { it.readText() }
//                val all = JSONObject(jsonfile)
//
//                val basics = all.getJSONArray("Basic").toString()
//
//
//                val klaxon = Klaxon()
//                JsonReader(StringReader(basics)).use { reader ->
//                    val result = arrayListOf<Card>()
//                    reader.beginArray {
//                        while (reader.hasNext()) {
//                            val card = klaxon.parse<Card>(reader)
//                            card?.let {
//                                result.add(it)
//                                allResponse.postValue(result)
//                                Log.e("added", it.name)
//                            }
//                        }
//                        Log.e("done",result.size.toString())
//                    }
//                }
//            }
//        }
//    }
}
