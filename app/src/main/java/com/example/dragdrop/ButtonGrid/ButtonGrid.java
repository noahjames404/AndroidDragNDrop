package com.example.dragdrop.ButtonGrid;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class ButtonGrid {

    private final String TAG = "ButtonGrid";
    private double width,height;
    private int row_count, col_count;
    private RelativeLayout layout;
    private double x_quotient,y_quotient;
    private List<ButtonProperties> buttonProperties;
    private Context context;
    private int [][]table_matrix;
    private List<double[]> active_buttons;
    private int item_grid_counter =0;
    private int[] recent_swaped_items;
    private float button_label_scale = 20;
    private double default_height;

    private float scale;
    public ButtonGrid(final int row_count, final int col_count, final List<ButtonProperties> buttonProperties, RelativeLayout layout, final Context context) {
        this.row_count = row_count;
        this.col_count = col_count;
        this.layout = layout;
        this.buttonProperties = buttonProperties;
        this.context = context;
        active_buttons = new ArrayList<>();
        table_matrix = new int[row_count][col_count];
        setLayout(layout, new IButtonGrid() {
            @Override
            public void onSizeChange(double width, double height) {
                Collections.sort(buttonProperties,new ButtonPropertyComparator());
                x_quotient = computeAxisQuotient(width,col_count);
                y_quotient = computeAxisQuotient(height,row_count);
                Log.d(TAG, "onSizeChange: y & x quotient " + x_quotient + "-" + y_quotient );
                scale = context.getResources().getDisplayMetrics().density;
                generateGrid();
            }
        });
    }

    public void clearGrid(){
        layout.removeAllViews();
    }

    public void updateGrid(List<ButtonProperties> buttonProperties){
        this.buttonProperties = buttonProperties;
        clearGrid();
        item_grid_counter = 0;
        active_buttons = new ArrayList<>();
        height = default_height;
        generateGrid();

    }




    public void printMatrix(){
        String r = "\n";
        for(int i =0; i < row_count; i++){
            for(int x =0; x < col_count; x++){
                r += " " + table_matrix[i][x];
            }
            r +="\n";
        }
        Log.d(TAG, "printMatrix: " + r);
    }




    public boolean isMatrixIndexOccupied(int r, int c){
        return table_matrix[r][c] == 1;
    }

    public int[] getLastBlankBlock(){
        for(int i = 0; i < row_count; i++){
            for(int x =0; x< col_count; x++){
                if(table_matrix[i][x] == 0){
                    return new int[] {i,x};
                }
            }
        }
        return null;
    }

    public void printCheck(){
        Log.d(TAG, "printCheck: ===================================");
        for(double[] a : active_buttons){
            Log.d(TAG, "printCheck: " + a[0] +"\t" + a[1] + "\t" +a[2] +"\t" + a[3]);
        }
        Log.d(TAG, "printCheck: ===================================");
    }

    public void generateGrid(){
        double x_margin = 0,y_margin = 0,
            x_counter =0, y_counter =0;

        Log.d(TAG, "generateGrid: button count " + buttonProperties.size());
        for(int i =0; i < buttonProperties.size(); i++){
            /**
             * it could be "==" but the x_axis may
             * surpass the value of width,I haven't tested
             * it but hope its not.
             *
             * update: I've tested using the "==" it doesn't always work
             * */
            if(x_margin >= width ){
                y_margin += y_quotient;
                x_margin = 0;
            }


            /**
             * generate button here
             * */
            ButtonProperties btn = buttonProperties.get(i);
            double result = 0;
            if(i > 0){
                do{
                    double []ai = new double[]{x_margin, y_margin,x_margin + x_quotient * btn.width_ratio,y_margin + y_quotient * btn.height_ratio};
                    result = isOccupied(ai);
                    if(result != 0){
                        x_margin += result;
                        if(x_margin >= width){
                            y_margin += y_quotient;
                            x_margin = 0;
                        }
                        Log.d(TAG, "generateGrid: isOccupied " + result);
                    }else{
                        Log.d(TAG, "generateGrid: isOccupied " + result);
                        printCheck();
                        Log.d(TAG, "printCheck: ------");
                        Log.d(TAG, "printCheck: " + ai[0] +"\t" + ai[1] + "\t" +ai[2] +"\t" + ai[3]);
                        Log.d(TAG, "printCheck: ------");
                        break;
                    }
                }while (true);
            }




            boolean has_drop = false;
            x_margin += result;
            if(x_margin + btn.width_ratio * x_quotient > width){
                y_margin += y_quotient;
                x_margin = 0;
                has_drop = true;
            }
            double compute_height = y_margin + btn.height_ratio * y_quotient;
            if(compute_height > height){
                Log.d(TAG, "generateGrid: reached the limit");
                ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                layoutParams.height = (int)compute_height;
            }
            Log.d(TAG, "generateGrid: width " + width);
            layout.addView(generateButton(x_margin,y_margin,btn));
            if(!has_drop){
                x_margin+= btn.width_ratio *  x_quotient;
            }
            Log.d(TAG, "generateGrid: "+ btn.label + " x & y margins " + x_margin + " " + y_margin);
            Log.d(TAG, "generateGrid:"+ btn.label + " w & h pixels " + width + " " + height);

        }
    }


   /* boolean checkBlocks(){
        ArrayList<View> layoutButtons = layout.getTouchables();
        for(View v : layoutButtons){

        }
        return false;
    }
*/

    public Button generateButton(final double x_margin, final double y_margin, final ButtonProperties properties){
        final Button btn = new Button(context);
        btn.setText(properties.label);
        setButtonBackground(
                btn,
                properties.color,
                properties.image
        );
        Log.d(TAG, "generateButton: color " + properties.color);

        final double width = x_quotient * properties.width_ratio;
        final double height = y_quotient * properties.height_ratio;

        btn.setWidth((int)width);
        btn.setHeight((int)height);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int)width,
                (int)height
        );

        btn.setTextSize((int)width / button_label_scale);

        params.setMargins((int)x_margin,(int)y_margin,0,0);

        btn.setLayoutParams(params);


        btn.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                int action = dragEvent.getAction();
                switch (action){
                    case DragEvent.ACTION_DROP :
                        int drag_position = Integer.parseInt(dragEvent.getClipData().getItemAt(0).getText().toString());
                        int drop_position = getViewPosition(view);
                        Log.d(TAG, "onDrag: DROP " + drag_position + " " + drop_position);
                        Collections.swap(buttonProperties,drag_position,drop_position);
                        recent_swaped_items = new int [] {
                                drop_position,
                                drag_position
                        };
                        for(ButtonProperties bp : buttonProperties){
                            Log.d(TAG, "onDrag: " + bp.label);
                        }
                        clearGrid();
                        updateGrid(buttonProperties);
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED :
                        Log.d(TAG, "onDrag: ENTERED");
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION :
                        Log.d(TAG, "onDrag: LOCATION");
                        break;
                    case DragEvent.ACTION_DRAG_EXITED :
                        Log.d(TAG, "onDrag: EXITED");
                        break;
                    case DragEvent.ACTION_DRAG_ENDED :
                        btn.getBackground().setAlpha(255);
                        Log.d(TAG, "onDrag: ENDED");
                        break;
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.d(TAG, "onDrag: STARTED");
                        break;

                }
