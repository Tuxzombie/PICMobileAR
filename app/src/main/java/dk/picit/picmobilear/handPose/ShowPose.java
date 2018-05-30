package dk.picit.picmobilear.handPose;

import android.app.Activity;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.augumenta.agapi.HandPose;
import com.augumenta.agapi.HandPoseEvent;
import com.augumenta.agapi.HandPoseListener;
import com.augumenta.agapi.HandTransitionEvent;
import com.augumenta.agapi.HandTransitionListener;
import com.augumenta.agapi.Poses;

import dk.picit.picmobilear.R;

public class ShowPose implements HandPoseListener {
    private static final String TAG = ShowPose.class.getSimpleName();

    public static final float MARGIN_TOP = -0.2f;
    public static final float MARGIN_BOTTOM = -0.2f;
    public static final float MARGIN_LEFT = -0.2f;
    public static final float MARGIN_RIGHT = -0.2f;

    // RelativeMargin maps the camera view so that it's possible to interact with views near
    // the edges
    public final RelativeMargin margin =
            new RelativeMargin(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM);

    // maps poses to image of the pose
    private static final SparseArray<Integer> POSE_CURSORS = new SparseArray<>();

    static {
        POSE_CURSORS.put(Poses.P229, R.drawable.p001);
        POSE_CURSORS.put(Poses.P141, R.drawable.p032);
        POSE_CURSORS.put(Poses.P016, R.drawable.p016);
        POSE_CURSORS.put(Poses.P201, R.drawable.p201);
    }

    public int POSE_HOVER = Poses.P229;
    public int POSE_TOUCH = Poses.P141;
    private final int POSE_IMAGE_SIZE = 64;

    private FrameLayout frameLayout;
    private Activity activity;
    private int previousPose = -1;
    private int cursorX = 0;
    private int cursorY = 0;
    // offset to cursor position to compensate shift in position when pose changes
    private int cursorDeltaX = 0;
    private int cursorDeltaY = 0;


    public ShowPose(Activity activity) {
        // gets the layout of the activity
        this.activity = activity;
        frameLayout = (FrameLayout) activity.findViewById(R.id.frameLayout);
    }

    // cache created ImageViews to SparceArray using pose event id as a key
    private SparseArray<ImageView> poseImageArray = new SparseArray<ImageView>();

    /**
     * Move cursor view based on the pose event
     *
     * @param event pose event
     */
    private void moveCursor(HandPoseEvent event) {
        // get event absolute position on the screen
        // event.rect.centerX gets x position on the screen, 0 is the left side
        // 1 is the right side
        int x = (int) (margin.translateX(event.rect.centerX()) * frameLayout.getWidth());
        int y = (int) (margin.translateY(event.rect.centerY()) * frameLayout.getHeight());


        int pose = event.handpose.pose();

        // compensate for the shift in position between hover and touch pose
        if (pose == POSE_TOUCH) {
            if (previousPose == POSE_HOVER) {
                cursorDeltaX = cursorX - x;
                cursorDeltaY = cursorY - y;
            }
            x += cursorDeltaX;
            y += cursorDeltaY;
        }
        previousPose = pose;
        cursorX = x;
        cursorY = y;
    }

    /**
     * Update cursor views based on the event
     * Positions the cursor on the screen
     *
     * @param handPoseEvent pose event
     */
    private void updateCursor(HandPoseEvent handPoseEvent, ImageView image) {

        int x = (int) (margin.translateX(handPoseEvent.rect.centerX()) * frameLayout.getWidth());
        int y = (int) (margin.translateY(handPoseEvent.rect.centerY()) * frameLayout.getHeight());

        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(POSE_IMAGE_SIZE, POSE_IMAGE_SIZE);
        lp.leftMargin = x - (POSE_IMAGE_SIZE / 2);
        lp.topMargin = y - (POSE_IMAGE_SIZE / 2);

        image.setLayoutParams(lp);
    }


    /**
     * Called when a pose is detected
     *
     * @param handPoseEvent
     * @param newDetect     is it a new pose
     */
    @Override
    public void onDetected(final HandPoseEvent handPoseEvent, final boolean newDetect) {
        Log.d(TAG, "onDetected: " + handPoseEvent);
        frameLayout.post(new Runnable() {
            @Override
            public void run() {
                if (newDetect) {
                    ImageView image = new ImageView(activity);
                    image.setImageResource(POSE_CURSORS.get(handPoseEvent.handpose.pose()));
                    // rotate cursor if right hand is used
                    if (handPoseEvent.handpose.handside() == HandPose.HandSide.RIGHT) {
                        image.setRotationY(180);
                    }

                    updateCursor(handPoseEvent, image);

                    moveCursor(handPoseEvent);

                    // cache image with poses id, so it can be referenced later
                    poseImageArray.put(handPoseEvent.id, image);

                    frameLayout.addView(image);
                } else {
                    ImageView image = poseImageArray.get(handPoseEvent.id);
                    if (image != null) {

                        updateCursor(handPoseEvent, image);
                        moveCursor(handPoseEvent);
                    }
                }
            }
        });
    }

    /**
     * when the handpose is lost from detection
     *
     * @param handPoseEvent
     */
    @Override
    public void onLost(final HandPoseEvent handPoseEvent) {
        frameLayout.post(new Runnable() {
            @Override
            public void run() {
                ImageView image = poseImageArray.get(handPoseEvent.id);
                if (image != null) {
                    frameLayout.removeView(image);
                }
            }
        });
    }

    @Override
    public void onMotion(HandPoseEvent handPoseEvent) {

    }


    public HandTransitionListener getSelectTransitionListner() {
        return selectTransitionListner;
    }

    /**
     * Listner for transition from one hand pose to another
     */
    private HandTransitionListener selectTransitionListner = new HandTransitionListener() {
        @Override
        public void onTransition(HandTransitionEvent handTransitionEvent) {
            Log.d(TAG, "onTransition: " + handTransitionEvent);
            frameLayout.post(new Runnable() {
                @Override
                public void run() {
                    long now = SystemClock.uptimeMillis();
                    // press event at (cursorX,cursorY)
                    frameLayout.dispatchTouchEvent(MotionEvent.obtain(now, now,
                                                                      MotionEvent.ACTION_DOWN,
                                                                      cursorX, cursorY, 0));
                    // release event at (cursorX,cursorY)
                    frameLayout.dispatchTouchEvent(MotionEvent.obtain(now, now + 1,
                                                                      MotionEvent.ACTION_UP,
                                                                      cursorX, cursorY, 0));
                }
            });
        }
    };
}
