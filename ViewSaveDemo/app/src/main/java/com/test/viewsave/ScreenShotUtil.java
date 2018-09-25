package com.test.viewsave;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuangyufeng on 2018/9/21.
 */

public class ScreenShotUtil {

    public static void saveScreenShot(View view, OnPicSaveResult onPicSaveResult) {
        Bitmap mBitmap;
        if (view instanceof ScrollView) {
            mBitmap = screenShot((ScrollView) view);
        } else if (view instanceof HorizontalScrollView) {
            mBitmap = screenShot((HorizontalScrollView) view);
        } else if (view instanceof ListView) {
            mBitmap = screenShot((ListView) view);
        } else if (view instanceof RecyclerView) {
            mBitmap = screenShot((RecyclerView) view);
        } else {
            mBitmap = screenShot(view);
        }
        saveBitmap(mBitmap, onPicSaveResult);

    }


    private static Bitmap screenShot(View view) {
        int w = view.getWidth();
        int h = view.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmp);
        /** 如果不设置canvas画布为白色，则生成透明 */
        c.drawColor(Color.WHITE);
        view.layout(0, 0, w, h);
        view.draw(c);
        return bmp;
    }

    private static Bitmap screenShot(ScrollView scrollView) {
        try {
            int h = 0;
            for (int i = 0; i < scrollView.getChildCount(); i++) {
                h += scrollView.getChildAt(i).getMeasuredHeight();
                scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
            }
            Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            scrollView.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap screenShot(HorizontalScrollView horizontalScrollView) {
        try {
            int w = 0;
            for (int i = 0; i < horizontalScrollView.getChildCount(); i++) {
                w += horizontalScrollView.getChildAt(i).getMeasuredWidth();
                horizontalScrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
            }
            Bitmap bitmap = Bitmap.createBitmap(w, horizontalScrollView.getMeasuredHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            horizontalScrollView.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Bitmap screenShot(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        int itemscount = adapter.getCount();
        int allitemsheight = 0;
        List<Bitmap> bmps = new ArrayList<>();

        for (int i = 0; i < itemscount; i++) {

            View childView = adapter.getView(i, null, listView);
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bmps.add(childView.getDrawingCache());
            allitemsheight += childView.getMeasuredHeight();
        }

        Bitmap bigBitmap = Bitmap.createBitmap(listView.getMeasuredWidth(), allitemsheight, Bitmap.Config.RGB_565);
        Canvas bigcanvas = new Canvas(bigBitmap);

        Paint paint = new Paint();
        int iHeight = 0;

        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();

            bmp.recycle();
        }
        return bigBitmap;
    }

    private static Bitmap screenShot(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(),
                        holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(recyclerView.getMeasuredWidth(), height, Bitmap.Config.RGB_565);
            Canvas bigCanvas = new Canvas(bigBitmap);
            Drawable lBackground = recyclerView.getBackground();
            if (lBackground instanceof ColorDrawable) {
                ColorDrawable lColorDrawable = (ColorDrawable) lBackground;
                int lColor = lColorDrawable.getColor();
                bigCanvas.drawColor(lColor);
            }

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }
            return bigBitmap;
        }
        return null;
    }


    private static void saveBitmap(final Bitmap bitmap, final OnPicSaveResult onPicSaveResult) {
        try {
            //
            if (bitmap != null && !bitmap.isRecycled()) {
                new Runnable() {
                    @Override
                    public void run() {
                        long maxSize = 307200L;
                        File destDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
                        String path = destDir.getPath() + "/" + System.currentTimeMillis() + ".jpg";
                        long length = compressFile(bitmap, path, 100);
                        if (length >= maxSize) {
                            compressFile(bitmap, path, 50);
                        }
                        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        scanIntent.setData(Uri.fromFile(new File(path)));
                        FrameworkUtil.getContext().sendBroadcast(scanIntent);
                        if (onPicSaveResult != null) {
                            onPicSaveResult.onPicSaveResult(true);
                        }
                    }
                }.run();
            } else {//保存失败
                if (onPicSaveResult != null) {
                    onPicSaveResult.onPicSaveResult(false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (onPicSaveResult != null) {
                onPicSaveResult.onPicSaveResult(false);
            }
        }
    }

    private static long compressFile(Bitmap bitmap, String filePath, int quanlity) {
        FileOutputStream fileOutputStream = null;

        try {

            File file = new File(filePath);
            if (file.isFile()) {
                file.delete();
            }

            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quanlity, fileOutputStream);
            long len = (new File(filePath)).length();
            long var8 = len;
            return var8;
        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception var18) {
                    var18.printStackTrace();
                }
            }

        }

        return 0L;
    }

}
