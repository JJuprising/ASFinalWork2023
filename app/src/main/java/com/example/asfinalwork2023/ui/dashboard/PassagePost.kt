package com.example.asfinalwork2023.ui.dashboard

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.asfinalwork2023.R
import kotlinx.android.synthetic.main.activity_passage_post.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class PassagePost : AppCompatActivity() {

    private val takePhoto = 1
    private val fromAlbum = 2
    private lateinit var imageUri: Uri
    lateinit var outputImage: File
    private val dbHelper: PassageDBHelper = PassageDBHelper(this, "Passage.db", 1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passage_post)
        PassagePostImage.setOnClickListener {
            // 创建File对象存储拍照后的图片
            outputImage = File(externalCacheDir, "output_image.jpg")
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    this, "com.example.asfinalwork2023.fileprovider", outputImage
                )
            } else {
                Uri.fromFile(outputImage)
            }
            // 启动相机程序
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, takePhoto)
        }
        PassagePostAlbumButton.setOnClickListener {
            // 打开文件选择器
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            // 指定只显示照片
            intent.type = "image/*"
            startActivityForResult(intent, fromAlbum)
        }
        PassagePostSubmitButton.setOnClickListener {
            val currentImage = PassagePostImage.drawable//获取当前图像
            if (currentImage == null) {
                Toast.makeText(this, "请上传封面~", 3).show()
            } else {
                val title = PassagePostTitle.text.toString()
                val content = PassagePostContent.text.toString()
                val bitmap: Bitmap = (currentImage as BitmapDrawable).bitmap
                if (title.length < 3) {
                    Toast.makeText(this, "请填写更长的标题", 3).show()
                } else if (content.length < 5) {
                    Toast.makeText(this, "你可以写的再多一点喔", 3).show()
                } else {
                    submitPost(title, content, bitmap)//提交文章数据
                }
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    // 将拍摄的照片显示出来
                    val bitmap =
                        BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    saveBitmap(bitmap)
                    PassagePostImage.setImageBitmap(rotateIfRequired(bitmap))
                }
            }
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 将选择的照片显示
                        val bitmap = getBitmapFromUri(uri)
                        PassagePostImage.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri, "r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        return when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedBitmap
    }

    private fun saveBitmap(bmp: Bitmap) {
        val tags = "saveBitmap"
        val f = System.currentTimeMillis().toString() + ".jpg"
        val path = Environment.getExternalStorageDirectory().toString() + "/DCIM/"
        val file= File(path + f)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 10, fos)
            Log.d(tags, "saved!")
        } catch (e: Exception) {
            Log.d(tags, "error!" + e.message)
        } finally {
            fos?.flush()
            fos?.close()
        }
    }

    private fun submitPost(title: String, content: String, Image: Bitmap) {//提交文章动作
        // Convert Bitmap to byte array
        val stream = ByteArrayOutputStream()
        Image.compress(Bitmap.CompressFormat.PNG, 10, stream)//压缩图片
        val byteArray = stream.toByteArray()//转成字节数组
        if (byteArray.size < 100) {
            Toast.makeText(this, "请上传更大的封面~", 3).show()
        } else {
            val passage = PassageInfoByte(title, content, byteArray)
            savePassagetoDB(passage)
            Toast.makeText(this, "发送成功", 3).show()
            finish()
        }
    }

    private fun savePassagetoDB(passage: PassageInfoByte) {//文章数据传入数据库
        val db = dbHelper.writableDatabase
        val values = ContentValues()

        values.put("title", passage.title)
        values.put("content", passage.content)
        values.put("picture", passage.picture)

        db.insert("Passage", null, values)
        db.close()
    }
}



