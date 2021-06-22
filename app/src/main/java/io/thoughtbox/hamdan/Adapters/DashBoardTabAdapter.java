package io.thoughtbox.hamdan.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import io.thoughtbox.hamdan.fragments.rates.BankTransferRateFragment;
import io.thoughtbox.hamdan.fragments.rates.CashPickUpRateFragment;

public class DashBoardTabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public DashBoardTabAdapter(FragmentManager fm, int mNumOfTabs){
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                BankTransferRateFragment bank = new BankTransferRateFragment();
                return bank;
            case 1:
                CashPickUpRateFragment cashPickUp = new CashPickUpRateFragment();
                return cashPickUp;
            default:
                return null;
        }
    }
}
