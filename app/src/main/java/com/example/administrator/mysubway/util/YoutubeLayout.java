package com.example.administrator.mysubway.util;

/**
 * Created by Administrator on 2017-10-25.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 1. onFinishInflate() : 뷰 초기화
 * 2. YoutubeLayout() - 생성자 : ViewDragHelper 초기화
 * 3. onMeasure() : 크기 지정(Header, Desc)
 * 4. onLayout() : 위치 지정, DragRange 지정
 * 5. onTouchEvent() :
 *
 */

public class YoutubeLayout extends ViewGroup {

    private final ViewDragHelper mDragHelper;

    private View mHeaderView;
    private View mDescView;

    private float mInitialMotionX;
    private float mInitialMotionY;

    private int mDragRange; // 전체 높이에서 mHeaderView 를 뺸 것으로 얼만큼 끌어당길 수 있는지 크기가 지정될 때 정해둔다
    private int mTop;       // mHeaderView 의 현재 y
    private float mDragOffset;


    public YoutubeLayout(Context context) {
        this(context, null);
    }

    public YoutubeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 뷰 초기화
     */
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onFinishInflate() {
//        mHeaderView = findViewById(R.id.viewHeader);
//        mDescView = findViewById(R.id.viewDesc);
        Log.e("순서확인", "onFinishInflate");
    }

