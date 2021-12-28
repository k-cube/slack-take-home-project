package com.slack.exercise.ui.usersearch

import com.slack.exercise.model.UserSearchResult

/**
 * MVP contract for User Search.
 */
interface UserSearchContract {

  /**
   * Callbacks to notify the view of the outcome of search queries initiated.
   */
  interface View {
    /**
     * Call when [UserSearchResult] are returned.
     */
    fun onUserSearchResults(results: Set<UserSearchResult>)

    /**
     * Call when an error occurs during the execution of search queries.
     */
    fun onUserSearchError()

    /**
     * Check weather [searchTerm] exist in the deny list.
     */
    fun termExistInDenyList(searchTerm: String): Boolean

    /**
     * Check weather [searchTerm] exist in the deny list.
     */
    fun addTermToDenyList(searchTerm: String)

    /**
     * Call when api returns not found for a specific term
     */
    fun showSearchNotFoundState()

    /**
     * Call when search term exists in the deny list
     */
    fun showSearchTermDenied(term: String)

    /**
     * Call when start performing search
     */
    fun showLoadingState()

    /**
    * Call when finish performing search
    */
    fun hideLoadingState()
  }

  interface Presenter {
    /**
     * Call to attach a [Presenter] and provide its [View].
     */
    fun attach(view: View)

    /**
     * Call to detach a [Presenter] and clean up resources.
     */
    fun detach()

    /**
     * Notifies the presenter that the [searchTerm] has changed.
     */
    fun onQueryTextChange(searchTerm: String)
  }
}