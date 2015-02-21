package es.uem.david.samuel.nacho.yepnsd;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;


/**
 * Created by usuario.apellido on 06/02/2015.
 *
 * @author david.sancho
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final int NUMBER_OF_TABS = 2;
    private Context context;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (position == 0) {
            return InboxFragment.newInstance(position + 1);
        } else if (position == 1) {
            return FriendsFragment.newInstance(position + 1);
        }
        return MainActivityTabbed.PlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return NUMBER_OF_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return context.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return context.getString(R.string.title_section2).toUpperCase(l);
        }
        return null;
    }
}
