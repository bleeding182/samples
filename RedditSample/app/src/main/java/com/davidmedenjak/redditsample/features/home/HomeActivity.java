package com.davidmedenjak.redditsample.features.home;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.davidmedenjak.redditsample.R;
import com.davidmedenjak.redditsample.common.BaseActivity;

public class HomeActivity extends BaseActivity implements OnAccountsUpdateListener {

    private AccountManager accountManager;
    private RedditAccountAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.accountManager = AccountManager.get(this);

        setContentView(R.layout.activity_home);

        adapter = new RedditAccountAdapter(accountManager);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        accountManager.addOnAccountsUpdatedListener(this, null, true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        accountManager.removeOnAccountsUpdatedListener(this);
    }

    @Override
    public void onAccountsUpdated(Account[] accounts) {
        adapter.updateAccounts(accounts);
    }
}
