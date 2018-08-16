package com.reader.rss.takanoshin.rssreader2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*


class MainActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position)
        }

        override fun getCount(): Int {
            return 4
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onResume() {
            super.onResume()

            val category = arguments?.getInt(ARG_SECTION_NUMBER)
            val urlString = when (category) {
                0 -> "http://b.hatena.ne.jp/hotentry.rss" // 総合
                1 -> "http://b.hatena.ne.jp/hotentry/social.rss" // 世の中
                2 -> "http://b.hatena.ne.jp/hotentry/economics.rss" // 政治と経済
                3 -> "http://b.hatena.ne.jp/hotentry/life.rss" // 暮らし
                else -> "http://b.hatena.ne.jp/hotentry.rss" // 総合
            }

            val parser = Parser()
            parser.execute(urlString)
            parser.onFinish(object : Parser.OnTaskCompleted {

                override fun onTaskCompleted(list: ArrayList<Article>) {
                    val adapter = CasarealRecycleViewAdapter(createDataset2(list))
                    view?.section_rv?.adapter = adapter
                }

                override fun onError() {
                    Log.e(this.javaClass.simpleName, "rss取得失敗エラー")
                }
            })
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            rootView.section_label.text = getString(R.string.section_format, getCategoryTitle(arguments?.getInt(ARG_SECTION_NUMBER)))
            rootView?.section_rv?.setHasFixedSize(true)
            rootView.section_rv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            rootView.section_rv.isNestedScrollingEnabled = false
            rootView.section_rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            return rootView
        }

        private fun getCategoryTitle(index: Int?): String {
           return when (index) {
                0 -> "総合"
                1 -> "世の中"
                2 -> "政治と経済"
                3 -> "暮らし"
                else -> "総合"
            }
        }

        private fun createDataset2(items: List<Article>?): List<RowData> {

            val dataset = arrayListOf<RowData>()
            if (items == null) {
                return dataset
            }

            for (item in items) {
                val data = RowData()
                data.title = item.title
                data.detail = item.description
                data.link = item.link
                data.image = item.image

                dataset.add(data)
            }
            return dataset
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private const val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }

        inner class RowTopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var titleView: TextView
            var detailView: TextView

            init {
                titleView = itemView.findViewById<View>(R.id.title) as TextView
                detailView = itemView.findViewById<View>(R.id.detail) as TextView
            }
        }


        inner class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var titleView: TextView
            // var detailView: TextView
            var image: ImageView


            init {
                titleView = itemView.findViewById<View>(R.id.title) as TextView
                // detailView = itemView.findViewById<View>(R.id.detail) as TextView
                image = itemView.findViewById<View>(R.id.image) as ImageView
            }
        }


        inner class CasarealRecycleViewAdapter(private val list: List<RowData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                    0
                } else {
                    1
                }
            }

            override fun getItemCount(): Int {
                return list.size
            }
        }
    }




}
