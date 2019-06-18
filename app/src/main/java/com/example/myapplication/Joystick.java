package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import androidx.core.view.MotionEventCompat;

/*
joystick activity class
 */
public class Joystick extends AppCompatActivity
{
    /*
    disconnect the client from the sever when joystick close
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        ClientConnectServer.getInstance().stopClient();
    }

    /*
    when the joystick activity open in first time
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       setContentView(new MyView(this));
    }

    /*
    new inner class for the drawing
     */
    public class MyView extends View
    {
        // class members
        Paint paint1;
        Paint paint2;
        private float startWidth;
        private float endWidth;
        private float startHeight;
        private float endHeight;
        private RectF oval;
        private float xPos;
        private float yPos;
        private Boolean playMoving = false;
        private float radius = 100;

        /*
        constructor
         */
        public MyView(Context context)
        {
            super(context);
            this.paint1 = new Paint();
            this.paint2 = new Paint();
            this.xPos = 0;
            this.yPos = 0;
        }

        /*
        deals with staying the oval proportional to the screen
         */
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            this.startWidth = (float)getWidth()/8;
            this.endWidth = (float)getWidth()-((float)getWidth()/8);
            this.startHeight = (float)getHeight()/8;
            this.endHeight = getHeight()-((float)getHeight()/8);
            this.oval = new RectF(this.startWidth,this.startHeight , this.endWidth, this.endHeight);

            // get the new center x and y positions
            getMiddle();
        }

        /*
        updates the new x and y values of the center position
         */
        public void getMiddle() {
            this.xPos = (float)getWidth()/2;
            this.yPos = (float)getHeight()/2;
        }

        @Override
        /*
        deals with drawing the circle and the oval on the canvas
         */
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            paint1.setColor(Color.LTGRAY);
            paint2.setColor(Color.BLUE);
            canvas.drawOval(this.oval, paint1);
            canvas.drawCircle(xPos, yPos, this.radius, paint2);
        }

        /*
        send the commands to the server
         */
        void sendNormalizeVal(String ailron, String elevator){
            String ailStr= "set controls/flight/aileron " + ailron + "\r\n";
            String eleStr = "set controls/flight/elevator " + elevator + "\r\n";
            ClientConnectServer.getInstance().sendMessage(ailStr);
            ClientConnectServer.getInstance().sendMessage(eleStr);
        }

        /*
        checks if motion happend, and if  yes - act appropriate
         */
        public boolean onTouchEvent(MotionEvent event) {
            int action = MotionEventCompat.getActionMasked(event);
            switch (action) {
                // if the user just pressed the screen
                case MotionEvent.ACTION_DOWN: {
                    // checks if the input is inside the circle limits
                    if(isInside(event.getX(), event.getY())) {
                        this.playMoving = true;
                    }
                    break;
                }
                // if the user move the circle of the joystick on the screen
                case MotionEvent.ACTION_MOVE: {
                    if (!this.playMoving)
                        return true;
                    // makes sure that the user's input is inside the limits
                    if (checkLimits(event.getX(), event.getY())) {
                        this.xPos = event.getX();
                        this.yPos = event.getY();
                        invalidate();

                        sendNormalizeVal(Float.toString( xNormalization(this.xPos)),
                                Float.toString(yNormalization(this.yPos)));
                    }

                    break;
                }
                // user input's finished
                case MotionEvent.ACTION_UP :
                    this.playMoving = false;
                    getMiddle();
                    //call on draw
                    invalidate();
            }

            return true;
        }

        /*
        checks if the user touched inside the circle
         */
        Boolean isInside(float xVal, float yVal) {
            double distance = Math.sqrt((this.xPos-xVal)*(this.xPos-xVal) + (this.yPos-yVal)*(this.yPos-yVal));
            return (distance <= this.radius);
        }

        /*
        checks if the given x,y are inside the oval
         */
        Boolean checkLimits(float xVal, float yVal) {
            double x = Math.pow(xVal - this.oval.centerX(), 2) / Math.pow((this.oval.width() /2) ,2);
            double y = Math.pow(yVal - this.oval.centerY(), 2) / Math.pow((this.oval.height() /2) ,2);
            x+=y;
            if (x <= 1) {
               return true;
            }
            return false;
        }

        /*
        doing the normalization of the x value for the aileron
         */
        public float xNormalization(float x) {
            return (x-((this.startWidth+this.endWidth)/2))/((this.endWidth-this.startWidth)/2);
        }

        /*
        doing the normalization of the y value for the elevator
         */
        public float yNormalization(float y) {
            return (y-((this.startHeight+this.endHeight)/2))/((this.startHeight-this.endHeight)/2);
        }
    }
}