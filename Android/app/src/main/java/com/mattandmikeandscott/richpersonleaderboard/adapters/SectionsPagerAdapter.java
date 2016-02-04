package com.mattandmikeandscott.richpersonleaderboard.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mattandmikeandscott.richpersonleaderboard.MainFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a MainFragment (defined as a static inner class below).
        return MainFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "ALL TIME";
            case 1:
                return "TODAY";
            case 2:
                return "THIS WEEK";
            case 3:
                return "THIS MONTH";
            case 4:
                return "THIS YEAR";
            default:
                return "";
        }
    }

    //public int getPageIcon(int position) {
    //    switch (position) {
    //        case 0:
    //            return R.drawable.table_icon;
    //        case 1:
    //            return R.drawable.boss_icon;
    //        case 2:
    //            return R.drawable.home_icon;
    //        case 3:
    //            return R.drawable.cards_icon;
    //        default:
    //            return -1;
    //    }
    //}
}
