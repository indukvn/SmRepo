package com.example.Feat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.facebook.ads.AdChoicesView;
import com.like.LikeButton;
import com.like.OnAnimationEndListener;
import com.like.OnLikeListener;
import com.peekandpop.shalskar.peekandpop.PeekAndPop;
import com.virmana.status_video.R;
import com.virmana.status_video.api.apiClient;
import com.virmana.status_video.api.apiRest;
import com.virmana.status_video.model.Category;
import com.virmana.status_video.model.User;
import com.virmana.status_video.model.Video;
import com.virmana.status_video.Provider.DownloadStorage;
import com.virmana.status_video.Provider.FavoritesStorage;
import com.virmana.status_video.Provider.PrefManager;
import com.virmana.status_video.ui.Activities.VideoActivity;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by Tamim on 08/10/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter {
    private  Boolean downloads = false;
    private  Boolean favorites = false;
    private  PeekAndPop peekAndPop;
    private List<Video> VideoList =new ArrayList<>();
    private List<Category> categoryList =new ArrayList<>();
    private Activity activity;
    private static final String WHATSAPP_ID="com.whatsapp";
    private static final String FACEBOOK_ID="com.facebook.katana";
    private static final String MESSENGER_ID="com.facebook.orca";
    private static final String INSTAGRAM_ID="com.instagram.android";
    private static final String SHARE_ID="com.android.all";
    private InterstitialAd mInterstitialAd;
    private LinearLayoutManager linearLayoutManager;

    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;
    private DataSource.Factory mediaDataSourceFactory;
    private ImageView ivHideControllerButton;
    private Timeline.Window window;
    private SubscribeAdapter subscribeAdapter;
    private List<User> userList;

    public VideoAdapter(final List<Video> VideoList, List<Category> categoryList, final Activity activity, final PeekAndPop peekAndPop) {
        this.VideoList = VideoList;
        this.categoryList = categoryList;
        this.activity = activity;
        this.peekAndPop=peekAndPop;
        mInterstitialAd = new InterstitialAd(activity.getApplication());
        mInterstitialAd.setAdUnitId(activity.getString(R.string.ad_unit_id_interstitial));
        requestNewInterstitial();

        peekAndPop.addHoldAndReleaseView(R.id.like_button_fav_image_dialog);
        peekAndPop.addHoldAndReleaseView(R.id.like_button_messenger_image_dialog);
        peekAndPop.addHoldAndReleaseView(R.id.like_button_facebook_image_dialog);
        peekAndPop.addHoldAndReleaseView(R.id.like_button_instagram_image_dialog);
        peekAndPop.addHoldAndReleaseView(R.id.like_button_share_image_dialog);
        peekAndPop.addHoldAndReleaseView(R.id.like_button_whatsapp_image_dialog);

        peekAndPop.setOnHoldAndReleaseListener(new PeekAndPop.OnHoldAndReleaseListener() {
            @Override
            public void onHold(View view, int i) {
                Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(40);
            }

            @Override
            public void onLeave(View view, int i) {

            }

            @Override
            public void onRelease(View view,final int position) {
                final DownloadFileFromURL downloadFileFromURL = new DownloadFileFromURL();
                switch (view.getId()){
                    case R.id.like_button_facebook_image_dialog:
                        if (mInterstitialAd.isLoaded()) {
                            if (check()) {
                                mInterstitialAd.show();
                                mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        requestNewInterstitial();
                                        if (!VideoList.get(position).isDownloading()) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,FACEBOOK_ID);
                                            else
                                                downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,FACEBOOK_ID);
                                        }
                                    }
                                });
                            } else {

                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,FACEBOOK_ID);
                                    else
                                        downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,FACEBOOK_ID);
                                }
                            }
                        }else{

                            if (!VideoList.get(position).isDownloading()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,FACEBOOK_ID);
                                else
                                    downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,FACEBOOK_ID);
                            }
                        }

                        break;
                    case R.id.like_button_messenger_image_dialog:



                        if (mInterstitialAd.isLoaded()) {
                            if (check()) {
                                mInterstitialAd.show();
                                mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        requestNewInterstitial();
                                        if (!VideoList.get(position).isDownloading()) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,MESSENGER_ID);
                                            else
                                                downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,MESSENGER_ID);
                                        }
                                    }
                                });
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,MESSENGER_ID);
                                    else
                                        downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,MESSENGER_ID);
                                }
                            }
                        }else{
                            if (!VideoList.get(position).isDownloading()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,MESSENGER_ID);
                                else
                                    downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,MESSENGER_ID);
                            }
                        }



                        break;
                    case R.id.like_button_whatsapp_image_dialog:


                        if (mInterstitialAd.isLoaded()) {
                            if (check()) {
                                mInterstitialAd.show();
                                mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        requestNewInterstitial();
                                        if (!VideoList.get(position).isDownloading()) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,WHATSAPP_ID);
                                            else
                                                downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,WHATSAPP_ID);
                                        }
                                    }
                                });
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,WHATSAPP_ID);
                                    else
                                        downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,WHATSAPP_ID);
                                }
                            }
                        }else{
                            if (!VideoList.get(position).isDownloading()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,WHATSAPP_ID);
                                else
                                    downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,WHATSAPP_ID);
                            }
                        }


                        break;
                    case R.id.like_button_instagram_image_dialog:


                        if (mInterstitialAd.isLoaded()) {
                            if (check()) {
                                mInterstitialAd.show();
                                mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        requestNewInterstitial();
                                        if (!VideoList.get(position).isDownloading()) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,INSTAGRAM_ID);
                                            else
                                                downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,INSTAGRAM_ID);
                                        }
                                    }
                                });
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,INSTAGRAM_ID);
                                    else
                                        downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,INSTAGRAM_ID);
                                }
                            }
                        }else{
                            if (!VideoList.get(position).isDownloading()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,INSTAGRAM_ID);
                                else
                                    downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,INSTAGRAM_ID);
                            }
                        }




                        break;
                    case R.id.like_button_share_image_dialog:


                        if (mInterstitialAd.isLoaded()) {
                            if (check()) {
                                mInterstitialAd.show();
                                mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        requestNewInterstitial();
                                        if (!VideoList.get(position).isDownloading()) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position, SHARE_ID);
                                            else
                                                downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,SHARE_ID);
                                        }
                                    }
                                });
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position, SHARE_ID);
                                    else
                                        downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,SHARE_ID);
                                }
                            }
                        }else{
                            if (!VideoList.get(position).isDownloading()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position, SHARE_ID);
                                else
                                    downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,SHARE_ID);
                            }
                        }
                        break;
                    case R.id.like_button_fav_image_dialog:
                        final FavoritesStorage storageFavorites= new FavoritesStorage(activity.getApplicationContext());

                        List<Video> favorites_list = storageFavorites.loadImagesFavorites();
                        Boolean exist = false;
                        if (favorites_list==null){
                            favorites_list= new ArrayList<>();
                        }
                        for (int i = 0; i <favorites_list.size() ; i++) {
                            if (favorites_list.get(i).getId().equals(VideoList.get(position).getId())){
                                exist = true;
                            }
                        }
                        if (exist  == false) {
                            ArrayList<Video> audios= new ArrayList<Video>();
                            for (int i = 0; i < favorites_list.size(); i++) {
                                audios.add(favorites_list.get(i));
                            }
                            audios.add(VideoList.get(position));
                            storageFavorites.storeImage(audios);
                        }else{
                            ArrayList<Video> new_favorites= new ArrayList<Video>();
                            for (int i = 0; i < favorites_list.size(); i++) {
                                if (!favorites_list.get(i).getId().equals(VideoList.get(position).getId())){
                                    new_favorites.add(favorites_list.get(i));

                                }
                            }
                            if (favorites==true){
                                VideoList.remove(position);
                                notifyDataSetChanged();
                                //holder.ripple_view_wallpaper_item.setVisibility(View.GONE);
                            }
                            storageFavorites.storeImage(new_favorites);

                        }
                        notifyDataSetChanged();
                        break;

                }
            }


        });
        peekAndPop.setOnGeneralActionListener(new PeekAndPop.OnGeneralActionListener() {
            @Override
            public void onPeek(View view, int position) {

                LikeButton like_button_fav_image_dialog =(LikeButton) peekAndPop.getPeekView().findViewById(R.id.like_button_fav_image_dialog);

                final FavoritesStorage storageFavorites= new FavoritesStorage(activity.getApplicationContext());
                List<Video> Videos = storageFavorites.loadImagesFavorites();
                Boolean exist = false;
                if (Videos ==null){
                     Videos = new ArrayList<>();
                }
                for (int i = 0; i < Videos.size() ; i++) {
                    if (Videos.get(i).getId().equals(VideoList.get(position).getId())){
                        exist = true;
                    }
                }
                if (exist == false) {
                    like_button_fav_image_dialog.setLiked(false);
                } else {
                    like_button_fav_image_dialog.setLiked(true);
                }


                ImageView circle_image_view_dialog_image =(ImageView) peekAndPop.getPeekView().findViewById(R.id.circle_image_view_dialog_image);
                simpleExoPlayerView = (SimpleExoPlayerView) peekAndPop.getPeekView().findViewById(R.id.video_view_dialog_image);
                TextView text_view_view_dialog_user=(TextView) peekAndPop.getPeekView().findViewById(R.id.text_view_view_dialog_user);
                TextView text_view_view_dialog_title=(TextView) peekAndPop.getPeekView().findViewById(R.id.text_view_view_dialog_title);
                TextView text_view_downloads=(TextView) peekAndPop.getPeekView().findViewById(R.id.text_view_downloads);

                Picasso.with(activity.getApplicationContext()).load(VideoList.get(position).getUserimage()).error(R.drawable.profile).placeholder(R.drawable.profile).into(circle_image_view_dialog_image);
                text_view_view_dialog_user.setText(VideoList.get(position).getUser());
                text_view_view_dialog_title.setText(VideoList.get(position).getTitle());
                text_view_downloads.setText(format(VideoList.get(position).getDownloads()) + " Shares");



                shouldAutoPlay = true;
                bandwidthMeter = new DefaultBandwidthMeter();
                mediaDataSourceFactory = new DefaultDataSourceFactory(activity.getApplicationContext(), Util.getUserAgent(activity.getApplication(), "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
                window = new Timeline.Window();
                ivHideControllerButton = (ImageView) peekAndPop.getPeekView().findViewById(R.id.exo_controller);
                initializePlayer(position);




            }

            @Override
            public void onPop(View view, int i) {
                try {
                    releasePlayer();
                    bandwidthMeter=null;
                    mediaDataSourceFactory=null;
                    window=null;
                }catch (Exception e){

                }

            }
        });
    }
    private void initializePlayer(Integer position) {

        simpleExoPlayerView.requestFocus();

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);

        simpleExoPlayerView.setPlayer(player);

        player.setPlayWhenReady(shouldAutoPlay);
/*        MediaSource mediaSource = new HlsMediaSource(Uri.parse("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"),
                mediaDataSourceFactory, mainHandler, null);*/

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(VideoList.get(position).getVideo()),
                mediaDataSourceFactory, extractorsFactory, null, null);
        if (downloads){
//            Log.v("this is path",VideoList.get(position).getPath());
             Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(VideoList.get(position).getLocal()));
             mediaSource = new ExtractorMediaSource(imageUri,
                    mediaDataSourceFactory, extractorsFactory, null, null);
        }


        player.prepare(mediaSource);
        simpleExoPlayerView.hideController();

        ivHideControllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleExoPlayerView.hideController();
            }
        });
    }
    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }
    public VideoAdapter(final List<Video> VideoList, List<Category> categoryList, final Activity activity, final PeekAndPop peekAndPop, Boolean favorites_) {
        this(VideoList,categoryList,activity,peekAndPop);
        this.favorites=favorites_;
    }
    public VideoAdapter(final List<Video> VideoList, List<Category> categoryList, final Activity activity, final PeekAndPop peekAndPop, Boolean favorites_,Boolean downloads_) {
        this(VideoList,categoryList,activity,peekAndPop);
        this.favorites=favorites_;
        this.downloads=downloads_;
    }
    public VideoAdapter(final List<Video> VideoList, List<Category> categoryList, final Activity activity, final PeekAndPop peekAndPop, Boolean favorites_,Boolean downloads_,List<User> userList_) {
        this(VideoList,categoryList,activity,peekAndPop);
        this.favorites=favorites_;
        this.downloads=downloads_;
        this.userList=userList_;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {

            case 0: {
                View v0 = inflater.inflate(R.layout.item_empty, parent, false);
                viewHolder = new VideoAdapter.EmptyHolder(v0);
                break;
            }
            case 1: {
                View v1 = inflater.inflate(R.layout.item_categories, parent, false);
                viewHolder = new VideoAdapter.CategoriesHolder(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_video, parent, false);
                viewHolder = new VideoAdapter.ImageHolder(v2);
                break;
            }
            case 3:{
                View v3 = inflater.inflate(R.layout.item_facebook_ads, parent, false);
                viewHolder = new FacebookNativeHolder(v3);
                break;
            }
            case 4: {
                View v4 = inflater.inflate(R.layout.item_subscriptions, parent, false);
                viewHolder = new SubscriptionHolder(v4);
                break;
            }

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_parent,final  int position) {
        switch (getItemViewType(position)) {
            case 0: {

                break;
            }
            case 1: {
                final VideoAdapter.CategoriesHolder holder = (VideoAdapter.CategoriesHolder) holder_parent;
                break;
            }
            case 2: {

                final VideoAdapter.ImageHolder holder = (VideoAdapter.ImageHolder) holder_parent;
                final DownloadFileFromURL downloadFileFromURL = new DownloadFileFromURL();
                peekAndPop.addLongClickView(holder.image_view_image_item_image, position);
                if (VideoList.get(position).getReview()){
                    holder.relative_layout_item_image_review.setVisibility(View.VISIBLE);
                }else{
                    holder.relative_layout_item_image_review.setVisibility(View.GONE);
                }
                if (downloads){
                    holder.like_button_delete_image_item.setVisibility(View.VISIBLE);
                    holder.like_button_fav_image_item.setVisibility(View.GONE);

                }else{
                    holder.like_button_delete_image_item.setVisibility(View.GONE);
                    holder.like_button_fav_image_item.setVisibility(View.VISIBLE);
                }
                Picasso.with(activity.getApplicationContext()).load(VideoList.get(position).getThumbnail()).error(R.drawable.bg_transparant).placeholder(R.drawable.bg_transparant).into(holder.image_view_image_item_image);
                Picasso.with(activity.getApplicationContext()).load(VideoList.get(position).getUserimage()).error(R.drawable.profile).placeholder(R.drawable.profile).into(holder.circle_image_view_image_item_user);
                if (!VideoList.get(position).isDownloading()){
                    holder.relative_layout_progress_image_item.setVisibility(View.GONE);
                }else{
                    holder.relative_layout_progress_image_item.setVisibility(View.VISIBLE);
                    holder.progress_bar_item_image.setProgress(VideoList.get(position).getProgress());
                    holder.text_view_progress_image_item.setText("Downloading : "+ VideoList.get(position).getProgress()+" %");
                    if (VideoList.get(position).getProgress()==100){
                        holder.image_view_cancel_image_item.setClickable(false);
                    }else{
                        holder.image_view_cancel_image_item.setClickable(true);

                    }
                }
                holder.text_view_downloads.setText(format(VideoList.get(position).getDownloads()) + " Shares");
                holder.text_view_image_item_name_user.setText(VideoList.get(position).getUser());
                holder.text_view_image_item_title.setText(VideoList.get(position).getTitle());
                holder.image_view_cancel_image_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadFileFromURL.cancel(true);
                    }
                });
                holder.like_button_whatsapp_image_item.setOnAnimationEndListener(new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(LikeButton likeButton) {
                        holder.like_button_whatsapp_image_item.setLiked(false);

                        if (mInterstitialAd.isLoaded()) {
                            if (check()) {
                                mInterstitialAd.show();
                                mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        requestNewInterstitial();
                                        if (!VideoList.get(position).isDownloading()) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,WHATSAPP_ID);
                                            else
                                                downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,WHATSAPP_ID);
                                        }
                                    }
                                });
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,WHATSAPP_ID);
                                    else
                                        downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,WHATSAPP_ID);
                                }
                            }
                        }else{
                            if (!VideoList.get(position).isDownloading()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,WHATSAPP_ID);
                                else
                                    downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,WHATSAPP_ID);
                            }
                        }


                    }
                });
                holder.like_button_messenger_image_item.setOnAnimationEndListener(new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(LikeButton likeButton) {
                        holder.like_button_messenger_image_item.setLiked(false);

                        if (mInterstitialAd.isLoaded()) {
                            if (check()) {
                                mInterstitialAd.show();
                                mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        requestNewInterstitial();
                                        if (!VideoList.get(position).isDownloading()) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,MESSENGER_ID);
                                            else
                                                downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,MESSENGER_ID);
                                        }
                                    }
                                });
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,MESSENGER_ID);
                                    else
                                        downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,MESSENGER_ID);
                                }
                            }
                        }else{
                            if (!VideoList.get(position).isDownloading()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,MESSENGER_ID);
                                else
                                    downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,MESSENGER_ID);
                            }
                        }



                    }
                });

                holder.like_button_instagram_image_item.setOnAnimationEndListener(new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(LikeButton likeButton) {
                        holder.like_button_instagram_image_item.setLiked(false);


                        if (mInterstitialAd.isLoaded()) {
                            if (check()) {
                                mInterstitialAd.show();
                                mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        requestNewInterstitial();
                                        if (!VideoList.get(position).isDownloading()) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,INSTAGRAM_ID);
                                            else
                                                downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,INSTAGRAM_ID);
                                        }
                                    }
                                });
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,INSTAGRAM_ID);
                                    else
                                        downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,INSTAGRAM_ID);
                                }
                            }
                        }else{
                            if (!VideoList.get(position).isDownloading()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,INSTAGRAM_ID);
                                else
                                    downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,INSTAGRAM_ID);
                            }
                        }



                    }
                });
                holder.like_button_facebook_image_item.setOnAnimationEndListener(new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(LikeButton likeButton) {
                        holder.like_button_facebook_image_item.setLiked(false);


                        if (mInterstitialAd.isLoaded()) {
                            if (check()) {
                                mInterstitialAd.show();
                                mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        requestNewInterstitial();
                                        if (!VideoList.get(position).isDownloading()) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,FACEBOOK_ID);
                                            else
                                                downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,FACEBOOK_ID);
                                        }
                                    }
                                });
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,FACEBOOK_ID);
                                    else
                                        downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,FACEBOOK_ID);
                                }
                            }
                        }else{
                            if (!VideoList.get(position).isDownloading()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,FACEBOOK_ID);
                                else
                                    downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,FACEBOOK_ID);
                            }
                        }



                    }
                });
                holder.like_button_share_image_item.setOnAnimationEndListener(new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(LikeButton likeButton) {
                        holder.like_button_facebook_image_item.setLiked(false);


                        if (mInterstitialAd.isLoaded()) {
                            if (check()) {
                                mInterstitialAd.show();
                                mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        requestNewInterstitial();
                                        if (!VideoList.get(position).isDownloading()) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,SHARE_ID);
                                            else
                                                downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,SHARE_ID);
                                        }
                                    }
                                });
                            } else {

                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,SHARE_ID);
                                    else
                                        downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,SHARE_ID);
                                }
                            }
                        }else{

                            if (!VideoList.get(position).isDownloading()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,SHARE_ID);
                                else
                                    downloadFileFromURL.execute(VideoList.get(position).getVideo(), VideoList.get(position).getTitle(), VideoList.get(position).getExtension(), position,SHARE_ID);
                            }
                        }

                    }
                });

                holder.image_view_image_item_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity.getApplicationContext(), VideoActivity.class);
                        intent.putExtra("id", VideoList.get(position).getId());
                        intent.putExtra("title", VideoList.get(position).getTitle());
                        intent.putExtra("description", VideoList.get(position).getDescription());
                        intent.putExtra("thumbnail", VideoList.get(position).getThumbnail());
                        intent.putExtra("userid", VideoList.get(position).getUserid());
                        intent.putExtra("user", VideoList.get(position).getUser());
                        intent.putExtra("userimage", VideoList.get(position).getUserimage());
                        intent.putExtra("type", VideoList.get(position).getType());
                        intent.putExtra("video", VideoList.get(position).getVideo());
                        intent.putExtra("image", VideoList.get(position).getImage());
                        intent.putExtra("extension", VideoList.get(position).getExtension());
                        intent.putExtra("comment", VideoList.get(position).getComment());
                        intent.putExtra("downloads", VideoList.get(position).getDownloads());
                        intent.putExtra("tags", VideoList.get(position).getTags());
                        intent.putExtra("review", VideoList.get(position).getReview());
                        intent.putExtra("comments", VideoList.get(position).getComments());
                        intent.putExtra("created", VideoList.get(position).getCreated());
                        intent.putExtra("local", VideoList.get(position).getLocal());

                        intent.putExtra("woow", VideoList.get(position).getWoow());
                        intent.putExtra("like", VideoList.get(position).getLike());
                        intent.putExtra("love", VideoList.get(position).getLove());
                        intent.putExtra("angry", VideoList.get(position).getAngry());
                        intent.putExtra("sad", VideoList.get(position).getSad());
                        intent.putExtra("haha", VideoList.get(position).getHaha());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });
                holder.relative_layout_item_imge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity.getApplicationContext(), VideoActivity.class);
                        intent.putExtra("id", VideoList.get(position).getId());
                        intent.putExtra("title", VideoList.get(position).getTitle());
                        intent.putExtra("description", VideoList.get(position).getDescription());
                        intent.putExtra("thumbnail", VideoList.get(position).getThumbnail());
                        intent.putExtra("userid", VideoList.get(position).getUserid());
                        intent.putExtra("user", VideoList.get(position).getUser());
                        intent.putExtra("userimage", VideoList.get(position).getUserimage());
                        intent.putExtra("type", VideoList.get(position).getType());
                        intent.putExtra("video", VideoList.get(position).getVideo());
                        intent.putExtra("image", VideoList.get(position).getImage());
                        intent.putExtra("extension", VideoList.get(position).getExtension());
                        intent.putExtra("comment", VideoList.get(position).getComment());
                        intent.putExtra("downloads", VideoList.get(position).getDownloads());
                        intent.putExtra("tags", VideoList.get(position).getTags());
                        intent.putExtra("review", VideoList.get(position).getReview());
                        intent.putExtra("comments", VideoList.get(position).getComments());
                        intent.putExtra("created", VideoList.get(position).getCreated());
                        intent.putExtra("local", VideoList.get(position).getLocal());


                        intent.putExtra("woow", VideoList.get(position).getWoow());
                        intent.putExtra("like", VideoList.get(position).getLike());
                        intent.putExtra("love", VideoList.get(position).getLove());
                        intent.putExtra("angry", VideoList.get(position).getAngry());
                        intent.putExtra("sad", VideoList.get(position).getSad());
                        intent.putExtra("haha", VideoList.get(position).getHaha());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });


                final FavoritesStorage storageFavorites= new FavoritesStorage(activity.getApplicationContext());
                List<Video> Videos = storageFavorites.loadImagesFavorites();
                Boolean exist = false;
                if (Videos ==null){
                    Videos = new ArrayList<>();
                }
                for (int i = 0; i < Videos.size() ; i++) {
                    if (Videos.get(i).getId().equals(VideoList.get(position).getId())){
                        exist = true;
                    }
                }
                if (exist == false) {
                    holder.like_button_fav_image_item.setLiked(false);
                } else {
                    holder.like_button_fav_image_item.setLiked(true);
                }
                holder.like_button_fav_image_item.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        List<Video> favorites_list = storageFavorites.loadImagesFavorites();
                        Boolean exist = false;
                        if (favorites_list==null){
                            favorites_list= new ArrayList<>();
                        }
                        for (int i = 0; i <favorites_list.size() ; i++) {
                            if (favorites_list.get(i).getId().equals(VideoList.get(position).getId())){
                                exist = true;
                            }
                        }
                        if (exist  == false) {
                            ArrayList<Video> audios= new ArrayList<Video>();
                            for (int i = 0; i < favorites_list.size(); i++) {
                                audios.add(favorites_list.get(i));
                            }
                            audios.add(VideoList.get(position));
                            storageFavorites.storeImage(audios);
                            holder.like_button_fav_image_item.setLiked(true);
                        }else{
                            ArrayList<Video> new_favorites= new ArrayList<Video>();
                            for (int i = 0; i < favorites_list.size(); i++) {
                                if (!favorites_list.get(i).getId().equals(VideoList.get(position).getId())){
                                    new_favorites.add(favorites_list.get(i));

                                }
                            }
                            if (favorites==true){
                                VideoList.remove(position);
                                notifyDataSetChanged();
                                //holder.ripple_view_wallpaper_item.setVisibility(View.GONE);
                            }
                            storageFavorites.storeImage(new_favorites);
                            holder.like_button_fav_image_item.setLiked(false);
                        }
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        List<Video> favorites_list = storageFavorites.loadImagesFavorites();
                        Boolean exist = false;
                        if (favorites_list==null){
                            favorites_list= new ArrayList<>();
                        }
                        for (int i = 0; i <favorites_list.size() ; i++) {
                            if (favorites_list.get(i).getId().equals(VideoList.get(position).getId())){
                                exist = true;
                            }
                        }
                        if (exist  == false) {
                            ArrayList<Video> audios= new ArrayList<Video>();
                            for (int i = 0; i < favorites_list.size(); i++) {
                                audios.add(favorites_list.get(i));
                            }
                            audios.add(VideoList.get(position));
                            storageFavorites.storeImage(audios);
                            holder.like_button_fav_image_item.setLiked(true);
                        }else{
                            ArrayList<Video> new_favorites= new ArrayList<Video>();
                            for (int i = 0; i < favorites_list.size(); i++) {
                                if (!favorites_list.get(i).getId().equals(VideoList.get(position).getId())){
                                    new_favorites.add(favorites_list.get(i));

                                }
                            }
                            if (favorites==true){
                                VideoList.remove(position);
                                notifyDataSetChanged();
                                //holder.ripple_view_wallpaper_item.setVisibility(View.GONE);
                            }
                            storageFavorites.storeImage(new_favorites);
                            holder.like_button_fav_image_item.setLiked(false);
                        }
                    }
                });

                holder.like_button_delete_image_item.setOnAnimationEndListener(new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(LikeButton likeButton) {
                        final DownloadStorage downloadStorage= new DownloadStorage(activity.getApplicationContext());

                        List<Video> downVideoList = downloadStorage.loadImagesFavorites();
                        Boolean exist = false;
                        if (downVideoList==null){
                            downVideoList= new ArrayList<>();
                        }
                        for (int i = 0; i <downVideoList.size() ; i++) {
                            if (downVideoList.get(i).getId().equals(VideoList.get(position).getId())){
                                exist = true;
                            }
                        }
                        if (exist  == true) {
                           String pathlocal =  VideoList.get(position).getLocal();
                            ArrayList<Video> new_dwonloads= new ArrayList<Video>();
                            for (int i = 0; i < downVideoList.size(); i++) {
                                if (!downVideoList.get(i).getId().equals(VideoList.get(position).getId())){
                                    new_dwonloads.add(downVideoList.get(i));

                                }
                            }
                            if (downloads==true){
                                VideoList.remove(position);
                                notifyDataSetChanged();
                            }
                            downloadStorage.storeImage(new_dwonloads);
                            holder.like_button_delete_image_item.setLiked(false);
                            Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(pathlocal));
                            File file = new File(pathlocal);
                            if (file.exists()) {
                                file.delete();
                                activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                            }

                        }
                    }
                });
                break;
            }
            case 3:{
                break;
            }
            case 4: {

                final SubscriptionHolder holder = (SubscriptionHolder) holder_parent;
                this.linearLayoutManager=  new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false);
                this.subscribeAdapter =new SubscribeAdapter(userList,activity);
                holder.recycle_view_follow_items.setHasFixedSize(true);
                holder.recycle_view_follow_items.setAdapter(subscribeAdapter);
                holder.recycle_view_follow_items.setLayoutManager(linearLayoutManager);
                subscribeAdapter.notifyDataSetChanged();
                break;
            }

        }
    }


    public static class ImageHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout relative_layout_progress_image_item;
        private final TextView text_view_progress_image_item;
        private final ImageView image_view_cancel_image_item;
        private final LikeButton like_button_whatsapp_image_item;
        private final LikeButton like_button_messenger_image_item;
        private final LikeButton like_button_share_image_item;
        private final LikeButton like_button_instagram_image_item;
        private final LikeButton like_button_facebook_image_item;
        private final LikeButton like_button_fav_image_item;
        private final RelativeLayout relative_layout_item_imge;
        private final TextView text_view_downloads;
        private final RelativeLayout relative_layout_item_image_review;
        private final LikeButton like_button_delete_image_item;
        private ProgressBar progress_bar_item_image;
        private  TextView text_view_image_item_name_user;
        private  TextView text_view_image_item_title;
        private  ImageView image_view_image_item_image;
        private  CircleImageView circle_image_view_image_item_user;

        public ImageHolder(View itemView) {
            super(itemView);
            this.relative_layout_item_image_review=(RelativeLayout) itemView.findViewById(R.id.relative_layout_item_image_review);
            this.relative_layout_item_imge=(RelativeLayout) itemView.findViewById(R.id.relative_layout_item_imge);
            this.like_button_delete_image_item=(LikeButton) itemView.findViewById(R.id.like_button_delete_image_item);
            this.like_button_fav_image_item=(LikeButton) itemView.findViewById(R.id.like_button_fav_image_item);
            this.like_button_messenger_image_item=(LikeButton) itemView.findViewById(R.id.like_button_messenger_image_item);
            this.like_button_facebook_image_item=(LikeButton) itemView.findViewById(R.id.like_button_facebook_image_item);
            this.like_button_instagram_image_item=(LikeButton) itemView.findViewById(R.id.like_button_instagram_image_item);
            this.like_button_share_image_item=(LikeButton) itemView.findViewById(R.id.like_button_share_image_item);
            this.like_button_whatsapp_image_item=(LikeButton) itemView.findViewById(R.id.like_button_whatsapp_image_item);
            this.image_view_cancel_image_item=(ImageView) itemView.findViewById(R.id.image_view_cancel_image_item);
            this.text_view_progress_image_item=(TextView) itemView.findViewById(R.id.text_view_progress_image_item);
            this.relative_layout_progress_image_item=(RelativeLayout) itemView.findViewById(R.id.relative_layout_progress_image_item);
            this.progress_bar_item_image=(ProgressBar) itemView.findViewById(R.id.progress_bar_item_image);
            this.circle_image_view_image_item_user=(CircleImageView) itemView.findViewById(R.id.circle_image_view_image_item_user);
            this.text_view_image_item_name_user=(TextView) itemView.findViewById(R.id.text_view_image_item_name_user);
            this.text_view_image_item_title = (TextView) itemView.findViewById(R.id.text_view_image_item_title);
            this.image_view_image_item_image=(ImageView) itemView.findViewById(R.id.image_view_image_item_image);
            this.text_view_downloads=(TextView) itemView.findViewById(R.id.text_view_downloads);
        }
    }
    public  class CategoriesHolder extends RecyclerView.ViewHolder {
        private final LinearLayoutManager linearLayoutManager;
        private final CategoryVideoAdapter categoryVideoAdapter;
        public RecyclerView recycler_view_item_categories;

        public CategoriesHolder(View view) {
            super(view);
            this.recycler_view_item_categories = (RecyclerView) itemView.findViewById(R.id.recycler_view_item_categories);
            this.linearLayoutManager=  new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
            this.categoryVideoAdapter =new CategoryVideoAdapter(categoryList,activity);
            recycler_view_item_categories.setHasFixedSize(true);
            recycler_view_item_categories.setAdapter(categoryVideoAdapter);
            recycler_view_item_categories.setLayoutManager(linearLayoutManager);
        }
    }
    public  class EmptyHolder extends RecyclerView.ViewHolder {


        public EmptyHolder(View view) {
            super(view);

        }
    }
    @Override
    public int getItemCount() {
        return VideoList.size();
    }

    @Override
    public int getItemViewType(int position) {

            return VideoList.get(position).getViewType();

    }


    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<Object, String, String> {

        private int position;
        private String old = "-100";
        private boolean runing = true;
        private String share_app;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        public boolean dir_exists(String dir_path)
        {
            boolean ret = false;
            File dir = new File(dir_path);
            if(dir.exists() && dir.isDirectory())
                ret = true;
            return ret;
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            runing = false;
        }
        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(Object... f_url) {
            int count;
            try {
                URL url = new URL((String) f_url[0]);
                String title = (String) f_url[1];
                String extension = (String) f_url[2];
                this.position = (int) f_url[3];
                this.share_app = (String) f_url[4];
                String id = VideoList.get(position).getId().toString();


                VideoList.get(position).setDownloading(true);
                URLConnection conection = url.openConnection();
                conection.setRequestProperty("Accept-Encoding", "identity");

                conection.connect();
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);



                String dir_path = Environment.getExternalStorageDirectory().toString() + "/StatusVideos/";

                if (!dir_exists(dir_path)){
                    File directory = new File(dir_path);
                    if(directory.mkdirs()){
                        Log.v("dir","is created 1");
                    }else{
                        Log.v("dir","not created 1");

                    }
                    if(directory.mkdir()){
                        Log.v("dir","is created 2");
                    }else{
                        Log.v("dir","not created 2");

                    }
                }else{
                    Log.v("dir","is exist");
                }
                File file= new File(dir_path+title.toString().replace("/","_")+"_"+id+"."+extension);
                if(!file.exists()){
                    // Output stream
                    OutputStream output = new FileOutputStream(dir_path+title.toString().replace("/","_")+"_"+id+"."+extension);

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress(""+(int)((total*100)/lenghtOfFile));
                        // writing data to file
                        output.write(data, 0, count);
                        if (!runing){
                            Log.v("v","not rurning");
                        }
                    }

                    output.flush();


                    output.close();
                    input.close();
                    MediaScannerConnection.scanFile(activity.getApplicationContext(), new String[] { dir_path+title.toString().replace("/","_")+"_"+id+"."+extension },
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        final Uri contentUri = Uri.fromFile(new File(dir_path+title.toString().replace("/","_")+"_"+id+"."+extension));
                        scanIntent.setData(contentUri);
                        activity.sendBroadcast(scanIntent);
                    } else {
                        final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                        activity.sendBroadcast(intent);
                    }
                }
                VideoList.get(position).setPath(dir_path+title.toString().replace("/","_")+"_"+id+"."+extension);
            } catch (Exception e) {
                Log.v("ex",e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            try {
                if(!progress[0].equals(old)){
                    VideoList.get(position).setProgress(Integer.valueOf(progress[0]));
                    notifyDataSetChanged();
                    old=progress[0];
                    Log.v("download",progress[0]+"%");
                    VideoList.get(position).setDownloading(true);
                    VideoList.get(position).setProgress(Integer.parseInt(progress[0]));
                }
            }catch (Exception e){

            }

        }
        public void AddDownloadLocal(Integer position){
            final DownloadStorage downloadStorage= new DownloadStorage(activity.getApplicationContext());
            List<Video> download_list = downloadStorage.loadImagesFavorites();
            Boolean exist = false;
            if (download_list==null){
                download_list= new ArrayList<>();
            }
            for (int i = 0; i <download_list.size() ; i++) {
                if (download_list.get(i).getId().equals(VideoList.get(position).getId())){
                    exist = true;
                }
            }
            if (exist  == false) {
                ArrayList<Video> audios= new ArrayList<Video>();
                for (int i = 0; i < download_list.size(); i++) {
                    audios.add(download_list.get(i));
                }
                Video videodownloaded = VideoList.get(position);

                videodownloaded.setLocal(VideoList.get(position).getPath());

                audios.add(videodownloaded);
                downloadStorage.storeImage(audios);
            }
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {

            VideoList.get(position).setDownloading(false);
            if (VideoList.get(position).getPath()==null){
                if (downloads){
                    switch (share_app) {
                        case WHATSAPP_ID:
                            shareWhatsapp(position);
                            break;
                        case FACEBOOK_ID:
                            shareFacebook(position);
                            break;
                        case MESSENGER_ID:
                            shareMessenger(position);
                            break;
                        case INSTAGRAM_ID:
                            shareInstagram(position);
                            break;
                        case SHARE_ID:
                            share(position);
                            break;
                    }
                }else{
                    Toasty.error(activity.getApplicationContext(), activity.getString(R.string.download_failed), Toast.LENGTH_SHORT, true).show();
                }
            }else {
                addDownload(position);
                AddDownloadLocal(position);
                switch (share_app) {
                    case WHATSAPP_ID:
                        shareWhatsapp(position);
                        break;
                    case FACEBOOK_ID:
                        shareFacebook(position);
                        break;
                    case MESSENGER_ID:
                        shareMessenger(position);
                        break;
                    case INSTAGRAM_ID:
                        shareInstagram(position);
                        break;
                    case SHARE_ID:
                        share(position);
                        break;
                }
            }
            notifyDataSetChanged();
        }

        public void shareWhatsapp(Integer position){
            String path = VideoList.get(position).getPath();
            if (VideoList.get(position).getLocal()!=null){
                if (new File(VideoList.get(position).getLocal()).exists()){
                    path =  VideoList.get(position).getLocal();
                }
            }
            Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(WHATSAPP_ID);



            final String final_text = activity.getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);


            shareIntent.setType(VideoList.get(position).getType());
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                activity.startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareFacebook(Integer position){
            String path = VideoList.get(position).getPath();

            Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(FACEBOOK_ID);



            final String final_text = activity.getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(VideoList.get(position).getType());
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                activity.startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.facebook_not_installed), Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareMessenger(Integer position){
            String path = VideoList.get(position).getPath();

            Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(MESSENGER_ID);

            final String final_text = activity.getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(VideoList.get(position).getType());
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                activity.startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.messenger_not_installed), Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareInstagram(Integer position){
            String path = VideoList.get(position).getPath();

            Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(INSTAGRAM_ID);



            final String final_text = activity.getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(VideoList.get(position).getType());
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                activity.startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.instagram_not_installed), Toast.LENGTH_SHORT, true).show();
            }
        }
        public void share(Integer position){
            String path = VideoList.get(position).getPath();

            Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);



            final String final_text = activity.getResources().getString(R.string.download_more_from_link);
            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(VideoList.get(position).getType());
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                activity.startActivity(Intent.createChooser(shareIntent, "Shared via " + activity.getResources().getString(R.string.app_name) ));
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.app_not_installed), Toast.LENGTH_SHORT, true).show();
            }
        }

    }
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }
    public void addDownload(final Integer position){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.imageAddDownload(VideoList.get(position).getId());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    VideoList.get(position).setDownloads(VideoList.get(position).getDownloads()+1);
                    notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }
    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
    public boolean check(){
        PrefManager prf = new PrefManager(activity.getApplicationContext());
        if (!prf.getString("SUBSCRIBED").equals("FALSE")) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        if (prf.getString("LAST_DATE_ADS").equals("")) {
            prf.setString("LAST_DATE_ADS", strDate);
        } else {
            String toyBornTime = prf.getString("LAST_DATE_ADS");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date oldDate = dateFormat.parse(toyBornTime);
                System.out.println(oldDate);

                Date currentDate = new Date();

                long diff = currentDate.getTime() - oldDate.getTime();
                long seconds = diff / 1000;

                if (seconds > Integer.parseInt(activity.getResources().getString(R.string.AD_MOB_TIME))) {
                    prf.setString("LAST_DATE_ADS", strDate);
                    return  true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }
    public  class FacebookNativeHolder extends  RecyclerView.ViewHolder {
        private final String TAG = "WALLPAPERADAPTER";
        private LinearLayout nativeAdContainer;
        private LinearLayout adView;
        private NativeAd nativeAd;
        public FacebookNativeHolder(View view) {
            super(view);
            loadNativeAd(view);
        }

        private void loadNativeAd(final View view) {
            // Instantiate a NativeAd object.
            // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
            // now, while you are testing and replace it later when you have signed up.
            // While you are using this temporary code you will only get test ads and if you release
            // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
            nativeAd = new NativeAd(activity,activity.getString(R.string.FACEBOOK_ADS_NATIVE_PLACEMENT_ID));
            nativeAd.setAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                    Log.e(TAG, "Native ad finished downloading all assets.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Native ad failed to load
                    Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Native ad is loaded and ready to be displayed
                    Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                    // Race condition, load() called again before last ad was displayed
                    if (nativeAd == null || nativeAd != ad) {
                        return;
                    }
                   /* NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                            .setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark))
                            .setTitleTextColor(Color.WHITE)
                            .setDescriptionTextColor(Color.WHITE)
                            .setButtonColor(Color.WHITE);

                    View adView = NativeAdView.render(activity, nativeAd, NativeAdView.Type.HEIGHT_300, viewAttributes);

                    LinearLayout nativeAdContainer = (LinearLayout) view.findViewById(R.id.native_ad_container);
                    nativeAdContainer.addView(adView);*/
                    // Inflate Native Ad into Container
                    inflateAd(nativeAd,view);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                    Log.d(TAG, "Native ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                    Log.d(TAG, "Native ad impression logged!");
                }
            });

            // Request an ad
            nativeAd.loadAd();
        }

        private void inflateAd(NativeAd nativeAd,View view) {

            nativeAd.unregisterView();

            // Add the Ad view into the ad container.
            nativeAdContainer = view.findViewById(R.id.native_ad_container);
            LayoutInflater inflater = LayoutInflater.from(activity);
            // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
            adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout_1, nativeAdContainer, false);
            nativeAdContainer.addView(adView);

            // Add the AdChoices icon
            LinearLayout adChoicesContainer = view.findViewById(R.id.ad_choices_container);
            AdChoicesView adChoicesView = new AdChoicesView(activity, nativeAd, true);
            adChoicesContainer.addView(adChoicesView, 0);

            // Create native UI using the ad metadata.
            AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
            TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
            MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
            TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
            TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
            TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
            Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

            // Set the Text.
            nativeAdTitle.setText(nativeAd.getAdvertiserName());
            nativeAdBody.setText(nativeAd.getAdBodyText());
            nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
            nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
            sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

            // Create a list of clickable views
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdCallToAction);

            // Register the Title and CTA button to listen for clicks.
            nativeAd.registerViewForInteraction(
                    adView,
                    nativeAdMedia,
                    nativeAdIcon,
                    clickableViews);
        }

    }
    public static class SubscriptionHolder extends  RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_follow_items;
        public SubscriptionHolder(View view) {
            super(view);
            recycle_view_follow_items = (RecyclerView) itemView.findViewById(R.id.recycle_view_follow_items);
        }
    }
}
