package com.bilibili.lingxiao.home.live

import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log

import android.view.View
import android.widget.Toast
import com.bilibili.lingxiao.R
import com.bilibili.lingxiao.utils.ToastUtil

import com.camera.lingxiao.common.app.BaseFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_live_play.*
import kotlinx.android.synthetic.main.fragment_live.view.*
import kotlin.properties.Delegates
import android.support.v7.widget.RecyclerView



class LiveFragment :BaseFragment() ,LiveView{

    var livePresenter: LivePresenter = LivePresenter(this,this)
    val TAG = LiveFragment::class.java.simpleName
    var liveList = arrayListOf<MultiItemLiveData>()

    private var liveAdapter:LiveRecyAdapter by Delegates.notNull()
    private var refresh: SmartRefreshLayout by Delegates.notNull()
    override val contentLayoutId: Int
        get() = R.layout.fragment_live

    override fun initWidget(root: View) {
        super.initWidget(root)
        var manager = GridLayoutManager(context,10,GridLayoutManager.VERTICAL,false)
        manager.setSpanSizeLookup(object :GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                var type = liveAdapter.data.get(position).itemType
                Log.e(TAG,"获取到的类型"+type)
                when(type){
                    MultiItemLiveData.BANNER-> return 10
                    MultiItemLiveData.CATEGORY-> return 2
                    MultiItemLiveData.RECOMMEND-> return 10
                    MultiItemLiveData.PARTITION-> return 10
                    else-> return 0
                }
            }
        })
        //让互相嵌套的RecyclerView的item都进入同一个共享池
        val recycledViewPool = RecyclerView.RecycledViewPool()
        root.live_recy.setRecycledViewPool(recycledViewPool)
        liveAdapter = LiveRecyAdapter(liveList,recycledViewPool)
        root.live_recy.adapter = liveAdapter
        root.live_recy.layoutManager = manager
        refresh = root.refresh
        refresh.autoRefresh()
        refresh.setOnRefreshListener {
            livePresenter.getLiveList(1)
        }
        livePresenter.getLiveList(1)

        liveAdapter.setMultiItemClickListener(object :LiveRecyAdapter.OnMultiItemClickListener{
            override fun onRecommendClick(live: LiveData.RecommendDataBean.LivesBean, position: Int) {
                ToastUtil.show("点击推荐："+live.playurl)
                goToPlay(live.playurl)
            }

            override fun onPartitionClick(live: LiveData.PartitionsBean.LivesBeanX, position: Int) {
                ToastUtil.show("点击下面的模块："+live.playurl)
                goToPlay(live.playurl)
            }

        })
    }

    var bannerData = MultiItemLiveData(MultiItemLiveData.BANNER)

    var recommendData = MultiItemLiveData(MultiItemLiveData.RECOMMEND)
    override fun onGetLiveList(data: LiveData) {
        liveList.clear()

        bannerData.bannerList = data.banner
        liveList.add(bannerData)

        for (category in data.entranceIcons){
            var categoryData = MultiItemLiveData(MultiItemLiveData.CATEGORY)
            categoryData.entranceIconsBean = category
            liveList.add(categoryData)
        }

        recommendData.liveList = data.recommend_data.lives
        liveList.add(recommendData)

        for (partition in data.partitions){
            var partitionData = MultiItemLiveData(MultiItemLiveData.PARTITION)
            partitionData.partitionsBean = partition
            liveList.add(partitionData)
        }
        /*var bannerData = LiveData(LiveData.BANNER)
        bannerData.banner = data.banner

        var categoryData = LiveData(LiveData.CATEGORY)
        categoryData.entranceIcons = data.entranceIcons

        var recommendData = LiveData(LiveData.RECOMMEND)
        recommendData.recommend_data = data.recommend_data

        var partitionData = LiveData(LiveData.PARTITION)
        partitionData.partitions = data.partitions*/

        liveAdapter.notifyDataSetChanged()
        refresh.finishRefresh()
    }

    fun goToPlay(play_url:String){
        val intent = Intent(context,LivePlayActivity::class.java)
        intent.putExtra("play_url",play_url)
        startActivity(intent)
    }

    override fun showDialog() {

    }

    override fun diamissDialog() {

    }

    override fun showToast(text: String?) {
        ToastUtil.show(text)
    }
}