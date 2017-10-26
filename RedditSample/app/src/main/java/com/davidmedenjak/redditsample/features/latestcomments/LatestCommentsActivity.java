package com.davidmedenjak.redditsample.features.latestcomments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.davidmedenjak.redditsample.R;
import com.davidmedenjak.redditsample.common.BaseActivity;
import com.davidmedenjak.redditsample.networking.RedditService;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class LatestCommentsActivity extends BaseActivity {

    private static final String EXTRA_ACCOUNT = "extra_account";
    private CommentsAdapter adapter;

    public static Intent newIntent(Context context, Account account) {
        Intent intent = new Intent(context, LatestCommentsActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, account);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        adapter = new CommentsAdapter();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Account account = getIntent().getParcelableExtra(EXTRA_ACCOUNT);

        RedditService service =
                createRetrofit(account, "https://oauth.reddit.com/api/")
                        .create(RedditService.class);

        service.fetchComments(account.name)
                .observeOn(AndroidSchedulers.mainThread())
                .map(r -> r.data)
                .flatMap(
                        l ->
                                Observable.fromIterable(l.children)
                                        .map(c -> c.data)
                                        .toList()
                                        .toObservable())
                .subscribe(
                        r -> {
                            adapter.setComments(r);
                        });


        // "invalidate" token and start multiple requests at once
        AccountManager accountManager = AccountManager.get(this);
        accountManager.setAuthToken(account, "bearer", "invalidAccessToken");
//        Observable.range(0, 5)
//                .flatMap(__ ->
//                        service.fetchComments(account.name)
//                ).subscribe();
    }

    @NonNull
    private Retrofit createRetrofit(Account account, String baseUrl) {
        AccountManager accountManager = AccountManager.get(this);
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(logger)
                        .addInterceptor(new AuthInterceptor(account, this))
                        .authenticator(new ApiAuthenticator(account, accountManager))
                        .build();

        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .baseUrl(baseUrl)
                .build();
    }
}
