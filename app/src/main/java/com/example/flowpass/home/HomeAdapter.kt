package com.example.flowpass.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.flowpass.database.DatabaseFilter
import com.example.flowpass.database.Entry
import com.example.flowpass.databinding.ListEntriesBinding
import java.lang.Exception

/**
 * Code that acts as a bridge between the app UI and the database. This object is used to
 * bind data to the home table and add this screen on the frontend. Also passes UI data to
 * the [DatabaseFilter]
 */
class HomeAdapter(
    activity: FragmentActivity,
    private val clickListener: PasswordListener,
    entries: List<Entry>?
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    // Data that is passed to the UI from this object, using LiveData bindings
    var data: List<Entry> = emptyList()
        get() {
            return getTuples()
        }
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    // The database filter bound to this adapter; for interacting with db
    private var dbFilter: DatabaseFilter

    init {
        if (entries != null) data = entries
        dbFilter = DatabaseFilter(activity)
        Log.i("DatabaseAdapter", "created")
    }

    // Returns the number of items present in data
    override fun getItemCount() = data.size

    // Binds all items to the UI upon object creation
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, clickListener)
    }

    // Get the view using db data, slap it on the UI
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // Interface to [DatabaseFilter]
    fun getTuples(): List<Entry> {
        return try {
            dbFilter.getEntries()
        } catch (err: Exception) {
            err.printStackTrace()
            emptyList()
        }
    }

    /**
     * ViewHolder helper class; defines how to extract data from the adapter onto the UI
     */
    class ViewHolder private constructor(
        val binding: ListEntriesBinding
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Entry, clickListener: PasswordListener) {
            binding.entry = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListEntriesBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

/**
 * A class that watches user taps a password entry on [HomeFragment], then prepares the
 * edit and delete entry buttons
 */
class PasswordListener(val clickListener: (entryId: Int) -> Unit) {
    fun onClick(entry: Entry) = entry._id?.let { clickListener(it) }
}