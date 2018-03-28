package eg.gov.iti.tripplanner.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import eg.gov.iti.tripplanner.R;
import eg.gov.iti.tripplanner.fragments.LoginFragment;
import eg.gov.iti.tripplanner.fragments.SignUpFragment;

/**
 * Created by Ahmed_Mokhtar on 3/28/2018.
 */

public class LoginFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public LoginFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new LoginFragment();

        } else {
            return new SignUpFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.str_login);

            case 1:
                return context.getResources().getString(R.string.str_sign_up);

            default:
                return null;
        }
    }
}
