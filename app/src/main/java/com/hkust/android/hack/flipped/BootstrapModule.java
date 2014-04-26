package com.hkust.android.hack.flipped;

import android.accounts.AccountManager;
import android.content.Context;

import com.hkust.android.hack.flipped.authenticator.BootstrapAccountRegisterActivity;
import com.hkust.android.hack.flipped.authenticator.BootstrapAuthenticatorActivity;
import com.hkust.android.hack.flipped.authenticator.LogoutService;
import com.hkust.android.hack.flipped.ui.MainActivity;
import com.hkust.android.hack.flipped.ui.CheckInsListFragment;
import com.hkust.android.hack.flipped.ui.NavigationDrawerFragment;
import com.hkust.android.hack.flipped.ui.NewsListFragment;
import com.hkust.android.hack.flipped.ui.PostStatusActivity;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module(
        complete = false,

        injects = {
                BootstrapApplication.class,
                BootstrapAuthenticatorActivity.class,
                MainActivity.class,
                CheckInsListFragment.class,
                NavigationDrawerFragment.class,
                NewsListFragment.class,
                BootstrapAccountRegisterActivity.class,
                PostStatusActivity.class
        }
)
public class BootstrapModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new Bus();
    }

    @Provides
    @Singleton
    LogoutService provideLogoutService(final Context context, final AccountManager accountManager) {
        return new LogoutService(context, accountManager);
    }

}
