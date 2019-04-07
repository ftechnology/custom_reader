package com.microasset.saiful.drawings;

import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.entity.DrawingObjectEntity;
import com.microasset.saiful.util.Convert;

import org.json.JSONException;
import org.json.JSONObject;

import static com.microasset.saiful.util.Convert.toFloat;
import static com.microasset.saiful.util.Convert.toInt;
import static java.nio.file.Paths.get;

/**
 * factory to generate different types of objects (factory Pattern)
 */
public class ShapeFactory {

    public static  String RECTANGLE = "RECTANGLE";
    public static  String LINE = "LINE";
    public static  String SYMBOL = "SYMBOL";
    public static  String SYMBOL_YES = "SYMBOL_YES";
    public static  String SYMBOL_CROSS = "SYMBOL_CROSS";

    //use getShape method to get object of type shape
    public static Shape getShape(String shapeType){
        if(shapeType == null){
            return null;
        }
        if(shapeType.equalsIgnoreCase(LINE)){
            return new Line(LINE);

        } else if(shapeType.equalsIgnoreCase(RECTANGLE)){
            return new Rectangle(RECTANGLE);

        } else if(shapeType.equalsIgnoreCase(SYMBOL)){
            return new ShapeSymbol(SYMBOL);

        }else if(shapeType.equalsIgnoreCase(SYMBOL_YES)){
            return new ShapeYes(SYMBOL_YES);
        }else if(shapeType.equalsIgnoreCase(SYMBOL_CROSS)){
            return new ShapeCross(SYMBOL_CROSS);
        }
        return null;
    }

    public static ShapeSymbol createSymbol(float x, float y, int pageIndex, String bookId){
        int w = 48;
        int h = 36;
        //
        ShapeSymbol shapeSymbol = (ShapeSymbol) getShape(ShapeFactory.SYMBOL);
        shapeSymbol.points.add(new Point(x - w/2, y - h/2));
        shapeSymbol.points.add(new Point(x + w, y + h));
        shapeSymbol.setmImageIndex(R.drawable.ic_tick);
        shapeSymbol.setmPageIndex(pageIndex);
        shapeSymbol.setmBookId(bookId);
        return shapeSymbol;
    }

    public static Shape createSymbol(DrawingObjectEntity entity){

        if(entity == null)
            return null;
        Shape shape = null;
        //
        Shape shapeSymbol = (Shape) getShape(entity.getType());
        if(shapeSymbol != null){
            shapeSymbol.setId(entity.getId());
            String str = entity.getJsonData();
            try {
                JSONObject jsonObject = new JSONObject(str);

                float l =  Convert.toFloat(jsonObject.getString("l"));// jsonObject.get("l");
                float t =  Convert.toFloat(jsonObject.getString("t"));// jsonObject.get("l");
                float r =  Convert.toFloat(jsonObject.getString("r"));// jsonObject.get("l");
                float b =  Convert.toFloat(jsonObject.getString("b"));// jsonObject.get("l");
                //
                int mStrokeWidth =  Convert.toInt(jsonObject.getString("mStrokeWidth"));// jsonObject.get("l");
                int mAlpha =  Convert.toInt(jsonObject.getString("mAlpha"));// jsonObject.get("l");
                int mLineColor =  Convert.toInt(jsonObject.getString("mLineColor"));// jsonObject.get("l");
                int mTextColor =  Convert.toInt(jsonObject.getString("mTextColor"));// jsonObject.get("l");
                //
                shapeSymbol.setLineColor(mLineColor);
                shapeSymbol.setmAlpha(mAlpha);
                shapeSymbol.setmStrokeWidth(mStrokeWidth);
                shapeSymbol.setmTextColor(mTextColor);
                //
                shapeSymbol.points.add(new Point(l, t));
                shapeSymbol.points.add(new Point(r, b));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            shapeSymbol.setmPageIndex(Convert.toInt(entity.getPagenumber()));
            shapeSymbol.setmBookId(entity.getBookId());
            //
            shape = shapeSymbol;
        }

        //
        return shape;
    }

    public static Shape createShape(float x, float y, int w, int h, int pageIndex, String bookId, String type){
        //
        Shape rectangle = ShapeFactory.getShape(type);
        rectangle.points.add(new Point(x - w/2, y - h/2));
        rectangle.points.add(new Point(x + w, y + h));
        rectangle.setmBookId(bookId);
        //
        rectangle.setmPageIndex(pageIndex);
        return rectangle;
    }

}
