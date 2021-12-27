package com.slack.exercise.ui.usersearch

import com.slack.exercise.api.TermNotFound
import com.slack.exercise.dataprovider.UserSearchResultDataProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Presenter responsible for reacting to user inputs and initiating search queries.
 */
class UserSearchPresenter @Inject constructor(
    private val userNameResultDataProvider: UserSearchResultDataProvider
) : UserSearchContract.Presenter {

    private var view: UserSearchContract.View? = null
    private val searchQuerySubject = PublishSubject.create<String>()
    private val searchQueryStream by lazy {
        searchQuerySubject.debounce(200L, TimeUnit.MILLISECONDS)
    }
    private var searchQueryDisposable = Disposable.disposed()

    override fun attach(view: UserSearchContract.View) {
        this.view = view

        searchQueryDisposable = searchQueryStream
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapSingle({ searchTerm ->
                if (searchTerm.isEmpty()) {
                    Single.just(emptySet())
                } else {
                    view.showLoadingState()
                    val termExist: Boolean = view.termExistInDenyList(searchTerm)
                    if (!termExist) {
                        userNameResultDataProvider.fetchUsers(searchTerm)
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnError {
                                onFetchError(it, searchTerm)
                            }
                    } else {
                        view.apply {
                            hideLoadingState()
                            showSearchTermDenied(searchTerm)
                        }
                        Single.error(Throwable("Search term denied"))
                    }
                }
            }, true)
            .subscribe({ results ->
                this@UserSearchPresenter.view?.apply {
                    hideLoadingState()
                    onUserSearchResults(results)
                }
            }, {
                this@UserSearchPresenter.view?.hideLoadingState()
            })
    }

    private fun onFetchError(it: Throwable?, searchTerm: String) {
        this@UserSearchPresenter.view?.hideLoadingState()
        if (it is TermNotFound) {
            this@UserSearchPresenter.view?.apply {
                addTermToDenyList(searchTerm)
                showSearchNotFoundState()
            }
        } else {
            this@UserSearchPresenter.view?.showGenericErrorState()
        }
    }

    override fun detach() {
        view = null
        searchQueryDisposable.dispose()
    }

    override fun onQueryTextChange(searchTerm: String) {
        searchQuerySubject.onNext(searchTerm)
    }
}