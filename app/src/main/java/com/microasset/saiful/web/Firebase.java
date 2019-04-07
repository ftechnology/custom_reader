package com.microasset.saiful.web;

import android.print.PrinterId;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.SharedPrefUtil;

import java.io.File;
import java.util.ArrayList;

public class Firebase {
    public static int USER_LOGIN_SUCCESS = 0;
    public static int USER_LOGIN_FAILED = -1;
    public static int USER_EMAIL_EXIST_FIREBASE = 2;
    public static int USER_EMAIL_ERROR = -3;
    //https://console.firebase.google.com/project/easyreaderbd/storage/easyreaderbd.appspot.com/files~2Fbook~2F
    private StorageReference mStorageRef;
    //FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public String mStatusMessage = "";
    private static final String DEBUG_TAG = "FireBase";
    // private String mSelectedBook = "";
    private String mServerRoot = "book";
    private DataObject mDownloadData;

    FirebaseAuth mAuth;

    public Firebase() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public void signOut(){
        mAuth.signOut();
    }

    public void login(String mobNumber, String password, final NotifyObserver notifyObserver){

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Log.d(DEBUG_TAG,  "signInWithEmailAndPassword -> user != null! Success");
            notifyUserInformation(notifyObserver, Firebase.USER_LOGIN_SUCCESS, "onSuccess");

        } else {
            mAuth.signInWithEmailAndPassword(mobNumber, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    notifyUserInformation(notifyObserver, Firebase.USER_LOGIN_SUCCESS, "onSuccess");
                }
            });
            mAuth.signInWithEmailAndPassword(mobNumber, password).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    notifyUserInformation(notifyObserver, Firebase.USER_LOGIN_FAILED, "onFailure");
                }
            });
        }
    }

    public void notifyUserInformation(NotifyObserver notifyObserver, int code, String message){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setResponseMsg(message);
        responseObject.setResponseCode(code);
        Log.d(DEBUG_TAG,   message);
        if(notifyObserver != null){
            notifyObserver.update(responseObject);
        }
    }

    public void addUser(final String mobNumber, final String password, final NotifyObserver notifyObserver){
        //
        mAuth.fetchProvidersForEmail(mobNumber).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>()
        {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task)
            {
                boolean isRegistered = !task.getResult().getProviders().isEmpty();
                if(isRegistered){
                    notifyUserInformation(notifyObserver, Firebase.USER_EMAIL_EXIST_FIREBASE, "onSuccess");
                    return;
                }
                if(!isRegistered){
                    mAuth.createUserWithEmailAndPassword(mobNumber, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            notifyUserInformation(notifyObserver, Firebase.USER_EMAIL_EXIST_FIREBASE, "onSuccess");
                        }
                    });

                    mAuth.createUserWithEmailAndPassword(mobNumber, password).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            notifyUserInformation(notifyObserver, Firebase.USER_EMAIL_ERROR, "onFailure");
                        }
                    });
                }
            }
        });
    }

    public void setDownloadData(DataObject downloadData){
        mDownloadData = downloadData;
    }

    /**
     *
     * @param bookObjects BookObject that contains image path, image name etc.
     * @param currentIndex current index
     * @param endIndex final index (exclusive i.e. stop when we reach this index)
     */
    public void downloadFilesRecursive(final ArrayList<DataObject> bookObjects, final int currentIndex, final int endIndex, final NotifyObserver notifyObserver) {
        try {
            if (currentIndex == endIndex) {
                notifyStatus(currentIndex, "", notifyObserver, ResponseObject.mComplete);
                return;
            }
            String localPath = bookObjects.get(currentIndex).getString("PAGE_IMAGE_PATH");
            String imgName = bookObjects.get(currentIndex).getString("PAGE_IMAGE");
            //
            String selectedBook = mDownloadData.getString("mSelectedBook");
            String selectedClass = mDownloadData.getString("mSelectedClass");
            String selectedVersion = mDownloadData.getString("mSelectedVersion");
            final String bookPath = selectedClass + selectedVersion + "/" + selectedBook;
            final String cloudPath = mServerRoot + "/" + bookPath + "/" + imgName;

            bookObjects.get(currentIndex).setValue(ResponseObject.mStatus, ResponseObject.mContinue);

            StorageReference pathReference = mStorageRef.child(cloudPath);
            File localFile = new File(localPath);
            mStatusMessage = ResponseObject.mFailed;

            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(DEBUG_TAG, cloudPath + "downloaded successfully!");
                    //notifyStatus(currentIndex, "", notifyObserver, ResponseObject.mContinue);
                    downloadFilesRecursive(bookObjects, currentIndex + 1, endIndex, notifyObserver);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    notifyStatus(currentIndex + 1, cloudPath, notifyObserver, ResponseObject.mFailed);
                }
            });
        }catch (Exception e){
            LogUtil.d(e.getMessage());
            notifyStatus(currentIndex + 1, "", notifyObserver, ResponseObject.mFailed);
            return;
        }
    }

    /**
     * download entire book
     */
    public void downloadFiles(ArrayList<DataObject> bookObjects, NotifyObserver notifyObserver) {
        downloadFilesRecursive(bookObjects, 0, bookObjects.size(), notifyObserver);
    }

    /**
     * download pages in specific index
     * @param bookObjects BookObject that contains image path, image name etc.
     * @param startIndex start index(inclusive)
     * @param endIndex end index (exclusive)
     */
    public void downloadFiles(final String userId, final String password, final ArrayList<DataObject> bookObjects, final int startIndex, final int endIndex, final NotifyObserver notifyObserver) {
        try {

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                /* perform your actions here*/
                doDownloadFiles(bookObjects, startIndex, endIndex, notifyObserver);
                Log.d(DEBUG_TAG, "signInWithEmailAndPassword -> user != null!");

            } else {
                mAuth.signInWithEmailAndPassword(userId, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        doDownloadFiles(bookObjects, startIndex, endIndex, notifyObserver);
                    }
                });
                mAuth.signInWithEmailAndPassword(userId, password).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(DEBUG_TAG, e.getMessage() + "signInWithEmailAndPassword -> cannot signInWithEmailAndPassword!");
                    }
                });
            }
        }catch (Exception e){
            LogUtil.d(e.getMessage());
            notifyStatus(startIndex, "", notifyObserver, ResponseObject.mFailed);
        }
    }

    public void doDownloadFiles(final ArrayList<DataObject> bookObjects, final int startIndex, final int endIndex, final NotifyObserver notifyObserver) {
        mStatusMessage = "";
        //
        try {
            String selectedBook = mDownloadData.getString("mSelectedBook");
            String selectedClass = mDownloadData.getString("mSelectedClass");
            String selectedVersion = mDownloadData.getString("mSelectedVersion");
            // String location = Constants.BOOK+"/"+mClassName+version+"/"+Constants.COVER_IMAGES;
            final String bookPath = selectedClass + selectedVersion + "/" + selectedBook;

            File file = new File(Constants.PATH_BOOK + "/" + bookPath);
            boolean success = file.mkdirs();
            LogUtil.d("success");
            //
            downloadFilesRecursive(bookObjects, startIndex, endIndex, notifyObserver);
        }catch (Exception e){
            LogUtil.d(e.getMessage());
            notifyStatus(startIndex, "", notifyObserver, ResponseObject.mFailed);
        }
    }

    public void doDownloadSolution(final NotifyObserver notifyObserver) {
        mStatusMessage = "";
        //
        try {
            String selectedBook = mDownloadData.getString("mSelectedBook");
            String selectedClass = mDownloadData.getString("mSelectedClass");
            String selectedVersion = mDownloadData.getString("mSelectedVersion");
            // String location = Constants.BOOK+"/"+mClassName+version+"/"+Constants.COVER_IMAGES;
            final String bookPath = selectedClass + selectedVersion + "/" + selectedBook;

            File file = new File(Constants.PATH_BOOK + "/" + bookPath);
            boolean success = file.mkdirs();
            LogUtil.d("success");
            //
            String localPath = Constants.PATH_BOOK+"/"+selectedClass+selectedVersion+"/"+selectedBook +"/"+selectedBook+" - link.xml";
            final String cloudPath = mServerRoot + "/" + bookPath + "/" +selectedBook+" - link.xml";
            StorageReference pathReference = mStorageRef.child(cloudPath);
            File localFile = new File(localPath);
            mStatusMessage = ResponseObject.mFailed;

            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(DEBUG_TAG, cloudPath + "downloaded successfully!");
                    notifyObserver.update(new ResponseObject(0,ResponseObject.mSuccess,null));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    notifyObserver.update(new ResponseObject(-1,ResponseObject.mFailed,null));
                }
            });

        }catch (Exception e){
            LogUtil.d(e.getMessage());
        }
    }

    public void doDownloadFile(final NotifyObserver notifyObserver) {
        mStatusMessage = "";
        //
        try {
            String selectedBook = mDownloadData.getString("mSelectedBook");
            String selectedClass = mDownloadData.getString("mSelectedClass");
            String selectedVersion = mDownloadData.getString("mSelectedVersion");
            // String location = Constants.BOOK+"/"+mClassName+version+"/"+Constants.COVER_IMAGES;
            final String bookPath = selectedClass + selectedVersion + "/" + selectedBook;

            File file = new File(Constants.PATH_BOOK + "/" + bookPath);
            boolean success = file.mkdirs();
            LogUtil.d("success");
            //
            String localPath = Constants.PATH_BOOK+"/"+selectedClass+selectedVersion+"/"+selectedBook +"/"+selectedBook+".xml";
            final String cloudPath = mServerRoot + "/" + bookPath + "/" +selectedBook+".xml";
            StorageReference pathReference = mStorageRef.child(cloudPath);
            File localFile = new File(localPath);
            mStatusMessage = ResponseObject.mFailed;

            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(DEBUG_TAG, cloudPath + "downloaded successfully!");
                    notifyObserver.update(new ResponseObject(0,ResponseObject.mSuccess,null));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    notifyObserver.update(new ResponseObject(-1,ResponseObject.mFailed,null));
                }
            });

        }catch (Exception e){
            LogUtil.d(e.getMessage());
        }
    }

    public void notifyStatus(int currentIndex, String cloudPath, NotifyObserver notifyObserver, String statusMessage){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setContentDownloaded(currentIndex);
        responseObject.setResponseMsg(statusMessage);
        mStatusMessage = statusMessage;
        // Handle any errors
        Log.d(DEBUG_TAG, cloudPath + "cannot download!");
        if(notifyObserver != null){
            notifyObserver.update(responseObject);
        }
    }
}
