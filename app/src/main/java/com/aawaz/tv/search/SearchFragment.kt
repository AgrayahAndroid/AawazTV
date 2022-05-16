package com.aawaz.tv.search

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.aawaz.tv.R
import com.aawaz.tv.data.ResultData
import com.aawaz.tv.data.db.Album
import com.aawaz.tv.data.mapper.toSearchEpisode
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class SearchFragment1() : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider{
    private val mHandler = Handler()
    private var mRowsAdapter: ArrayObjectAdapter? = null
    private var mQuery: String? = null

    private var mResultsFound = false

    private val vm: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
//        mVideoCursorAdapter.mapper = VideoCursorMapper()
        setSearchResultProvider(this)
        setOnItemViewClickedListener(ItemViewClickedListener())



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isFireTV = Build.MODEL.equals("AFTB", ignoreCase = true)
        Log.d("debug", "we are here  ${isFireTV}")
        requireView().findViewById<View>(R.id.lb_search_bar_speech_orb).visibility = View.INVISIBLE
        requireView().findViewById<View>(R.id.lb_search_bar).requestFocus()
//        requireView().findViewById<View>(R.id.lb_search_bar).pla

        view.findViewById<SearchBar>(R.id.lb_search_bar).badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search_24)


        if (isTV()) {
            //this is overriding the default searchResultProvider, because of a bug in it
            view.findViewById<SearchBar>(R.id.lb_search_bar)
                .setSearchBarListener(object : SearchBar.SearchBarListener {
                    override fun onSearchQueryChange(query1: String?) {
                        if (query1 != null) {
                            onQueryTextChange(query1)
                        }
                    }

                    override fun onSearchQuerySubmit(query: String?) {
                        if (query != null) {
                            onQueryTextSubmit(query)
                        }
                    }

                    override fun onKeyboardDismiss(query: String?) {
                        onSearchQuerySubmit(" ")
                    }
                })
        }
    }

    override fun onPause() {
        mHandler.removeCallbacksAndMessages(null)
        super.onPause()
    }






    override fun getResultsAdapter(): ObjectAdapter {
        return mRowsAdapter!!
    }

    override fun onQueryTextChange(newQuery: String): Boolean {
        if (true) Log.i(TAG, String.format("Search text changed: %s", newQuery))
        if (newQuery.length>=3) {
            loadQuery(newQuery)
        }
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        if (true){
            Log.v(TAG, String.format("Search text submitted: %s", query))
        }
//        loadQuery(query)
        return true
    }

    fun hasResults(): Boolean {
        return mRowsAdapter!!.size() > 0 && mResultsFound
    }


    private fun loadQuery(query: String) {
        if (!TextUtils.isEmpty(query) && query != "nil") {
            mQuery = query
            vm.getData2(query)
            fetchAlbumDetails()
            requireView().findViewById<View>(R.id.lb_search_bar).requestFocus()
        }
    }
    private fun fetchAlbumDetails() {
        var titleRes: Int
        vm.album.observe(viewLifecycleOwner, Observer {
            when(val result = it as ResultData<List<NetworkSearchResult1>>){
                is ResultData.Loading -> {
                    Timber.d("XXXXXXXXX loading")
                }
                is ResultData.Success ->{
                    result.value?.let {

                        it.let {alb ->


                            val tracks = it.mapIndexed { index, networkEpisode ->
                                networkEpisode.toSearchEpisode()
                            }

                            if (tracks.isNotEmpty()){
                                titleRes = R.string.search_results
                            }else{
                                titleRes = R.string.no_search_results
                            }
                            val header = HeaderItem(getString(titleRes, mQuery))
                            mRowsAdapter!!.clear()
                            val listRowAdapter = ArrayObjectAdapter(SearchEpisodePresenter())
                            listRowAdapter.addAll(0, tracks)
                            val row = ListRow(header, listRowAdapter)
                            mRowsAdapter!!.add(row)
                        }

                        /*  it.results?.let { alb ->
                              //album = alb
                              val tracks = alb.mapIndexed { index, networkEpisode ->
                                  networkEpisode.toSearchEpisode(it)
                              }
  //                            Toast.makeText(activity, ""+tracks, Toast.LENGTH_SHORT).show()
                              val titleRes: Int = R.string.no_search_results
                              val header = HeaderItem(getString(titleRes, mQuery))
                              mRowsAdapter!!.clear()
                              val listRowAdapter = ArrayObjectAdapter(SearchEpisodePresenter(300))
                              listRowAdapter.addAll(0, tracks)
                              val row = ListRow(header, listRowAdapter)
                              mRowsAdapter!!.add(row)
                          }*/

                    }
                }
            }
        })
    }
    fun focusOnSearch() {
        requireView().findViewById<View>(R.id.lb_search_bar).requestFocus()
    }







    //    hdsjfjsggdfgfdsjgdfsfdfgfsghdgfj
    private inner class ItemViewClickedListener() : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder, item: Any,
            rowViewHolder: RowPresenter.ViewHolder, row: Row
        ) {
            if (item is Album){


                val metadata = item as Album?
                metadata?.let { album ->
                    val direction = SearchFragment1Directions.actionSearchFragmentToExoPlayerFragment(
                        album, null
                    )
                    val action = SearchFragment1Directions.actionSearchFragmentToAlbumDetailsFragment(album)
                    findNavController().navigate(action)

                }
            }else{
                Toast.makeText(activity, "false", Toast.LENGTH_SHORT).show()
            }

        }
    }

    companion object {
        private val TAG = "SearchFragment"
        private val FINISH_ON_RECOGNIZER_CANCELED = true
        private val REQUEST_SPEECH = 0x00000010
    }

    fun isTV(): Boolean {
        return Build.MODEL.contains("AFT")
    }

}
