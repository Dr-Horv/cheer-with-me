package dev.fredag.cheerwithme

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import dev.fredag.cheerwithme.service.BackendService
import kotlinx.android.synthetic.main.friends_view.*
import org.json.JSONObject
import org.koin.android.ext.android.get

data class User(
    val id : Long,
    val nick : String,
    val avatarUrl: String? = null
)

class UserFriends(val friends: List<User>,
                  val incomingFriendRequests: List<User>,
                  val outgoingFriendRequests: List<User>)

class MyAdapter(private val myDataset: List<String>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.friend_nick)
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {
        // create a new view
        val constraintLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_row_item, parent, false) as ConstraintLayout
        return MyViewHolder(constraintLayout)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.text = myDataset[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}

class FriendsFragment : Fragment() {
    private lateinit var firstRecyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var currentLayoutManagerType: LayoutManagerType
    private lateinit var adapter: MyAdapter
    private val dataset: MutableList<String> = mutableListOf()
    enum class LayoutManagerType { GRID_LAYOUT_MANAGER, LINEAR_LAYOUT_MANAGER }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.friends_view,
            container, false)
        firstRecyclerView = rootView.findViewById(R.id.friendsFirstRecyclerView)


        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        layoutManager = LinearLayoutManager(activity)

        currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            currentLayoutManagerType = savedInstanceState
                .getSerializable(KEY_LAYOUT_MANAGER) as LayoutManagerType
        }
        setRecyclerViewLayoutManager(currentLayoutManagerType)

        firstRecyclerView.adapter = MyAdapter(dataset)

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        BackendService.getInstance(this.requireContext().applicationContext)
            .get("friends/", UserFriends::class.java) {
                when {
                    it.isSuccess -> {
                        dataset.clear()
                        dataset.addAll(it.getOrThrow().friends.map { user -> user.nick })
                        firstRecyclerView.adapter!!.notifyDataSetChanged()

                    }
                }
            }

    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    private fun setRecyclerViewLayoutManager(layoutManagerType: LayoutManagerType) {
        var scrollPosition = 0

        // If a layout manager has already been set, get current scroll position.
        if (firstRecyclerView.layoutManager != null) {
            scrollPosition = (firstRecyclerView.layoutManager as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition()
        }

        when (layoutManagerType) {
            FriendsFragment.LayoutManagerType.GRID_LAYOUT_MANAGER -> {
                layoutManager = GridLayoutManager(activity, SPAN_COUNT)
                currentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER
            }
            FriendsFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER -> {
                layoutManager = LinearLayoutManager(activity)
                currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER
            }
        }

        with(firstRecyclerView) {
            layoutManager = this@FriendsFragment.layoutManager
            scrollToPosition(scrollPosition)
        }

    }

    companion object {
        private val KEY_LAYOUT_MANAGER = "layoutManager"
        private val SPAN_COUNT = 2
    }


}
