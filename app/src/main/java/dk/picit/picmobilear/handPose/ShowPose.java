package dk.picit.picmobilear.handPose;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.augumenta.agapi.HandPose;
import com.augumenta.agapi.HandPoseEvent;
import com.augumenta.agapi.HandPoseListener;
import com.augumenta.agapi.Poses;

import dk.picit.picmobilear.R;

public class ShowPose implements HandPoseListener {
    private static final String TAG = ShowPose.class.getSimpleName();

    private static final SparseArray<Integer> POSE_CURSORS = new SparseArray<>();
    static {
        POSE_CURSORS.put(Poses.P001, R.drawable.p001);
    }
    private View view;
    private FrameLayout frameLayout;

    public ShowPose(View view, FragmentActivity activity){
        this.view = view;
        frameLayout = (FrameLayout) activity.findViewById(android.R.id.content);
    }

    // cache created ImageViews to SparceArray using pose event id as a key
    private SparseArray<ImageView> poseImageArray = new SparseArray<ImageView>();


    @Override
    public void onDetected(final HandPoseEvent handPoseEvent, final boolean newDetect) {

        view.post(new Runnable() {
            @Override
            public void run() {
                if(newDetect){
                    ImageView image = new ImageView(view.getContext());
                    image.setImageResource(POSE_CURSORS.get(handPoseEvent.handpose.pose()));
                    if(handPoseEvent.handpose.handside() == HandPose.HandSide.RIGHT){
                        image.setRotationY(180);
                    }
                    int w = (int) (handPoseEvent.rect.width() * frameLayout.getWidth());
                    int h = (int) (handPoseEvent.rect.height() * frameLayout.getHeight());

                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
                    lp.leftMargin = (int) (handPoseEvent.rect.centerX() * frameLayout.getWidth() - (w/2));
                    lp.topMargin = (int) (handPoseEvent.rect.centerY() * frameLayout.getHeight() - (h/2));
                    image.setLayoutParams(lp);

                    // cache image with poses id, so it can be referenced later
                    poseImageArray.put(handPoseEvent.id, image);

                    frameLayout.addView(image);
                } else{
                    ImageView image = poseImageArray.get(handPoseEvent.id);
                    if(image!=null){

                        int w = (int) (handPoseEvent.rect.width() * frameLayout.getWidth());
                        int h = (int) (handPoseEvent.rect.height() * frameLayout.getHeight());

                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
                        lp.leftMargin = (int) (handPoseEvent.rect.centerX() * frameLayout.getWidth() - (w/2));
                        lp.topMargin = (int) (handPoseEvent.rect.centerY() * frameLayout.getHeight() - (h/2));
                        image.setLayoutParams(lp);
                    }
                }
            }
        });
    }

    @Override
    public void onLost(final HandPoseEvent handPoseEvent) {

        view.post(new Runnable() {
            @Override
            public void run() {
                ImageView image = poseImageArray.get(handPoseEvent.id);
                if(image != null){
                    frameLayout.removeView(image);
                }
            }
        });
    }

    @Override
    public void onMotion(HandPoseEvent handPoseEvent) {

    }
}
