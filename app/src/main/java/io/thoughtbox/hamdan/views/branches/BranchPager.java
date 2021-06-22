package io.thoughtbox.hamdan.views.branches;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import javax.inject.Inject;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.ActivityBranchPagerBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;


public class BranchPager extends AppCompatActivity {

    private static final int NUM_PAGES = 2;
    public static ViewPager2 viewPager;
    private String[] titles;
    ActivityBranchPagerBinding binding;
    @Inject
    Dictionary dictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_branch_pager);
        binding.setLifecycleOwner(this);
        binding.setClickers(this);
        DaggerApiComponents.create().inject(this);
        binding.title.setText(dictionary.get("nearBranches"));
        titles = new String[]{dictionary.get("list"), dictionary.get("map")};
        viewPager = findViewById(R.id.pager);
        FragmentStateAdapter pagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(titles[position])).attach();
    }


    private static class MyPagerAdapter extends FragmentStateAdapter {

        public MyPagerAdapter(FragmentActivity fa) {
            super(fa);
        }


        @Override
        public Fragment createFragment(int pos) {
            switch (pos) {
                case 0: {
                    return new NearbyLocationList();
                }
                case 1: {
                    return new ShowBranches();
                }
                default:
                    return new NearbyLocationList();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    public void onBackClicked(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}