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

    public static final int INBOX_TAB = 0;
    public static final int FRIENDS_TAB = 1;

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
        if (position == INBOX_TAB) {
            return InboxFragment.newInstance(position + 1);
        } else if (position == FRIENDS_TAB) {
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
            case INBOX_TAB:
                return context.getString(R.string.title_section1).toUpperCase(l);
            case FRIENDS_TAB:
                return context.getString(R.string.title_section2).toUpperCase(l);
        }
        return null;
    }

    public int getIcon(int position) {
        switch (position) {
            case INBOX_TAB:
                return R.drawable.ic_tab_inbox;
            case FRIENDS_TAB:
                return R.drawable.ic_tab_friends;
        }
        return R.drawable.ic_tab_inbox;
    }
}
