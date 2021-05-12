//package com.ongraph.realestate.ui.adapter
//
//import android.content.Context
//import android.support.v7.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import com.bumptech.glide.request.RequestOptions
//
//class SmallImageAdapter(val context: Context, val mImageList: List<String>, val mCallback: AppCallBackListner.UploadImageListner) : RecyclerView.Adapter<SmallImageAdapter.ViewHolder>() {
//
//    var pos: Int = 0
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_small_image, parent, false)
//        return ViewHolder(v)
//    }
//
//    override fun getItemCount(): Int {
//        return mImageList.size
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Glide.with(context)
//                .load(mImageList[position])
//                .apply(RequestOptions().placeholder(R.mipmap.default_icon)
//                        .error(R.mipmap.default_icon).diskCacheStrategy(DiskCacheStrategy.ALL)).into(holder.image)
//
//        if (pos == position) {
//            holder.image.setBackgroundResource(R.drawable.view_outline_blue)
//        } else {
//            holder.image.setBackgroundResource(android.R.color.transparent)
//        }
//
//        holder.image.setOnClickListener {
//            pos = position
//            notifyDataSetChanged()
//            mCallback.getImage(mImageList[position], position)
//        }
//    }
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        val image = itemView.findViewById(R.id.image) as ImageView
//    }
//
//}