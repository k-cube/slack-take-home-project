package com.slack.exercise.dataBinding

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * A generic ViewHolder that works with a [ViewDataBinding].
 * @param <T> The type of the ViewDataBinding.
 *
 * Source: https://github.com/android/architecture-components-samples/blob/main/GithubBrowserSample/app/src/main/java/com/android/example/github/ui/common/DataBoundViewHolder.kt
 *
 * @see DataBoundListAdapter
 */
class DataBoundViewHolder<out T : ViewDataBinding> constructor(
        val binding: T
) : RecyclerView.ViewHolder(binding.root)