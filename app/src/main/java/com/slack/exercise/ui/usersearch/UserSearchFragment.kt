package com.slack.exercise.ui.usersearch


import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.slack.exercise.R
import com.slack.exercise.databinding.FragmentUserSearchBinding
import com.slack.exercise.extensions.hide
import com.slack.exercise.extensions.show
import com.slack.exercise.model.UserSearchResult
import dagger.android.support.DaggerFragment
import timber.log.Timber
import java.io.*
import javax.inject.Inject

/**
 * Main fragment displaying and handling interactions with the view.
 * We use the MVP pattern and attach a Presenter that will be in charge of non view related operations.
 */
class UserSearchFragment : DaggerFragment(), UserSearchContract.View {

    @Inject
    internal lateinit var presenter: UserSearchPresenter

    private lateinit var userSearchBinding: FragmentUserSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        userSearchBinding = FragmentUserSearchBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return userSearchBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        setUpList()
    }

    override fun onStart() {
        super.onStart()

        presenter.attach(this)
    }

    override fun onStop() {
        super.onStop()

        presenter.detach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_user_search, menu)

        val searchView: SearchView = menu.findItem(R.id.search_menu_item).actionView as SearchView
        searchView.queryHint = getString(R.string.search_users_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                presenter.onQueryTextChange(newText)
                return true
            }
        })
    }

    override fun onUserSearchResults(results: Set<UserSearchResult>) {
        userSearchBinding.apply {
            userSearchResultList.show()
            errorField.hide()
        }
        val adapter = userSearchBinding.userSearchResultList.adapter as UserSearchAdapter
        adapter.setResults(results)
    }

    override fun onUserSearchError(error: Throwable) {
        Timber.e(error, "Error searching users.")
    }

    override fun showSearchNotFoundState() {
        userSearchBinding.apply {
            userSearchResultList.hide()
            errorField.show()
            errorField.text = "No user was found!"
        }
    }

    override fun showSearchTermDenied(term: String) {
        userSearchBinding.apply {
            userSearchResultList.hide()
            errorField.show()
            errorField.text = "The search term \"$term\" has been denied! Please search something else!"
        }
    }

    override fun showLoadingState() {
        userSearchBinding.apply {
            userSearchResultList.hide()
            errorField.hide()
            progressCircular.show()
        }
    }

    override fun hideLoadingState() {
        userSearchBinding.apply {
            progressCircular.hide()
        }
    }

    private fun setUpToolbar() {
        val act = activity as UserSearchActivity
        act.setSupportActionBar(userSearchBinding.toolbar)
    }

    private fun setUpList() {
        with(userSearchBinding.userSearchResultList) {
            adapter = UserSearchAdapter()
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }
    }

    override fun termExistInDenyList(searchTerm: String): Boolean {
        val originalInputStream = resources.openRawResource(R.raw.denylist)

        val bufferedReader = BufferedReader(InputStreamReader(originalInputStream))
        var eachLine = bufferedReader.readLine();
        while (eachLine != null) {
            if (eachLine.trim() == searchTerm) {
                return true
            }
            eachLine = bufferedReader.readLine()
        }

        // Check the secondary denied list in pref
        val listOfTerms = retrieveDeniedListFromPref()
        return listOfTerms.contains(searchTerm)
    }

    override fun addTermToDenyList(searchTerm: String) {
        val context = context ?: return

        val listOfCurrentTerms = retrieveDeniedListFromPref()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        val newList = mutableListOf<String>().apply {
            addAll(listOfCurrentTerms)
            add(searchTerm)
        }

        editor.putString(getString(R.string.deny_list_pref_key), Gson().toJson(newList))
        editor.apply()
    }

    private fun retrieveDeniedListFromPref() : List<String> {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val jsonList = sharedPreferences.getString(getString(R.string.deny_list_pref_key), "")
        val stringListType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson<List<String>>(jsonList, stringListType) ?: emptyList()
    }
}