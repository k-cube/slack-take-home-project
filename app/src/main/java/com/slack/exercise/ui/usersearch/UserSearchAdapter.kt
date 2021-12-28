package com.slack.exercise.ui.usersearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.slack.exercise.dataBinding.DataBoundViewHolder
import com.slack.exercise.databinding.ItemUserSearchBinding
import com.slack.exercise.model.UserSearchResult

/**
 * Adapter for the list of [UserSearchResult].
 */
class UserSearchAdapter : RecyclerView.Adapter<DataBoundViewHolder<ItemUserSearchBinding>>() {
    private var userSearchResults: List<UserSearchResult> = emptyList()

    fun setResults(results: Set<UserSearchResult>) {
        userSearchResults = results.toList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<ItemUserSearchBinding> =
            DataBoundViewHolder(ItemUserSearchBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
            ))


    override fun getItemCount(): Int {
        return userSearchResults.size
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder<ItemUserSearchBinding>, position: Int) {
        holder.binding.user = userSearchResults[position]
    }
}