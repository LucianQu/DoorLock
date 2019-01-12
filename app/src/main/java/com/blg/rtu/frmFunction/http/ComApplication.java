package com.blg.rtu.frmFunction.http;

import android.content.Context;
import android.util.Log;

import com.blg.rtu3.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.L;

public class ComApplication {
    private static final String TAG = "ComApplication";
    private static Context context;

    public static Context getContext()
    {
        return context;
    }

    public static void init(Context paramContext)
    {
        context = paramContext;
        initImageLoader();
    }

    private static void initImageLoader()
    {
        Object localObject = new DisplayImageOptions.Builder().
                showImageOnLoading(R.mipmap.ic_default_bg).
                showImageForEmptyUri(R.mipmap.ic_default_bg).
                showImageOnFail(R.mipmap.ic_default_bg).
                cacheInMemory(true).
                cacheOnDisk(true).
                build();
        localObject = new ImageLoaderConfiguration.Builder(context).
                threadPoolSize(3).
                threadPriority(3).
                denyCacheImageMultipleSizesInMemory().
                diskCacheFileNameGenerator(new Md5FileNameGenerator()).
                tasksProcessingOrder(QueueProcessingType.FIFO).
                diskCacheFileCount(500).
                imageDownloader(new BaseImageDownloader(context, 500, 30000)).
                defaultDisplayImageOptions((DisplayImageOptions)localObject);
        try
        {
            ((ImageLoaderConfiguration.Builder)localObject).
                    diskCache(new UnlimitedDiskCache(FileConfig.getImageCacheDir(), null, new Md5FileNameGenerator()));
        }
        catch (Exception localException)
        {
            Log.e("ComApplication", localException.toString());
        }
        L.writeLogs(false);
        localObject = ((ImageLoaderConfiguration.Builder)localObject).build();
        ImageLoader.getInstance().init((ImageLoaderConfiguration)localObject);
    }
}
