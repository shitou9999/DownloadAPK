package com.my_app_study.com.myapp1.net.rshttp;

import android.graphics.Bitmap;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okio.BufferedSink;

/**
 * Created by MT3020 on 2015/11/4.
 */
public class FileUploadRequestBody extends RequestBody {

    MediaType mMediaDiff;
    AbsAsyncTask mUploadAsyncTask;
    String mFileName;

    Object data;

    int perLen = 1024*16;

    public FileUploadRequestBody(MediaType mMediaDiff, AbsAsyncTask task, String fileName, Object data) {
        this.mMediaDiff = mMediaDiff;
        this.mUploadAsyncTask = task;
        this.data = data;
        this.mFileName = fileName;
        checkData();

    }

    void checkData(){
        if(data instanceof Bitmap){
            Bitmap mData = (Bitmap)data;
            ByteArrayOutputStream bs = new ByteArrayOutputStream();

            mData.compress(Bitmap.CompressFormat.PNG, 50, bs);
            try {
                bs.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            data = bs.toByteArray();
        }

        if(!(data instanceof byte[] || data instanceof File)){
            throw new RuntimeException("mUploadData must be File or Bitmap or byte[]");
        }
    }


    @Override
    public MediaType contentType() {
        return mMediaDiff;
    }

    @Override
    public long contentLength() throws IOException {
        if (data instanceof byte[]){
            return ((byte[]) data).length;
        }else if(data instanceof File){
            return ((File) data).length();
        }

        return -1;
    }

    // 分段上传实现监听；以1KB 为单位
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (data instanceof byte[]){
            writeBytes(sink);
        }else if(data instanceof File){
            writeFile(sink);
        }

    }


    /**
     * 写入文件
     * @param sink
     * @throws IOException
     */
    private void writeFile(BufferedSink sink) throws IOException {


        File mData = (File) data;

        FileInputStream inputStream = new FileInputStream(mData);
        byte[] temBytes = new byte[perLen];
        long totalCount = mData.length();
        long ProgressCount =0;
        int lens =-1;
        while ((lens= inputStream.read(temBytes))>0){
            ProgressCount+=lens;
            sink.write(temBytes, 0, lens);
            if(mUploadAsyncTask!=null){
                mUploadAsyncTask.publishItemUpdateProgressChange(mFileName,ProgressCount * 1f / totalCount);
            }
        }
    }

    /**
     * 写入字节流
     * @param sink
     * @throws IOException
     */
    private void writeBytes(BufferedSink sink)  throws IOException {
        byte[] mData = (byte[])data;

        int perLen = 1024;
        int starIndex = mData.length%perLen;
        // 先上传碎片
        sink.write(mData, 0, starIndex);
        while (starIndex< mData.length){
            sink.write(mData,starIndex,perLen);
            starIndex+=perLen;
            if(mUploadAsyncTask!=null){
                mUploadAsyncTask.publishItemUpdateProgressChange(mFileName,1f * starIndex / mData.length);
            }
        }

    }
}








    /*
    private void writeBitMap(BufferedSink sink) throws IOException{
        Bitmap mData = (Bitmap) mUploadData;
        int sumCount = mData.getHeight()*mData.getWidth()*4;
        Te convent = new Te(sink.outputStream(), sumCount);
        mData.compress(Bitmap.CompressFormat.PNG,40,convent);

    }
    class Te extends OutputStream{
        Te (OutputStream outputStream, int sumCount){
            target = outputStream;
            this.sumCount = sumCount;
        }
        OutputStream target;
        float sumCount;
        int progress;
        @Override
        public void write(int oneByte) throws IOException {
            target.write(oneByte);
            progress++;
            if(progress/1024==0&&mUploadAsyncTask!=null){
                mUploadAsyncTask.publishItemUpdataProgressChange(progress*1f/sumCount);
            }

        }

        @Override
        public void close() throws IOException {
            target.close();
            super.close();
        }
        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            target.write(buffer, offset, count);
            super.write(buffer, offset, count);
        }
        @Override
        public void flush() throws IOException {
            target.flush();
            super.flush();
        }
        @Override
        public void write(byte[] buffer) throws IOException {
            target.write(buffer);
            super.write(buffer);
        }
    }
    */