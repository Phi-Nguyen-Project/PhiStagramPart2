package com.example.phistagram.fragments

import android.util.Log
import com.example.phistagram.Post
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser
import okhttp3.internal.parseCookie

class ProfileFragment : FeedFragment() {

    override fun queryPosts(){
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all posts objects
        query.include(Post.KEY_USER)

        // Only return all the post of the current user
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser())

        // Return all post in the descending order
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

}