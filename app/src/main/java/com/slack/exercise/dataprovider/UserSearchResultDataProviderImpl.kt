package com.slack.exercise.dataprovider

import com.slack.exercise.api.SlackApi
import com.slack.exercise.api.TermNotFound
import com.slack.exercise.model.UserSearchResult
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [UserSearchResultDataProvider].
 */
@Singleton
class UserSearchResultDataProviderImpl @Inject constructor(
    private val slackApi: SlackApi) : UserSearchResultDataProvider {

    val cacheResponse = mutableMapOf<String, Set<UserSearchResult>>()

    /**
     * Returns a [Single] emitting a set of [UserSearchResult].
     */
    override fun fetchUsers(searchTerm: String): Single<Set<UserSearchResult>> {
        cacheResponse[searchTerm]?.let {
            return Single.just(it)
        }
        return slackApi.searchUsers(searchTerm)
            .map {
                val results = it.map { user ->
                    UserSearchResult(
                            username = user.username,
                            imageUrl = user.avatarUrl,
                            fullName = user.displayName
                    )
                }.toSet()
                cacheResponse[searchTerm] = results
                results
            }
    }
}