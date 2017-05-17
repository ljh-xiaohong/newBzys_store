package com.bapm.bzys.newBzys_store.widget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 图片简单处理工具类
 */
public class ImageUtils {

	/**
	 * 屏幕宽
	 * 
	 * @param context
	 * @return
	 */
	public static int getWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/**
	 * 屏幕高
	 * 
	 * @param context
	 * @return
	 */
	public static int getHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	/**
	 * 解决小米、魅族等定制ROM
	 * 
	 * @param context
	 * @param intent
	 * @return
	 */
	public static Uri getUri(Context context, Intent intent) {
		Uri uri = intent.getData();
		String type = intent.getType();
		if (uri.getScheme().equals("file") && (type.contains("image/"))) {
			String path = uri.getEncodedPath();
			if (path != null) {
				path = Uri.decode(path);
				ContentResolver cr = context.getContentResolver();
				StringBuffer buff = new StringBuffer();
				buff.append("(").append(Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
				Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI, new String[] { Images.ImageColumns._ID },
						buff.toString(), null, null);
				int index = 0;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					index = cur.getColumnIndex(Images.ImageColumns._ID);
					// set _id value
					index = cur.getInt(index);
				}
				if (index == 0) {
					// do nothing
				} else {
					Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
					if (uri_temp != null) {
						uri = uri_temp;
						Log.i("urishi", uri.toString());
					}
				}
			}
		}
		return uri;
	}

	/**
	 * 根据文件Uri获取路径
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String getFilePathByFileUri(Context context, Uri uri) {
		String filePath = null;
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		if (cursor.moveToFirst()) {
			filePath = cursor.getString(cursor.getColumnIndex(Images.Media.DATA));
		}
		cursor.close();
		return filePath;
	}

	/**
	 * 根据图片原始路径获取图片缩略图
	 * 
	 * @param imagePath
	 *            图片原始路径
	 * @param width
	 *            缩略图宽度
	 * @param height
	 *            缩略图高度
	 * @return
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 不加载直接获取Bitmap宽高
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		if (bitmap == null) {
			// 计算缩放比
			int h = options.outHeight;
			int w = options.outWidth;
			Log.i("test", "optionsH" + h + "optionsW" + w);
			int beWidth = w / width;
			int beHeight = h / height;
			int rate = 1;
			if (beWidth < beHeight) {
				rate = beWidth;
			} else {
				rate = beHeight;
			}
			if (rate <= 0) {// 图片实际大小小于缩略图,不缩放
				rate = 1;
			}
			options.inSampleSize = rate;
			options.inJustDecodeBounds = false;
			// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
			bitmap = BitmapFactory.decodeFile(imagePath, options);
			// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

	public static String compressImage(String filePath, String targetPath, int quality) {
		Bitmap bm = getSmallBitmap(filePath);// 获取一定尺寸的图片
//		int degree = readPictureDegree(filePath);// 获取相片拍摄角度
//		if (degree != 0) {// 旋转照片角度，防止头像横着显示
//			bm = rotateBitmap(bm, degree);
//		}
		File outputFile = new File(targetPath);
		try {
			if (!outputFile.exists()) {
				outputFile.getParentFile().mkdirs();
				// outputFile.createNewFile();
			} else {
				outputFile.delete();
			}
			FileOutputStream out = new FileOutputStream(outputFile);
			bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
		} catch (Exception e) {
		}
		return outputFile.getPath();
	}

	/**
	 * 根据路径获得图片信息并按比例压缩，返回bitmap
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 只解析图片边沿，获取宽高
		BitmapFactory.decodeFile(filePath, options);
		// 计算缩放比
		options.inSampleSize = calculateInSampleSize(options, 480, 800);
		// 完整解析图片返回bitmap
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 获取照片角度
	 * 
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 旋转照片
	 * 
	 * @param bitmap
	 * @param degress
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
		if (bitmap != null) {
			Matrix m = new Matrix();
			m.postRotate(degress);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			return bitmap;
		}
		return bitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	/**
	 * 获取bitmap
	 *
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmapByPath(String filePath) {
		return getBitmapByPath(filePath, null);
	}

	public static Bitmap getBitmapByPath(String filePath,
										 BitmapFactory.Options opts) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			File file = new File(filePath);
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis, null, opts);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 裁剪图片质量
	 * @param ctx
	 * @param targetPath
	 * @param bitmap
	 * @param quality
	 * @throws IOException
	 */
	public static void cutQualityImage(Context ctx, String targetPath,
									   Bitmap bitmap, int quality) throws IOException {
		if (bitmap != null) {
			OutputStream bos = null;
			try {
				//File targetFile = new File(targetPath);
				Log.e("filepath","  "+targetPath.substring(0,
						targetPath.lastIndexOf(File.separator)));
				File file = new File(targetPath.substring(0,
						targetPath.lastIndexOf(File.separator)));
				if (!file.exists()) {
					file.mkdirs();
				}
				File targetFile = new File(targetPath);
				if(targetFile.exists()){
					targetFile.delete();
				}
				targetFile.createNewFile();
				bos=new FileOutputStream(targetFile);
//                KLog.e(targetPath);
				if (targetPath.endsWith(".png")) {
					bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos);
				} else if (targetPath.endsWith(".jpg")) {
					bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
				} else {
					bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (bos != null) {
					bos.flush();
					bos.close();

					if (bitmap != null) {
						bitmap.recycle();
						bitmap = null;
					}
				}
			}
		}
	}
}
