package cc.aoeiuv020.panovel.history


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cc.aoeiuv020.panovel.App.Companion.ctx
import cc.aoeiuv020.panovel.IView
import cc.aoeiuv020.panovel.R
import cc.aoeiuv020.panovel.data.entity.Novel
import cc.aoeiuv020.panovel.list.NovelListAdapter
import cc.aoeiuv020.panovel.main.MainActivity
import cc.aoeiuv020.panovel.settings.ListSettings
import kotlinx.android.synthetic.main.novel_item_list.*


/**
 * 绝大部分照搬书架，
 * Created by AoEiuV020 on 2017.10.15-18:07:39.
 */
class HistoryFragment : Fragment(), IView {
    private val novelListAdapter by lazy {
        NovelListAdapter(onError = ::showError)
    }
    private val presenter: HistoryPresenter = HistoryPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.novel_item_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvNovel.layoutManager = if (ListSettings.gridView) {
            GridLayoutManager(ctx, if (ListSettings.largeView) 3 else 5)
        } else {
            LinearLayoutManager(ctx)
        }
        rvNovel.adapter = novelListAdapter
        srlRefresh.setOnRefreshListener {
            forceRefresh()
        }

        presenter.attach(this)
    }

    override fun onDestroyView() {
        presenter.detach()
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    private fun refresh() {
        srlRefresh.isRefreshing = true
        presenter.refresh()
    }

    /**
     * 强行刷新，重新下载小说详情，主要是看最新章，
     */
    private fun forceRefresh() {
        novelListAdapter.refresh()
        refresh()
    }

    fun showNovelList(list: List<Novel>) {
        novelListAdapter.data = list
        srlRefresh.isRefreshing = false
    }

    fun showError(message: String, e: Throwable) {
        (activity as? MainActivity)?.showError(message, e)
    }
}