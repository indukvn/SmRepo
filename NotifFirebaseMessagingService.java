package com.example.Feat.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.virmana.status_video.R;
import com.virmana.status_video.Provider.PrefManager;
import com.virmana.status_video.ui.Activities.CategoryActivity;
import com.virmana.status_video.ui.Activities.MainActivity;
import com.virmana.status_video.ui.Activities.VideoActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tamim on 26/10/2017.
 */

public class NotifFirebaseMessagingService  extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    Bitmap bitmap;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String type = remoteMessage.getData().get("type");
        String id = remoteMessage.getData().get("id");
        String title = remoteMessage.getData().get("title");
        String image = remoteMessage.getData().get("image");
        String icon = remoteMessage.getData().get("icon");
        String message = remoteMessage.getData().get("message");
        PrefManager prf = new PrefManager(getApplicationContext());
        if (!prf.getString("notifications").equals("false")) {
            if (type.equals("video")) {


                String video_id =remoteMessage.getData().get("id") ;
                String video_title = remoteMessage.getData().get("video_title") ;
                String video_description = remoteMessage.getData().get("video_description") ;
                String video_review = remoteMessage.getData().get("video_review");
                String video_comment = remoteMessage.getData().get("video_comment") ;
                String video_comments = remoteMessage.getData().get("video_comments");
                String video_downloads =remoteMessage.getData().get("video_downloads") ;
                String video_user = remoteMessage.getData().get("video_user") ;
                String video_userid = remoteMessage.getData().get("video_userid") ;
                String video_type = remoteMessage.getData().get("video_type") ;
                String video_extension =remoteMessage.getData().get("video_extension") ;
                String video_thumbnail =remoteMessage.getData().get("video_thumbnail");
                String video_image =remoteMessage.getData().get("video_image") ;
                String video_video = remoteMessage.getData().get("video_video") ;
                String video_userimage = remoteMessage.getData().get("video_userimage") ;
                String video_created = remoteMessage.getData().get("video_created") ;
                String video_tags = remoteMessage.getData().get("video_tags") ;
                String video_like = remoteMessage.getData().get("video_like") ;
                String video_love = remoteMessage.getData().get("video_love");
                String video_woow = remoteMessage.getData().get("video_woow") ;
                String video_angry = remoteMessage.getData().get("video_angry") ;
                String video_sad = remoteMessage.getData().get("video_sad");
                String video_haha = remoteMessage.getData().get("video_haha") ;

                sendStatusNotification(
                    id,
                    title,
                    image,
                    icon,
                    message,
                    video_id ,
                    video_title  ,
                    video_description,
                    video_review ,
                    video_comment  ,
                    video_comments ,
                    video_downloads ,
                    video_user  ,
                    video_userid  ,
                    video_type  ,
                    video_extension ,
                    video_thumbnail,
                    video_image ,
                    video_video  ,
                    video_userimage  ,
                    video_created  ,
                    video_tags  ,
                    video_like  ,
                    video_love ,
                    video_woow  ,
                    video_angry  ,
                    video_sad ,
                    video_haha
            );
            }else if (type.equals("link")) {
                    String link = remoteMessage.getData().get("link");
                    sendNotificationUrl(
                            id,
                            title,
                            image,
                            icon,
                            message,
                            link
                    );
            }else if(type.equals("category")){
                String category_title = remoteMessage.getData().get("title_category");
                String category_image = remoteMessage.getData().get("video_category");

                sendNotificationCategory(
                        id,
                        title,
                        image,
                        icon,
                        message,
                        category_title,
                        category_image);
            }
        }
    }
    private void sendNotificationCategory(
            String id,
            String title,
            String imageUri,
            String iconUrl,
            String message,
            String category_title,
            String category_image
    ) {


        Bitmap image = getBitmapfromUrl(imageUri);
        Bitmap icon = getBitmapfromUrl(iconUrl);
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));


        intent.putExtra("id", Integer.parseInt(id));
        intent.putExtra("title",category_title);
        intent.putExtra("image",category_image);
        intent.putExtra("from", "notification");




        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        int NOTIFICATION_ID = Integer.parseInt(id);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_w)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message);

        if (icon!=null){
            builder.setLargeIcon(icon);

        }else{
            builder.setLargeIcon(largeIcon);
        }
        if (image!=null){
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());


    }
    private void sendNotificationUrl(
            String id,
            String title,
            String imageUri,
            String iconUrl,
            String message,
            String url

    ) {


        Bitmap image = getBitmapfromUrl(imageUri);
        Bitmap icon = getBitmapfromUrl(iconUrl);


        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.setData(Uri.parse(url));
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        int NOTIFICATION_ID = Integer.parseInt(id);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_w)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message);

        if (icon!=null){
            builder.setLargeIcon(icon);

        }else{
            builder.setLargeIcon(largeIcon);
        }
        if (image!=null){
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());



    }


    private void sendStatusNotification(
            String id,
            String title,
            String imageUri,
            String iconUrl,
            String message,
            String   video_id ,
            String   video_title  ,
            String   video_description,
            String   video_review ,
            String   video_comment  ,
            String   video_comments ,
            String   video_downloads ,
            String   video_user  ,
            String   video_userid  ,
            String   video_type  ,
            String   video_extension ,
            String   video_thumbnail,
            String   video_image ,
            String   video_video  ,
            String   video_userimage  ,
            String   video_created  ,
            String   video_tags  ,
            String   video_like  ,
            String   video_love ,
            String   video_woow  ,
            String   video_angry  ,
            String   video_sad ,
            String   video_haha

    ){
        Bitmap image = getBitmapfromUrl(imageUri);
        Bitmap icon = getBitmapfromUrl(iconUrl);
        Intent intent = new Intent(this, VideoActivity.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));




        intent.putExtra("from","notification");

        intent.putExtra("id", Integer.parseInt(video_id));
        intent.putExtra("title", video_title);
        intent.putExtra("description", video_description);
        intent.putExtra("thumbnail", video_thumbnail);
        intent.putExtra("userid", Integer.parseInt(video_userid));
        intent.putExtra("user", video_user);
        intent.putExtra("userimage", video_userimage);
        intent.putExtra("type", video_type);
        intent.putExtra("video", video_video);
        intent.putExtra("image", video_image);
        intent.putExtra("extension", video_extension);
        intent.putExtra("comment", video_comment);
        intent.putExtra("downloads",Integer.parseInt( video_downloads));
        intent.putExtra("tags", video_tags);
        intent.putExtra("review", video_review);
        intent.putExtra("comments",Integer.parseInt( video_comments));
        intent.putExtra("created", video_created);
        intent.putExtra("woow", Integer.parseInt(video_woow));
        intent.putExtra("like", Integer.parseInt(video_like));
        intent.putExtra("love", Integer.parseInt(video_love));
        intent.putExtra("angry", Integer.parseInt(video_angry));
        intent.putExtra("sad", Integer.parseInt(video_sad));
        intent.putExtra("haha", Integer.parseInt(video_haha));




        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        int NOTIFICATION_ID = Integer.parseInt(id);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_w)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message);

        if (icon!=null){
            builder.setLargeIcon(icon);

        }else{
            builder.setLargeIcon(largeIcon);
        }
        if (image!=null){
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }




    /*
    *To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}