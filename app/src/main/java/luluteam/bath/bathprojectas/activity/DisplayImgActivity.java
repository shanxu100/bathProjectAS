package luluteam.bath.bathprojectas.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.List;

import luluteam.bath.bathprojectas.R;

/**
 * 展示具体一张图片
 */
public class DisplayImgActivity extends BaseActivity {

    private LinearLayout content_ll;
    private ViewPager img_viewPager;
    private PagerAdapter pagerAdapter;
    private Toolbar include_toolbar;

    private List<String> urls;
    private int position = 0;

    private List<ImageView> imageViewList;

    private static final String TAG = "DisplayImgActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_img);
        initUI();
        initData();
    }

    private void initData() {

        urls = getIntent().getStringArrayListExtra("urls");
        position = getIntent().getIntExtra("position", 0);
        pagerAdapter = new LargePicturesPargeAdapter(context, urls);

        img_viewPager.setAdapter(pagerAdapter);

        //position + urls.size() * 100,这样可以实现伪循环
        img_viewPager.setCurrentItem(position);


    }

    private void initUI() {
        content_ll = (LinearLayout) findViewById(R.id.content_ll);
        img_viewPager = (ViewPager) findViewById(R.id.img_viewpager);
        include_toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        include_toolbar.setTitle("图片详情");
        this.setSupportActionBar(include_toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    @Deprecated
    public static class ImgPagerAdapter extends PagerAdapter {

        private List<ImageView> imageViewList;

        public ImgPagerAdapter(List<ImageView> imageViewList) {
            this.imageViewList = imageViewList;
        }

        @Override
        public int getCount() {
            return imageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        // PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView(imageViewList.get(position));
        }

        // 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(imageViewList.get(position));
            return imageViewList.get(position);
        }
    }


    /**
     * 利用Glide加载图片，然后显示
     * 现在先不用
     */
    public static class LargePicturesPargeAdapter extends PagerAdapter {

        private List<String> allPicturePaths;
        private Context mContext;

        public LargePicturesPargeAdapter(Context context, List<String> allPicturePaths) {
            mContext = context;
            this.allPicturePaths = allPicturePaths;
        }

        @Override
        public int getCount() {
            //伪循环
//            return Integer.MAX_VALUE;
            return allPicturePaths.size();
        }

        @Override
        public int getItemPosition(Object object) {
            int position = super.getItemPosition(object);
            Log.e(TAG, "super.getItemPosition(object)=======" + position);
            return position;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 必须要实现的方法
         * 每次滑动的时实例化一个页面,ViewPager同时加载3个页面,假如此时你正在第二个页面，向左滑动，
         * 将实例化第4个页面
         **/
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            int newPosition = position % allPicturePaths.size();
            ImageView imageView = new ImageView(mContext);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(layoutParams);
            ((ViewPager) container).addView(imageView);
            Glide.with(mContext)
                    .load(allPicturePaths.get(newPosition))
                    .into(imageView);
            return imageView;
        }

        /**
         * 必须要实现的方法
         * 滑动切换的时销毁一个页面，ViewPager同时加载3个页面,假如此时你正在第二个页面，向左滑动，
         * 将销毁第1个页面
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub;
            ImageView imageView = (ImageView) object;
            if (imageView == null) {
                return;
            }
            ((ViewPager) container).removeView(imageView);
        }

    }
}
