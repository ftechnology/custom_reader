package com.microasset.saiful.appfrw;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.model.TTTModel;
import com.microasset.saiful.util.NetworkUtil;
import com.microasset.saiful.util.Utill;

import java.util.List;

public class MLKit extends Object{
    private ProgressDialog mDialog;
    Context context;
    static MLKit mlKit;
    FirebaseVisionText fbVisionText;

    public static MLKit getInstance(){
        if(mlKit == null){
            mlKit = new MLKit();
        }
        return mlKit;
    }

    public FirebaseVisionText getFbVisionText(){
        return fbVisionText;
    }

    public void clearData(){
        fbVisionText = null;
    }

    public void getTextFromImage(Context c, Bitmap bitmap, final NotifyObserver notifyObserver){
        context = c;
        if(bitmap == null){
            Toast.makeText(context, "Image not found!", Toast.LENGTH_LONG).show();
            return;
        }

        //ImageLoader imageLoader = new ImageLoader(context);
        //Bitmap bitmap = imageLoader.loadBitmapFromAsset("book/SSCE/cover_images/SSCE - History.jpg" );
        //bitmap = imageLoader.cropBitmap(bitmap, 80, 96, 120, 40);
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        showProgressDialog();
        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                hideDialog();
                                fbVisionText = firebaseVisionText;
                                ResponseObject responseObject = new ResponseObject();
                                responseObject.setResponseMsg(ResponseObject.mSuccess);
                                responseObject.setDataObject(firebaseVisionText.getTextBlocks());
                                notifyObserver.update(responseObject);

                                /*
                                for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {



                                    Rect boundingBox = block.getBoundingBox();
                                    Point[] cornerPoints = block.getCornerPoints();
                                    String text = block.getText();

                                    TTTModel ttsModel = new TTTModel();
                                    if(isNetwork){
                                        ttsModel.translateText(text, "bn", new TTTModel.TTSListener() {
                                            @Override
                                            public void onTranslation(String nativeText, String translatedText) {
                                                Toast.makeText(context, translatedText, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                                    }

                                    for (FirebaseVisionText.Line line: block.getLines()) {

                                        for (FirebaseVisionText.Element element: line.getElements()) {

                                        }
                                    }
                                }*/
                            }

                        })
                .addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideDialog();
                        // Task failed with an exception
                        // ...
                        ResponseObject responseObject = new ResponseObject();
                        responseObject.setResponseMsg(ResponseObject.mFailed);
                        notifyObserver.update(responseObject);

                    }
                });
    }

    private void processTextBlock(FirebaseVisionText result) {
        // [START mlkit_process_text_block]
        String resultText = result.getText();
        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionText.Line line: block.getLines()) {
                String lineText = line.getText();
                Float lineConfidence = line.getConfidence();
                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();
                    Float elementConfidence = element.getConfidence();
                    List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }
        // [END mlkit_process_text_block]
    }


    private void showProgressDialog(){
        if(mDialog == null){
            mDialog = new ProgressDialog(context);
        }
        mDialog.setMessage(context.getString(R.string.STR_LOADING));
        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void hideDialog(){
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
