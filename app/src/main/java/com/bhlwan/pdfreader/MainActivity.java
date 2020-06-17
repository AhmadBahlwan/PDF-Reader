package com.bhlwan.pdfreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    ListView lv_pdf;
    public static ArrayList<File>fileList = new ArrayList<>();
    public static PDFAdapter obj_adapter;
    public static int REQUEST_PERMISSION = 1;
    public static String ROOT_DIRECTORY = "storage/emulated/0";
    boolean permission;
    File dir;
    SharedPreferences sharedPreferences;
    int sorting_option_id;
    static boolean is_Descending;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_pdf = findViewById(R.id.list_view);
        sharedPreferences = getSharedPreferences("option id", Context.MODE_PRIVATE);
        sorting_option_id = sharedPreferences.getInt("sorting_option", R.id.sort_by_name);
        is_Descending = sharedPreferences.getBoolean("IS_CHECKED",false);
        dir = new File(ROOT_DIRECTORY);
        permission_fn();
        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ViewPDFFiles.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        MenuItem menu_item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView)menu_item.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                obj_adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort){
            SortingFragmentDialog dialog = new SortingFragmentDialog();
            dialog.show(getSupportFragmentManager(),"OptionDialog");
        }
        return true;
    }

    /** Check if permission are granted*/
    private void permission_fn(){

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))){

            }else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            }

        }else {
            permission = true;
            getFile(dir);
            sortBy(sorting_option_id);
            obj_adapter = new PDFAdapter(getApplicationContext(),fileList);
            lv_pdf.setAdapter(obj_adapter);
        }
    }
        /** TO Compare files in order to sort them*/
    public static Comparator name_compartor = new Comparator<File>() {

        public int compare(File file1, File file2) {
            if (is_Descending)
              return file2.getName().compareTo(file1.getName());
            else
              return file1.getName().compareTo(file2.getName());
        }
    };
    public static Comparator size_compartor = new Comparator<File>() {
        public int compare(File file1, File file2) {
            if (is_Descending)
                return (int) (file2.length() - file1.length());
            else
                return (int) (file1.length() - file2.length());        }
    };
    public static Comparator date_compartor = new Comparator<File>() {
        public int compare(File file1, File file2) {
            if (is_Descending)
                return (int)(file2.lastModified() - file1.lastModified());
            else
                return (int) (file1.lastModified() - file2.lastModified());
        }
    };
    
      /**chose the sorting type*/
    public static void sortBy(int sorting_id){
        switch (sorting_id){
            case R.id.sort_by_name:
                Collections.sort(fileList,name_compartor);
                break;
            case R.id.sort_by_size:
                Collections.sort(fileList,size_compartor);
                break;
            case R.id.sort_by_date:
                Collections.sort(fileList,date_compartor);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION){
            if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                permission = true;
                getFile(dir);
                sortBy(sorting_option_id);
                obj_adapter = new PDFAdapter(getApplicationContext(),fileList);
                lv_pdf.setAdapter(obj_adapter);
            } else {
                Toast.makeText(this,"Please Allow the Permission",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**Add all files in device to fileList variable*/
    public ArrayList<File> getFile(File dir){
        File listFiles[] = dir.listFiles();
        if (listFiles!=null && listFiles.length>0){

            for (int i=0; i<listFiles.length;i++){
                if (listFiles[i].isDirectory()){
                    getFile(listFiles[i]);
                }else {
                    boolean fileIsExist = false;
                    if (listFiles[i].getName().endsWith(".pdf")){
                        for (int j=0; j<fileList.size();j++){
                            if (fileList.get(j).getName().equals(listFiles[i].getName())){
                                fileIsExist = true;
                            }
                        }

                        if (!fileIsExist){
                            fileList.add(listFiles[i]);
                        }
                    }
                }
            }
        }
        return fileList;
    }
}
