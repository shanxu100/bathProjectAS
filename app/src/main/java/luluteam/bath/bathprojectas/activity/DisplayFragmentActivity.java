package luluteam.bath.bathprojectas.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.databinding.ActivityDisplayFragmentBinding;

/**
 * @author Guan
 */
public class DisplayFragmentActivity extends BaseActivity {

    public static final String EXTRA_NAME_TARGET_FRAGMENT = "extra_name_target_fragment";
    public static final String EXTRA_NAME_TOOLBAR_TITLE = "extra_name_toolbar_title";
    private ActivityDisplayFragmentBinding binding;

    private Fragment fragment;
    private FragmentTransaction transaction;
    private FragmentManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_display_fragment);
        manager = getSupportFragmentManager();
        initData();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        String targetFragment = getIntent().getStringExtra(EXTRA_NAME_TARGET_FRAGMENT);
        String toolbarTitle = getIntent().getStringExtra(EXTRA_NAME_TOOLBAR_TITLE);
        setupToolbar(toolbarTitle);
        try {
            fragment = (Fragment) Class.forName(targetFragment).newInstance();
            showFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFragment() {
        transaction = manager.beginTransaction();
        transaction.add(R.id.fl_content, fragment);
        transaction.show(fragment).commit();
    }

    private void setupToolbar(String toolbatTitle) {
        toolbatTitle = toolbatTitle == null ? "" : toolbatTitle;
        ((Toolbar) binding.toolbar).setTitle(toolbatTitle);
        this.setSupportActionBar((Toolbar) binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


}