    /**
     * DragHelperCallback 초기화
     */
    public YoutubeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        Log.e("순서확인", "생성자");
    }

    /**
     * DragHelperCallback
     */
    private class DragHelperCallback extends ViewDragHelper.Callback {

        // ViewGroup 의 어떤 ChildView 를 잡았는지 view 인자로 넘어옴. 퍼미션 필요 없음.
        // 내가 지정한 mHeaderView 와 비교해서 원하는 뷰를 잡았으면 true 를 리턴. 즉, 내가 원하는 뷰만 잡아서 움직일 수 있음.
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mHeaderView;
        }

        // 어떤 뷰든 뷰가 움직이면 호출됨. 위에서 뷰를 선택하면 움직이게 되는데, 이 때 여기가 호출
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            // 뷰를 움직이면 뷰의 y 좌표를 mTop 으로 설정해준다
            mTop = top;

            // 여기서 Header, Desc 의 크기와 투명도 조절
            mDragOffset = (float) top / mDragRange;
            mHeaderView.setPivotX(mHeaderView.getWidth());
            mHeaderView.setPivotY(mHeaderView.getHeight());
            mHeaderView.setScaleX(1 - mDragOffset / 2);
            mHeaderView.setScaleY(1 - mDragOffset / 2);

            mDescView.setAlpha(1 - mDragOffset);

            // onMeasure(), onLayout() 호출
            requestLayout();
        }

        // 뷰를 놓았을 때 어디로 돌아갈 것인지 결정
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top = getPaddingTop();
            if (yvel > 0 || (yvel == 0 && mDragOffset > 0.5f)) {
                top += mDragRange;
            }
            mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
        }

        // 드래가 할 뷰가 얼만큼 움직일 수 있는지 지정
        @Override
        public int getViewVerticalDragRange(View child) {
            return mDragRange;
        }

        //
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - mHeaderView.getHeight() - mHeaderView.getPaddingBottom();

            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }

    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * onTouch 가 일어나기 이전에 ViewGroup 이 터치이벤트 검사
     * 안드로이드는 여러 레이어로 구성되어 있고, 최하위로부터 최상위에 있는 위젯에게까지 이벤트를 전달해야 한다.
     * 먼저 부모가 인식을 하고 자신에게 속한 자식에게 이벤트를 전달할지 결정해야 한다. dispatchTouchEvent 는
     * View, Activity 에서 자식 뷰의 터치 이벤트를 호출하는 기능을 한다. 특이하게 ViewGroup 에서는 onInterceptTouchEvent 를
     * 호출함으로써 자식뷰에게 터치이벤트를 호출할지 여부를 알려주는 것이다. 이를 정의하면 특정 방식에서는 터치이벤트가 발생하지
     * 않게 할 수 있는 것이다.
     *
     * true : 이벤트를 가져온다는 의미 -> 현재 뷰그룹의 onTouchEvent 를 실행한다
     * false : 하위 View 로 이벤트를 전달한다는 의미
     *
     * true를 리턴하면 자식들로부터의 이벤트를 훔치고 이 viewGroup으로 onTouchEvent함수를 통해 이벤트들이 전달되도록 한다.
     * 현재 타겟은 ACTION_CANCEL 이벤트를 받게 될 것이고 더이상 어떠한 메시지들도 이 함수에 전달되지 않을 것이다.
     * 역주) false를 리턴하면 계속 이 함수로 이벤트를 받아서 검사하겠다는 의미.
     *
     * 즉, 현재 true 값을 리턴하도록 하는 것은 자식뷰로 이벤트를 전달하지 않고 현재 ViewGroup 의 onTouchEvent
     *
     * activity의 dispatchTouchEvent() 가 호출되면서 window 의 superDispatchTouchEvent(ev) 를 호출한다.
     * 이 superDispatchTouchEvent() 가 최상위에 있는 Dispatch 해주는 녀석이라고 봐도 무방할 듯 하다.
     * 아무튼 이 녀석으로부터 시작해서 child 의 dispatchTouchEvent(event) 를 호출하고, 그 child 의 child 의
     * dispatchTouchEvent(event)를 호출하고, 또 그 child의 child 의 child 의 dispatchTouchEvent(event)를 호출한다.
     * 이런 식으로 쭉 밑으로 내려 가다보면, View 의 dispatchTouchEvent(event) 로 가게 되고, 여기서 최종적으로 View 의
     * OnTouchEvent() 를 호출하게 된다.
     *
     * 적용점1. 이 뷰에는 스크롤뷰가 들어갈 것인데, 스크롤이 될 때는 false 를 리턴해서 자식으로 터치이벤트를 넘겨주지 않고
     * 스크롤이 끝난 경우에만 터치이벤트가 가능하게 해 준다
     * 적용점2. 뷰페이저 안에 뷰페이저가 있는 경우 자식 뷰페이저가 마지막에 갔을 때만 부모 뷰페이저가 작동하도록 해야 한다.
     *
     * 참고로 자식 뷰가 이벤트를 처리하지 않고 super 을 호출한 경우 부모가 직접 값을 받아서 호출해준다.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("순서확인1", "onInterceptTouchEvent");
        final int action = MotionEventCompat.getActionMasked(ev);

        if ((action != MotionEvent.ACTION_DOWN)) {
            mDragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }

        final float x = ev.getX();
        final float y = ev.getY();
        boolean interceptTap = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = x;
                mInitialMotionY = y;
                interceptTap = mDragHelper.isViewUnder(mHeaderView, (int) x, (int) y);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                final int slop = mDragHelper.getTouchSlop();
                if (ady > slop && adx > ady) {
                    mDragHelper.cancel();
                    Log.e("확인2", "false");
                    return false;
                }
            }
        }
        Log.e("확인2", (mDragHelper.shouldInterceptTouchEvent(ev) || interceptTap)+"");

        return false;
    }

    /**
     * 시작 지점
     * 1. 드래그 헬퍼에게 터치이벤트 넘겨주기 : processTouchEvent()
     * 2. ACTION_DOWN : 처음 눌린 위치 설정
     * 3. ACTION_UP : 움직인 거리 & 뷰와 현재위치 비교 -> smoothSlideTo
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 가장 중요한 부분으로 부모 뷰의 터치이벤트를 드래그 헬퍼가 받아서 처리하겠다
        mDragHelper.processTouchEvent(ev);

        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();

        // 넘겨준 뷰(Header 의 좌표는 (0, 0))가 x,y 좌표 아래에 있는지 확인
        boolean isHeaderViewUnder = mDragHelper.isViewUnder(mHeaderView, (int) x, (int) y);
        Log.e("isHeaderViewUnder", isHeaderViewUnder+"");
        switch (action & MotionEventCompat.ACTION_MASK) {
            // 처음 눌렸을 때 초기 좌표 설정(움직임을 처리하려면 ACTION_MOVE 가 필요함)
            case MotionEvent.ACTION_DOWN: {
                // 초기 좌표 설정
                mInitialMotionX = x;
                mInitialMotionY = y;
                break;
            }

            // 1.피타고라스로 움직인 거리 계산 2. 현재 좌표와 뷰의 위치값 비교 -> 3.어디로 이동할 것인지 결정(맨위, 맨 아래, 혹은 개발자 지정)
            case MotionEvent.ACTION_UP: {
                // 움직인 거리의 밑변과 높이
                final float dx = x - mInitialMotionX;
                final float dy = y - mInitialMotionY;
                // The minimum distance in pixels that the user must travel to initiate a drag
                final int slop = mDragHelper.getTouchSlop();
                // 움직인 거리가 피타고라스 정리에 의해 둔각 삼각형 && 내가 누르고 있는 좌표가 뷰의 왼쪽 맨위의 좌표보다 아래일 경우
                // 즉 최소한 움직여줘야 하는 거리를 움직였는지 확인
                if (dx * dx + dy * dy < slop * slop && isHeaderViewUnder) {
                    // mDragOffset 가 0이라는 말은 Header 가 맨 위에 있다는 말
                    if (mDragOffset == 0) {
                        smoothSlideTo(1f);
                        // Header 가 아래로 내려가 있다
                    } else {
                        smoothSlideTo(0f);
                    }
                }
                break;
            }
        }
        return isHeaderViewUnder && isViewHit(mHeaderView, (int) x, (int) y) || isViewHit(mDescView, (int) x, (int) y);
    }

    /**
     * smoothSlideViewTo(1, 2, 3)
     * 1 : 움직일 뷰
     * 2 : x 좌표 : view.getLeft()
     * 3 : y 좌표 : 맨위, 맨아래, 지정된 위치
     */
    boolean smoothSlideTo(float slideOffset) {
        // 뷰그룹의 top 패딩값
        final int topBound = getPaddingTop();
        // y 좌표
        int y = (int) (topBound + slideOffset * mDragRange);

        // slideOffset 이 1이면 맨 아래로 0이면 맨 위로 움직인다
        if (mDragHelper.smoothSlideViewTo(mHeaderView, mHeaderView.getLeft(), y)) {
            // 움직였다면 애니메이션이 끝나고 invalidate()
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }


    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    /**
     * 뷰 크기 책정
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("순서확인", "onMeasure");
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(
                resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    /**
     * 레이아웃 위치 설정
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("순서확인", "onLayout");
        mDragRange = getHeight() - mHeaderView.getHeight();

        mHeaderView.layout(
                0,
                mTop,
                r,
                mTop + mHeaderView.getMeasuredHeight());

        mDescView.layout(
                0,
                mTop + mHeaderView.getMeasuredHeight(),
                r,
                mTop + b);
    }
}