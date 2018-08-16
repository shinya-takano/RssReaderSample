package com.reader.rss.takanoshin.rssreader2

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

/**
 * RssアプリActivity
 */
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

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return false // 常に非表示とする
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            rootView.section_label.text = getString(R.string.section_format, getCategoryTitle(arguments?.getInt(ARG_SECTION_NUMBER)))
            rootView.section_rv.setHasFixedSize(true)
            rootView.section_rv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            rootView.section_rv.isNestedScrollingEnabled = false
            rootView.section_rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            return rootView
        }

        override fun onResume() {
            super.onResume()

            // RSS の取得とAdapterへの設定
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
                    val adapter = RssRecycleViewAdapter(context, createRowData(list))
                    view?.section_rv?.adapter = adapter
                }

                override fun onError() {
                    Log.e(this.javaClass.simpleName, "rss取得失敗エラー")
                }
            })
        }

        /**
         * ページタイトル取得
         *
         * @param pageIndex ページインデックス
         * @return ページタイトル
         */
        private fun getCategoryTitle(pageIndex: Int?): String {
           return when (pageIndex) {
                0 -> "総合"
                1 -> "世の中"
                2 -> "政治と経済"
                3 -> "暮らし"
                else -> "総合"
            }
        }

        /**
         * 行データ作成
         * RSS情報から必要な情報のみを行データとして作成する
         *
         * @param items RSS記事情報リスト
         * @return 行データリスト
         */
        private fun createRowData(items: List<Article>?): List<RowData> {

            val rowDataList = arrayListOf<RowData>()
            if (items == null) {
                return rowDataList
            }

            for (item in items) {
                val data = RowData()
                data.title = item.title
                data.detail = item.description
                data.link = item.link
                data.image = item.image

                rowDataList.add(data)
            }
            return rowDataList
        }
    }
}
