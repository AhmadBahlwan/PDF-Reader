package com.bhlwan.pdfreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;
import java.lang.reflect.Method;

public class ViewPDFFiles extends AppCompatActivity implements OnPageChangeListener {

    PDFView pdfView;
    int position = -1;
    String filePath;
    static int pageNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdffiles);

        pdfView = findViewById(R.id.pdfView);
        pdfView.setBackgroundColor(Color.LTGRAY);
        position = getIntent().getIntExtra("position",-1);
        Log.d("page",String.valueOf(pageNumber));
        System.out.println(MainActivity.fileList.get(position).getName());
        Intent intent=getIntent();
        if(intent!=null) {

            String action = intent.getAction();

            String type = intent.getType();

            if (Intent.ACTION_VIEW.equals(action) && type.endsWith("pdf")) {

                // Get the file from the intent object

                Uri file_uri = intent.getData();

                if (file_uri != null){
                    filePath = getRealPathFromURI(file_uri);
                    pageNumber = getPageNumber(filePath);
                    displayPDF(filePath);
                    return;
                }
            }
        }
        filePath = MainActivity.fileList.get(position).getPath();
        pageNumber = getPageNumber(filePath);
        displayPDF();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pdf_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.action_share){
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            shareIntent.setType("application/pdf");


            try{ /// disable runtime check
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
            startActivity(shareIntent);
        }
        return true;
    }

    /**Open pdf file from within the application*/
    private void displayPDF(){

        pdfView.fromFile(MainActivity.fileList.get(position))
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableSwipe(true)
                .enableAnnotationRendering(true)
                .spacing(4)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();

        Log.d("test",MainActivity.fileList.get(position).getPath());
    }

    /**Open pdf file from the file explorer*/
    private void displayPDF(String filePath){

        pdfView.fromFile(new File(filePath))
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableSwipe(true)
                .spacing(4)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    /** Extract the file path from file explorer*/
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

        /** Save the current page number */
    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;

        //savePageNumber(,pageNumber);
    }


    private int getPageNumber(String key) {
        return getApplicationContext().getSharedPreferences("PAGE_NUMBER", Context.MODE_PRIVATE)
                .getInt(key, 0);
    }

    // save the chosen option of sorting
    private void savePageNumber(String key, int pageNumber) {
        SharedPreferences.Editor editor = getApplicationContext()
                .getSharedPreferences("PAGE_NUMBER", Context.MODE_PRIVATE).edit();
        editor.putInt(key, pageNumber);
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePageNumber(filePath,pageNumber);
    }
}
