package com.example.phistagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phistagram.MainActivity
import com.example.phistagram.Post
import com.example.phistagram.PostAdapter
import com.example.phistagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery

open class FeedFragment : Fragment() {

    lateinit var postsRecyclerView: RecyclerView
    lateinit var adapter: PostAdapter

    var allPosts : MutableList<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // This is where we set up our view and click listener
            postsRecyclerView = view.findViewById(R.id.postRecyclerView)

        // Set adapter on the recycler view
        adapter = PostAdapter(requireContext(), allPosts)
        postsRecyclerView.adapter = adapter

        // Set layout manager on the recycler view
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Query posts
        queryPosts()

    }

    // Query for all post in our server
    open fun queryPosts(){
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all posts objects
        query.include(Post.KEY_USER)
        query.addDescendingOrder("createdAt")
        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null ){
                    // Something has gone wrong
                    Log.e(TAG,"Error fetching posts")
                }else{
                    if (posts != null){
                        for (post in posts) {
                            Log.i(TAG, "Post: " + post.getDescription() + " , username: "+
                                    post.getUser()?.username)
                        }
                        allPosts.addAll(posts)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }

    companion object{
        const val TAG = "FeedFragment"
    }

}