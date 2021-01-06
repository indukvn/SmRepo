package com.example.Feat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.virmana.status_video.R;
import com.virmana.status_video.model.StatusWhatsapp;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

/**
 * Created by hsn on 06/11/2018.
 */
public class RecyclerViewMediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String DIRECTORY_TO_SAVE_MEDIA_NOW = "/WSDownloader/";
    private ArrayList<StatusWhatsapp> filesList;
    private Activity activity;

    public RecyclerViewMediaAdapter(ArrayList<StatusWhatsapp> filesList, Activity activity) {
        this.filesList = filesList;
        for (int i = 0; i < this.filesList.size(); i++) {
            Log.v("ITEM_ADAPTER",filesList.get(i).getFile().getAbsolutePath());
        }
        this.activity = activity;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0: {
                View v1 = inflater.inflate(R.layout.item_whatsapp_video, null);
                viewHolder = new RecyclerViewMediaAdapter.VideoHolder(v1);
                break;
            }
            case 1: {
                View v2 = inflater.inflate(R.layout.item_whatsapp_image,null);
                viewHolder = new RecyclerViewMediaAdapter.ImageHolder(v2);
                break;
            }
        }
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_parent,final int position) {
        switch (getItemViewType(position)) {
            case 0: {
                final RecyclerViewMediaAdapter.VideoHolder holder = (RecyclerViewMediaAdapter.VideoHolder) holder_parent;
                Uri video = Uri.parse(filesList.get(position).getFile().getAbsolutePath());
                holder.videoViewVideoMedia.setVideoURI(video);
                holder.videoViewVideoMedia.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                        holder.videoViewVideoMedia.start();
                    }
                });

                holder.linear_layout_whatsapp_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            copyFile(filesList.get(position).getFile(), new File(Environment.getExternalStorageDirectory().toString() + DIRECTORY_TO_SAVE_MEDIA_NOW + filesList.get(position).getFile().getName()));
                            File sharefile = new File(Environment.getExternalStorageDirectory().toString() + DIRECTORY_TO_SAVE_MEDIA_NOW + filesList.get(position).getFile().getName());

                            Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", sharefile);
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);


                            final String final_text = activity.getResources().getString(R.string.download_more_from_link);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
                            shareIntent.setPackage("com.whatsapp");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                            shareIntent.setType(getMimeType(imageUri));
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                            try {
                                activity.startActivity(shareIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT, true).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                holder.linear_layout_share_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            copyFile(filesList.get(position).getFile(), new File(Environment.getExternalStorageDirectory().toString() + DIRECTORY_TO_SAVE_MEDIA_NOW + filesList.get(position).getFile().getName()));
                            File sharefile  =   new File(Environment.getExternalStorageDirectory().toString() + DIRECTORY_TO_SAVE_MEDIA_NOW + filesList.get(position).getFile().getName());
                            Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", sharefile);
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);



                            final String final_text = activity.getResources().getString(R.string.download_more_from_link);
                            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
                            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                            shareIntent.setType(getMimeType(imageUri));
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            try {
                                activity.startActivity(shareIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT, true).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                holder.linear_layout_download_video.setOnClickListener(this.downloadMediaItem(filesList.get(position).getFile()));
                break;
            }
            case 1: {

                final RecyclerViewMediaAdapter.ImageHolder holder = (RecyclerViewMediaAdapter.ImageHolder) holder_parent;
                Picasso.with(activity.getApplicationContext()).load(filesList.get(position).getFile()).error(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(holder.imageViewImageMedia);
                holder.linear_layout_whatsapp_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (filesList.get(position).getFile().exists()){
                            Log.v("V","File Exist");
                        }else{
                            Log.v("V","Not File Exist");
                        }
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setPackage("com.whatsapp");
                        shareIntent.setType(getMimeType(Uri.parse(filesList.get(position).getFile().getAbsolutePath())));
                        shareIntent.putExtra(Intent.EXTRA_STREAM,  Uri.parse(filesList.get(position).getFile().getAbsolutePath()));
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            activity.startActivity(shareIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
                holder.linear_layout_share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            copyFile(filesList.get(position).getFile(), new File(Environment.getExternalStorageDirectory().toString() + DIRECTORY_TO_SAVE_MEDIA_NOW + filesList.get(position).getFile().getName()));
                            File sharefile  =   new File(Environment.getExternalStorageDirectory().toString() + DIRECTORY_TO_SAVE_MEDIA_NOW + filesList.get(position).getFile().getName());

                            Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", sharefile);
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);



                            final String final_text = activity.getResources().getString(R.string.download_more_from_link);
                            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
                            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                            shareIntent.setType(getMimeType(imageUri));
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                            try {
                                activity.startActivity(shareIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT, true).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                holder.linear_layout_download_image.setOnClickListener(this.downloadMediaItem(filesList.get(position).getFile()));
                break;
            }
        }






    }
    @Override
    public int getItemViewType(int position) {
        return  filesList.get(position).getViewType();
    }
    public int getItemCount() {
        return filesList.size();
    }



    public View.OnClickListener downloadMediaItem(final File sourceFile) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Runnable(){
                    @Override
                    public void run() {
                        try {
                            copyFile(sourceFile, new File(Environment.getExternalStorageDirectory().toString() + DIRECTORY_TO_SAVE_MEDIA_NOW + sourceFile.getName()));
                            Toasty.error(activity,activity.getResources().getString(R.string.save_successful_message), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("RecyclerV", "onClick: Error:"+e.getMessage() );
                            Toasty.error(activity,activity.getResources().getString(R.string.save_error_message), Toast.LENGTH_LONG).show();
                        }
                    }
                }.run();
            }
        };
    }

    /**
     * copy file to destination.
     *
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public  void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }

        MediaScannerConnection.scanFile(activity.getApplicationContext(), new String[] { destFile.getAbsolutePath() },
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = Uri.fromFile(destFile);
            scanIntent.setData(contentUri);
            activity.sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(destFile.getAbsolutePath()));
            activity.sendBroadcast(intent);
        }

    }



    public  class ImageHolder extends RecyclerView.ViewHolder {
        private final LinearLayout linear_layout_download_image;
        private final LinearLayout linear_layout_share_image;
        private final LinearLayout linear_layout_whatsapp_image;
        ImageView imageViewImageMedia;
        CardView cardViewImageMedia;
        Button buttonImageDownload;

        public ImageHolder(View itemView) {
            super(itemView);
            imageViewImageMedia = (ImageView) itemView.findViewById(R.id.imageViewImageMedia);
            cardViewImageMedia = (CardView) itemView.findViewById(R.id.cardViewImageMedia);
            buttonImageDownload = (Button) itemView.findViewById(R.id.buttonImageDownload);
            linear_layout_download_image = (LinearLayout) itemView.findViewById(R.id.linear_layout_download_image);
            linear_layout_share_image = (LinearLayout) itemView.findViewById(R.id.linear_layout_share_image);
            linear_layout_whatsapp_image = (LinearLayout) itemView.findViewById(R.id.linear_layout_whatsapp_image);
        }

    }
    public  class VideoHolder extends RecyclerView.ViewHolder {
        private final LinearLayout linear_layout_whatsapp_video;
        private final LinearLayout linear_layout_download_video;
        private final LinearLayout linear_layout_share_video;
        VideoView videoViewVideoMedia;
        CardView cardViewVideoMedia;
        Button buttonVideoDownload;

        public VideoHolder(View itemView) {
            super(itemView);
            videoViewVideoMedia = (VideoView) itemView.findViewById(R.id.videoViewVideoMedia);
            cardViewVideoMedia = (CardView) itemView.findViewById(R.id.cardViewVideoMedia);
            buttonVideoDownload = (Button) itemView.findViewById(R.id.buttonVideoDownload);
            linear_layout_download_video = (LinearLayout) itemView.findViewById(R.id.linear_layout_download_video);
            linear_layout_share_video = (LinearLayout) itemView.findViewById(R.id.linear_layout_share_video);
            linear_layout_whatsapp_video = (LinearLayout) itemView.findViewById(R.id.linear_layout_whatsapp_video);
        }
    }
    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme()!=null){
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = activity.getApplicationContext().getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                        .toString());
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        fileExtension.toLowerCase());
            }
        }else{
            mimeType = "*/*";
        }
        return mimeType;
    }
}