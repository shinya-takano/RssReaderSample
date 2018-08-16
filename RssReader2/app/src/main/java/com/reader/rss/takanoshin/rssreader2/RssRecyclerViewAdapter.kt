package com.reader.rss.takanoshin.rssreader2

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

/**
 * RssアプリRecyclerViewAdapter
 */
class RssRecycleViewAdapter(private val context: Context?, private val list: List<RowData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        /** 行Top */
        private const val ROW_TYPE_TOP = 0
        /** 行Top以外 */
        private const val ROW_TYPE_OTHER = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val inflate = LayoutInflater.from(parent.context).inflate(R.layout.row_top, parent, false)
            RowTopViewHolder(inflate)
        } else {
            val inflate = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
            RowViewHolder(inflate)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RowTopViewHolder) {
            holder.titleView.text = list[position].title
            holder.detailView.text = list[position].detail
            /* background にサムネイルを表示する方がよかったが、画像が小さ過ぎる場合があり、適切な表示とならないため断念
            Picasso.with(context).load(list[position].image).into(object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    Log.d("TAG", "Prepare Load");
                }

                override fun onBitmapFailed(errorDrawable: Drawable?) {
                    Log.d("TAG", "FAILED");
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    holder.itemView.background = BitmapDrawable(context?.resources, bitmap)
                }

            })
            */
            // クリック処理
            holder.itemView.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(list[position].link))
                context?.startActivity(i)
            }
        }
        if (holder is RowViewHolder) {
            holder.titleView.text = list[position].title
            // holder.detailView.text = list[position].detail
            Picasso.with(context).load(list[position].image).into(holder.image)
            // クリック処理
            holder.itemView.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(list[position].link))
                context?.startActivity(i)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ROW_TYPE_TOP
        } else {
            ROW_TYPE_OTHER
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class RowTopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var titleView: TextView = itemView.findViewById<View>(R.id.title) as TextView
    var detailView: TextView = itemView.findViewById<View>(R.id.detail) as TextView
}


class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var titleView: TextView = itemView.findViewById<View>(R.id.title) as TextView
    var image: ImageView = itemView.findViewById<View>(R.id.image) as ImageView
}


