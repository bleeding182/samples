package com.davidmedenjak.redditsample.features.home;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davidmedenjak.redditsample.R;

class RedditAccountAdapter extends RecyclerView.Adapter<AccountViewHolder> {

    private Account[] accounts;
    private final AccountManager accountManager;

    public RedditAccountAdapter(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public void updateAccounts(Account[] accounts) {
        this.accounts = accounts;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_account_view, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        Account account = accounts[position];
        holder.name.setText(account.name);
        long linkKarma = Long.parseLong(accountManager.getUserData(account, "link_karma"));
        long commentKarma = Long.parseLong(accountManager.getUserData(account, "comment_karma"));

        Context context = holder.itemView.getContext();
        holder.linkKarma.setText(context.getString(R.string.link_karma, linkKarma));
        holder.commentKarma.setText(context.getString(R.string.comment_karma, commentKarma));
    }

    @Override
    public int getItemCount() {
        return accounts != null ? accounts.length : 0;
    }
}
