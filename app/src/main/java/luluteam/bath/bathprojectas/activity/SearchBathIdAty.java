package luluteam.bath.bathprojectas.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.model.ToiletInfo;
import luluteam.bath.bathprojectas.tools.Repository;
import luluteam.bath.bathprojectas.utils.GsonUtil;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;


/**
 * Created by TC on 2018/4/23.
 */
public class SearchBathIdAty extends BaseActivity {

    private List<ToiletInfo.ToiletItem> toiletListData;

    private List<String> onlineToiletIdList;

    private LinearLayout search_ll;
    private TextInputEditText searchInfo_TIET;
    private Button search_btn;

    private LinearLayout searchResult_ll;
    private ToiletAdapter mAdapter;
    private ListView toiletId_lv;
    private Toolbar include_toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bath_id);
        initView();
        initData(getIntent());
    }

    private void initData(Intent data) {
        String province = data.getStringExtra("province");
        String city = data.getStringExtra("city");
        String district = data.getStringExtra("district");
        String from = data.getStringExtra("from");
        onlineToiletIdList = (List<String>) data.getSerializableExtra("onlineToiletIdList");
        if (province != null || city != null || district != null) {
            search_ll.setVisibility(View.GONE);
            loadToiletByLocation(province, city, district);
        } else if (from != null) {
            search_ll.setVisibility(View.GONE);
            loadToilets();
            include_toolbar.setTitle("远程控制");
        } else {
            search_ll.setVisibility(View.VISIBLE);
        }

    }

    private void initView() {
        search_ll = (LinearLayout) findViewById(R.id.search_ll);
        searchInfo_TIET = (TextInputEditText) findViewById(R.id.searchInfo_TIET);
        search_btn = (Button) findViewById(R.id.search_btn);
        toiletId_lv = (ListView) findViewById(R.id.toiletId_lv);
        include_toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        include_toolbar.setTitle("查询厕所");
        this.setSupportActionBar(include_toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        searchResult_ll = (LinearLayout) findViewById(R.id.searchResult_ll);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关闭输入法
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchInfo_TIET.getWindowToken(), 0);
                String pattern = searchInfo_TIET.getText().toString().trim();
                loadToiletByRegEx(pattern);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 省市区 的显示是否正确？
     */
    private void onChangeUI() {
        if (toiletListData != null && toiletListData.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new ToiletAdapter(this, toiletListData);
                toiletId_lv.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataChange(toiletListData);
            }
        }
    }

    /**
     * 查找当前用户权限允许的所有厕所
     */
    private void loadToilets() {
        loadToiletByLocation("", "", "");
    }


    /**
     * 通过 省市区 查找toilet信息
     *
     * @param province
     * @param city
     * @param district
     */
    private void loadToiletByLocation(String province, String city, String district) {
        HashMap<String, String> params = new HashMap<>();
        params.put("province", province.trim());
        params.put("city", city.trim());
        params.put("district", district == null ? "" : district.trim());
        params.put("username", APPConstant.USERNAME);
        OkHttpManager.CommonPostAsyn(WebConstant.GET_TOILET_INFO_FROM_LOCATION, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                Log.i(TAG, "按省市区查询厕所：" + result);
                if (state == OkHttpManager.State.SUCCESS) {
                    toiletListData = GsonUtil.fromJson(result, ToiletInfo.class).getToiletList();
                    if (onlineToiletIdList != null && onlineToiletIdList.size() > 0) {
                        for (ToiletInfo.ToiletItem item : toiletListData) {
                            if (onlineToiletIdList.contains(item.getToiletId())) {
                                item.setOnline(true);
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onSearchToiletResult(toiletListData);
                        }
                    });
                }
            }
        });
    }

    /**
     * 模糊查询 toilet信息
     *
     * @param pattern
     */
    private void loadToiletByRegEx(String pattern) {
        HashMap<String, String> params = new HashMap<>();
        params.put("pattern", pattern);
        params.put("username", APPConstant.USERNAME);
        OkHttpManager.CommonPostAsyn(WebConstant.GET_TOILET_INFO_FROM_REGEX, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                Log.i(TAG, "按地理位置模糊查询厕所：" + result);
                if (state.equals(OkHttpManager.State.SUCCESS)) {
                    toiletListData = GsonUtil.fromJson(result, ToiletInfo.class).getToiletList();
                    if (onlineToiletIdList != null && onlineToiletIdList.size() > 0) {
                        for (ToiletInfo.ToiletItem item : toiletListData) {
                            if (onlineToiletIdList.contains(item.getToiletId())) {
                                item.setOnline(true);
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onSearchToiletResult(toiletListData);
                        }
                    });
                }
            }
        });
    }

    /**
     * 查找到Toilet信息后
     * 需要在主线程里面调用
     *
     * @param toiletListData
     */
    @MainThread
    private void onSearchToiletResult(List<ToiletInfo.ToiletItem> toiletListData) {
        if (toiletListData != null && toiletListData.size() > 0) {
            searchResult_ll.setVisibility(View.VISIBLE);
            onChangeUI();
        } else {
            searchResult_ll.setVisibility(View.GONE);
        }
    }

    /**
     * 选中Item后的操作
     *
     * @param item
     */
    public void onItemSelected(ToiletInfo.ToiletItem item) {
        if (!StringUtils.isEmpty(item.getToiletId())) {
            Intent intent = getIntent();
            intent.putExtra("toiletItem", item);
            setResult(2, intent);
            SearchBathIdAty.this.finish();
        }
    }


    /**
     * 显示厕所列表
     */
    private static class ToiletAdapter extends BaseAdapter {

        private List<ToiletInfo.ToiletItem> toiletItemList;
        private Context context;

        public ToiletAdapter(Context context, List<ToiletInfo.ToiletItem> toiletItemList) {
            this.toiletItemList = toiletItemList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return toiletItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return toiletItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 通知数据实时改变
         */
        private void notifyDataChange(List<ToiletInfo.ToiletItem> toiletItemList) {
            this.toiletItemList = toiletItemList;
            this.notifyDataSetChanged();
        }

        /**
         * 选择Item确定
         */
        private void onItemClick(int position) {
            ToiletInfo.ToiletItem item = toiletItemList.get(position);
            if (!item.isOnline()) {
                ToastUtil.showShortToast(context, "该厕所不在线，无法切换！", Gravity.CENTER);
                return;
            }
            String text = item.getToiletId();
            if (StringUtils.isNotEmpty(item.getNickname())) {
                text += "\t\t" + item.getNickname() + "\n";
            } else {
                text += "\t\t" + item.getCompanyName() + "\n";
            }
            text += item.getProvince() + "--" + item.getCity() + "--" + item.getCounty() + "\n";
            text += item.getDetail();
            new AlertDialog.Builder(context).setMessage(text)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //首先请求公共数据
                            Repository.getItemCommon((Activity) context, item.getToiletId(), new Repository.DataCallback() {
                                @Override
                                public void onData(Object... args) {
                                    if (args[0] != null) {
                                        ((SearchBathIdAty) context).onItemSelected(item);
                                    } else {
                                        ToastUtil.logAndToast(context, "请求公共数据失败，请重试");
                                    }
                                    dialog.dismiss();
                                }
                            });
                        }
                    }).create().show();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ToiletAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new ToiletAdapter.ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_toilet_info_item, null);
                holder.item_ll = (LinearLayout) convertView.findViewById(R.id.item_ll);
                holder.item_tv = (TextView) convertView.findViewById(R.id.item_tv);
                holder.itemCity_tv = (TextView) convertView.findViewById(R.id.itemCity_tv);
                holder.itemDetail_tv = (TextView) convertView.findViewById(R.id.itemDetail_tv);
                convertView.setTag(holder);
            } else {
                holder = (ToiletAdapter.ViewHolder) convertView.getTag();
            }
            String text = toiletItemList.get(position).getToiletId();
            if (StringUtils.isNotEmpty(toiletItemList.get(position).getNickname())) {
                text += "\t\t" + toiletItemList.get(position).getNickname();
            } else {
                text += "\t" + toiletItemList.get(position).getCompanyName();
            }
            holder.item_tv.setText(text);
            if (toiletItemList.get(position).isOnline()) {
                holder.item_tv.setTextColor(context.getResources().getColor(R.color.black));
            } else {
                holder.item_tv.setTextColor(context.getResources().getColor(R.color.gray_offline));
            }
            text = toiletItemList.get(position).getProvince() + "--" + toiletItemList.get(position).getCity() + "--" + toiletItemList.get(position).getCounty();
            holder.itemCity_tv.setText(text);
            text = toiletItemList.get(position).getDetail();
            holder.itemDetail_tv.setText(text);
            holder.item_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(position);
                }
            });
            return convertView;
        }


        /**
         * 图片列表显示的ViewHolder 内部类
         */
        public class ViewHolder {
            LinearLayout item_ll;
            TextView item_tv;
            TextView itemCity_tv;
            TextView itemDetail_tv;
        }
    }

}
