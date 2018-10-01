package com.example.user.notesaltarix;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.notesaltarix.adapters.Note;
import com.example.user.notesaltarix.database.DBhelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * Activity редактирования заметок заметок
 * @author Николай Шлянкин
 * @version 1.0
 */
public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener{
    private FloatingActionButton changeImageButton, editNoteButton;
    private EditText tittleView, dataView;
    private ImageView imageView;

    private DBhelper dBhelper;

    private File mTempPhoto;
    private String mImageUri = "";
    private static final int REQUEST_CODE_PERMISSION_RECEIVE_CAMERA = 102;
    private static final int REQUEST_CODE_TAKE_PHOTO = 103;

    private RadioGroup importantGroup;
    int important = 0;

    Note currentNote;
    Note newNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        dBhelper = new DBhelper(this);
        final Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        if(id != -1) {
            currentNote = dBhelper.getNote(id);

            tittleView = (EditText) findViewById(R.id.tittleView);
            dataView = (EditText) findViewById(R.id.dataView);
            imageView = (ImageView) findViewById(R.id.imageView2);
            changeImageButton = (FloatingActionButton) findViewById(R.id.changeImageButton);
            editNoteButton = (FloatingActionButton) findViewById(R.id.editNoteButton);

            tittleView.setText(currentNote.getTittle());
            dataView.setText(currentNote.getData());
            mImageUri = currentNote.getUri();
            important = currentNote.getImportant();

            Glide.with(this)
                    .load(mImageUri)
                    .into(imageView);

            importantGroup = (RadioGroup) findViewById(R.id.importantGroup);
            if(important != 0) {
                RadioButton radioButton[] = {(RadioButton) findViewById(R.id.radioButton3), (RadioButton) findViewById(R.id.radioButton2), (RadioButton) findViewById(R.id.radioButton1)};
                radioButton[important - 1].setEnabled(true);
            }
            importantGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (i) {
                        case -1:
                            important = 0;
                            break;
                        case R.id.radioButton1:
                            important = 3;
                            break;
                        case R.id.radioButton2:
                            important = 2;
                            break;
                        case R.id.radioButton3:
                            important = 1;
                            break;
                    }
                }
            });

        } else {
            Toast.makeText(this, "Note not found. Try again later...", Toast.LENGTH_LONG);
            onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_create_and_edit_activity, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addImageItem:
                onClick(changeImageButton);
                return true;
            case R.id.saveItem:
                onClick(editNoteButton);
                return true;
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Метод отправляет запрос в приложения-галереи и фото-приложения
     */
    private void addPhoto() {
        //Проверяем разрешение на работу с камерой
        boolean isCameraPermissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        //Проверяем разрешение на работу с внешнем хранилещем телефона
        boolean isWritePermissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        //Если разрешения != true
        if(!isCameraPermissionGranted || !isWritePermissionGranted) {

            String[] permissions;//Разрешения которые хотим запросить у пользователя

            if (!isCameraPermissionGranted && !isWritePermissionGranted) {
                permissions = new String[] {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            } else if (!isCameraPermissionGranted) {
                permissions = new String[] {android.Manifest.permission.CAMERA};
            } else {
                permissions = new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            }
            //Запрашиваем разрешения у пользователя
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION_RECEIVE_CAMERA);
            //снова проверяем разрешения и запускаем метод заново, если разрешения получены
            isCameraPermissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            isWritePermissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (isCameraPermissionGranted && isWritePermissionGranted)
                addPhoto();
        } else {
            //Если все разрешения получены
            try {
                mTempPhoto = createTempImageFile(getExternalCacheDir());
                //Создаём лист с интентами для работы с изображениями
                List<Intent> intentList = new ArrayList<>();
                Intent chooserIntent = null;
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(EditNoteActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        mTempPhoto);
                mImageUri = uri.toString();
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                intentList = addIntentsToList(this, intentList, pickIntent);
                intentList = addIntentsToList(this, intentList, takePhotoIntent);

                if (!intentList.isEmpty()) {
                    chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),"Choose your image source");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
                }

                /*После того как пользователь закончит работу с приложеним(которое работает с изображениями)
                 будет вызван метод onActivityResult
                */
                startActivityForResult(chooserIntent, REQUEST_CODE_TAKE_PHOTO);
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage(), e);
            }
        }
    }

    /**
     * @return абсолютный путь файла из URI
     */
    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int columnIndex = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    /**
     * @param storageDir абсолютный путь к каталогу конкретного приложения на
     * основном общем /внешнем устройстве хранения, где приложение может размещать
     * файлы кеша, которыми он владеет.
     */
    public static File createTempImageFile(File storageDir) throws IOException {
        // Генерируем имя файла
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());//получаем время
        String imageFileName = "photo_" + timeStamp;//состовляем имя файла
        //Создаём файл

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    /**
     * Метод для добавления интента в лист интентов
     */
    public static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode , resultCode , data);
        switch (requestCode){
            case REQUEST_CODE_TAKE_PHOTO:
                if(resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {
                        mImageUri = getRealPathFromURI(data.getData());
                        if(mImageUri!= null && !mImageUri.equals("")) {
                            Glide.with(this)
                                    .load(mImageUri)
                                    .into(imageView);
                        }
                    } else if (mTempPhoto != null) {
                        mImageUri = Uri.fromFile(mTempPhoto).toString();
                        if(mImageUri != null && !mImageUri.equals("")) {
                            Glide.with(this)
                                    .load(mImageUri)
                                    .into(imageView);
                        }
                    } else {
                        Glide.with(this)
                                .load(mImageUri)
                                .into(imageView);
                        Toast.makeText(this, "Try again. Cant find image", Toast.LENGTH_SHORT);
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changeImageButton:
                addPhoto();
                break;
            case R.id.editNoteButton:
                newNote = new Note(tittleView.getText().toString(), dataView.getText().toString(), mImageUri, currentNote.getId(), important);
                dBhelper.updateNote(newNote, currentNote.getId());
                onBackPressed();
                break;
            case R.id.deleteNoteButton:
                if(currentNote.getId() != -1) {
                    new AlertDialog.Builder(this)
                            .setTitle("Delete note?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dBhelper.deleteNote(currentNote.getId());
                                    onBackPressed();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                } else {
                    Toast.makeText(this, "Note is not deleted. Try again later...", Toast.LENGTH_LONG);
                }
                break;
        }
    }
}
