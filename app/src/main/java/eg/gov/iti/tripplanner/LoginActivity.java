package eg.gov.iti.tripplanner;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import eg.gov.iti.tripplanner.adapters.LoginFragmentPagerAdapter;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewPager viewPager = findViewById(R.id.viewpager);

        LoginFragmentPagerAdapter adapter = new LoginFragmentPagerAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);

        tabLayout.setupWithViewPager(viewPager);
    }
}
