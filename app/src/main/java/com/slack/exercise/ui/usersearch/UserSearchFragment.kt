package com.slack.exercise.ui.usersearch


import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.slack.exercise.R
import com.slack.exercise.databinding.FragmentUserSearchBinding
import com.slack.exercise.extensions.addTermToDeniedList
import com.slack.exercise.extensions.hide
import com.slack.exercise.extensions.retrieveDeniedListFromPref
import com.slack.exercise.extensions.show
import com.slack.exercise.model.UserSearchResult
import dagger.android.support.DaggerFragment
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

    override fun onUserSearchError() {
        userSearchBinding.apply {
            userSearchResultList.hide()
            errorField.show()
            errorField.text = getString(R.string.generic_error_msg)
        }
    }

    override fun showSearchNotFoundState() {
        userSearchBinding.apply {
            userSearchResultList.hide()
            errorField.show()
            errorField.text = getString(R.string.user_not_found_error_msg)
        }
    }

    override fun showSearchTermDenied(term: String) {
        userSearchBinding.apply {
            userSearchResultList.hide()
            errorField.show()
            errorField.text = getString(R.string.term_denied_error_msg, term)
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
        val context = context ?: return false
        val originalInputStream = resources.openRawResource(R.raw.denylist)

        val bufferedReader = BufferedReader(InputStreamReader(originalInputStream))
        var eachLine = bufferedReader.readLine();
        while (eachLine != null) {
            val deniedTerm = eachLine.trim()
            if (searchTerm == deniedTerm || searchTerm.startsWith(deniedTerm)) {
                return true
            }
            eachLine = bufferedReader.readLine()
        }

        // Check the secondary denied list in pref
        val listOfTerms = context.retrieveDeniedListFromPref()
        listOfTerms.forEach { deniedTerm ->
            if (searchTerm == deniedTerm || searchTerm.startsWith(deniedTerm)) {
                return true
            }
        }
        return false
    }

    override fun addTermToDenyList(searchTerm: String) {
        context?.addTermToDeniedList(searchTerm)
    }
}