//                Log.d(TAG, "onDrag: called: " );
                return true;
            }
        });




        btn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent me) {


                int x = (int)me.getRawX();
                int y = (int)me.getRawY();
//                Button btn = (Button) v;
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(btn);

                ClipData.Item item = null;

                item = new ClipData.Item(getViewPosition(v) + "");


                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData data = new ClipData(
                        btn.toString(),
                        mimeTypes,
                        item
                );


                if (me.getAction() == MotionEvent.ACTION_MOVE  ){
                    v.getBackground().setAlpha(150);
                    v.startDrag(data,shadowBuilder,null,0);
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)width, (int)height);
//                    //set the margins. Not sure why but multiplying the height by 1.5 seems to keep my finger centered on the button while it's moving
//                    params.setMargins(x - v.getWidth()/2, (int)(y - v.getHeight()*1.5),0,0);
//                    v.setLayoutParams(params);
                }
                if(me.getAction() == MotionEvent.ACTION_UP){

                    if(isViewInBounds(btn,x , y)){
//                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                                (int)width,
//                                (int) height
//                        );
//
//                        params.setMargins((int)x_margin,(int)y_margin,0,0);
//                        v.setLayoutParams(params);


                    }
                    else if(isViewInBounds(v, x, y)){
                        Log.d(TAG, "onTouch ViewA");
                        //Here goes code to execute on onTouch ViewA
                    }

                }

                return true;
            }
        });

        Log.d(TAG, "generateButton: label: "+ properties.label + "\txm: " + x_margin + "\tym: " + y_margin + "\t pw: " + properties.width_ratio + "\tph: " + properties.height_ratio);
        active_buttons.add(new double[]{x_margin, y_margin, x_margin + properties.width_ratio * x_quotient, y_margin + properties.height_ratio * y_quotient});
        return btn;

    }

    int getViewPosition(View v){
        int count = 0;
        ArrayList<View> layoutButtons = layout.getTouchables();
        for(View b: layoutButtons){
            if( b instanceof Button) {
                if(b == v){
                    return count;
                }
                count++;
            }
        }
        return -1;
    }


    public double isOccupied(double[] data){
        for(double[] d : active_buttons){
//            boolean arg1 = ((data[0] + (data[2] * x_quotient)) <= (d[0] + (d[2] * x_quotient))
//                    &&
//                    (data[0] + x_quotient) >= (d[0] + x_quotient))
//                    &&
//                    ((data[1] + (data[3] * y_quotient)) <= (d[1] + (d[3] * y_quotient))
//                            &&
//                            (data[1] + y_quotient) >= (d[1] + y_quotient));



            boolean arg1 =  d[0] + x_quotient <= data[0] + x_quotient
                    &&
                    d[0] + d[2] * x_quotient >= data[0] + data[2] * x_quotient
                    &&
                    d[1] + y_quotient <= data[1] + y_quotient
                    &&
                    d[1] + d[3] * y_quotient >= data[1] + data[3] * y_quotient;

            boolean arg2 =
                    d[0] < data[2]
                    &&
                    d[2] > data[0]
                    &&
                    d[1] < data[3]
                    &&
                    d[3] > data[1];

            if(
                    arg2
            ){

                //data is occupied
                Log.d(TAG, "isOccupied: occupied");
                return x_quotient;
            }
        }
        return 0;
    }

    public void setButtonBackground(Button btn, final String color, final String image){

        Log.e(TAG, "setButtonBackground: before [" + color + "] ");
        if(!image.isEmpty()){
            Bitmap bitmap = getBitmapByPath(image);
            if(bitmap != null){
                btn.setBackground(new BitmapDrawable(context.getResources(),bitmap));
                return;
            }
        }

        try {
            Drawable drawable = btn.getBackground();
            GradientDrawable shape = new GradientDrawable();
            shape.setStroke(2, Color.parseColor("#000000"));
            shape.setColor(Color.parseColor(color));
            btn.setBackground(shape);
        }
        catch (Exception ex){
            Log.e(TAG, "setButtonBackground: after [" + color + "] " + ex);
        }
    }



    /**
     * prerequisites in manifest file
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     * */
    public Bitmap getBitmapByPath(String path){
        File img_file = new File(path);
        if(img_file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(img_file.getAbsolutePath());
            return bitmap;
        }
        return null;
    }

    public double computeAxisQuotient(double size, int count){
        return size / count;
    }

    public float getDPinPixel(float scale, float dps){
        return (int) (dps * scale + 0.5f);
    }

    public void setLayout(final RelativeLayout layout, final IButtonGrid callback){
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                } else {
                    layout.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                }

                width  = layout.getMeasuredWidth();
                height = layout.getMeasuredHeight();

                if(width != 0 && height != 0){
                    callback.onSizeChange(width, height);
                }


            }
        });
    }


    public class ButtonPropertyComparator implements Comparator<ButtonProperties> {

        @Override
        public int compare(ButtonProperties a, ButtonProperties b) {
            return a.position > b.position ? a.position : b.position;
        }
    }

    /**
     *this is base on
     * @link https://stackoverflow.com/questions/12980156/detect-touch-event-on-a-view-when-dragged-over-from-other-view
     *
     * */
    Rect outRect = new Rect();
    int[] location = new int[2];

    private boolean isViewInBounds(View view, int x, int y){
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }
}
