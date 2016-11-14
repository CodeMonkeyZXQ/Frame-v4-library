package com.etong.android.frame.library.search;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.etong.android.frame.library.R;
import com.etong.android.frame.publisher.HttpMethod;
import com.etong.android.frame.publisher.HttpPublisher;
import com.etong.android.frame.subscriber.BaseSubscriberActivity;
import com.etong.android.frame.utils.ListAdapter;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouxiqing on 2016/10/12.
 */
public class searchBrandOrCarsetActivity extends BaseSubscriberActivity {
    private EditText editText;
    private RecyclerView recyclerView;
    private SqlLiteDao sqlLiteDao;
    private List<BrandCarset> mList;
    //    private ListAdapter<BrandCarset> mListAdapter;
    private  ListAdapter mListAdapter;

    @Override
    protected void onInit(@Nullable Bundle bundle) {
        setContentView(R.layout.activity_search);
        sqlLiteDao = SqlLiteDao.getInstance(this);
        mList = new ArrayList<>();
        initView();
    }

    LinearLayoutManager linearLayoutManager;
    protected void initView() {
        editText = findViewById(R.id.editText, EditText.class);
        recyclerView = findViewById(R.id.listView, RecyclerView.class);

        // 设置布局管理器
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        addClickListener(R.id.button);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editText.getText().toString();
                if (TextUtils.isEmpty(str)) {
                    recyclerView.setVisibility(View.GONE);
                    return;
                }
//                mList = sqlLiteDao.find(str);
                mList.clear();
                mList.addAll(sqlLiteDao.find(str));
                if (mList == null) {
                    return;
                }
                if (mList.isEmpty()) {
                    toastMsg("未找到相关数据");
                    recyclerView.setVisibility(View.GONE);
                    return;
                }
                recyclerView.setVisibility(View.VISIBLE);
                mListAdapter.notifyDataSetChanged();
            }
        });
        mListAdapter = new ListAdapter(this,mList);
        recyclerView.setAdapter(mListAdapter);
    }

    @Override
    protected void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
//                loadStart("初始化数据中。。。", 0);
                Map map = new HashMap() {
                };
                HttpMethod method = new HttpMethod("http://192.168.10.167:8090/etong2sc-app-consumer/search/getAll", map);
                HttpPublisher.getInstance().sendRequest(method, "getBrandOrCarset");
                break;
        }
    }

    @Subscriber(tag = "getBrandOrCarset")
    protected void onGetTripListFinish(HttpMethod method) {
        // 加载完成
        loadFinish();

        int code = method.data().getIntValue("errCode");
        Boolean status = method.data().getBoolean("status");
        String message = method.data().getString("msg");
        if (code != 0) {
            toastMsg("GetTripList error!");
            return;
        }

        if (!status) {
            if (!TextUtils.isEmpty(message)) {
                toastMsg(message);
            } else {
                toastMsg("初始化数据失败");
            }
            return;
        }
        JSONArray array = method.data().getJSONArray("data");
        if (array == null || array.isEmpty()) {
            toastMsg("初始化数据失败");
            return;
        } else {
            //数据写入数据库中
            sqlLiteDao.insert(array);
        }
    }


    public class ListAdapter extends
            RecyclerView.Adapter<ListAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private List<BrandCarset> mDatas;

        public ListAdapter(Context context, List<BrandCarset> datats) {
            mInflater = LayoutInflater.from(context);
            mDatas = datats;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View arg0) {
                super(arg0);
            }

            TextView tv1;
            TextView tv2;
            TextView tv3;
            TextView tv4;
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        /**
         * 创建ViewHolder
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.layout_list_item, viewGroup,
                    false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.tv1 = (TextView) view.findViewById(R.id.item_tv1);
            viewHolder.tv2 = (TextView) view.findViewById(R.id.item_tv2);
            viewHolder.tv3 = (TextView) view.findViewById(R.id.item_tv3);
            viewHolder.tv4 = (TextView) view.findViewById(R.id.item_tv4);
            return viewHolder;
        }

        /**
         * 设置值
         */
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            viewHolder.tv1.setText("Sid:" + mDatas.get(i).getSid());
            viewHolder.tv2.setText("id:" + mDatas.get(i).getId());
            viewHolder.tv3.setText("name:" + mDatas.get(i).getName());
            viewHolder.tv4.setText("type:" + mDatas.get(i).getType());
        }
    }
}
