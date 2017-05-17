package com.bapm.bzys.newBzys_store.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.bapm.bzys.newBzys.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListAdapter;

/**
 * @author Zaric
 * 
 */
public class ZrcListView extends ZrcAbsListView {
	static final int NO_POSITION = -1;
	private static final float MAX_SCROLL_FACTOR = 0.33f;

	public class FixedViewInfo {
		public View view;
		public Object data;
		public boolean isSelectable;
	}

	private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<FixedViewInfo>();
	private ArrayList<FixedViewInfo> mFooterViewInfos = new ArrayList<FixedViewInfo>();
	Drawable mDivider;
	int mDividerHeight;
	private boolean mIsCacheColorOpaque;
	private boolean mDividerIsOpaque;
	private boolean mHeaderDividersEnabled;
	private boolean mFooterDividersEnabled;
	private boolean mAreAllItemsSelectable = true;
	private boolean mItemsCanFocus = false;
	private final Rect mTempRect = new Rect();
	private Paint mDividerPaint;
	private int mItemAnimForTopIn;
	private int mItemAnimForBottomIn;
	// SwipeListView控件參數
	private Boolean mIsHorizontal;
	public View mPreItemView;

	private View mCurrentItemView;

	private int itemPosition;

	private float mFirstX;

	private float mFirstY;

	private int mRightViewWidth;
	private Map<Integer,Integer> itemRightWidths = new HashMap<Integer, Integer>();

	// private boolean mIsInAnimation = false;
	private final int mDuration = 100;

	private final int mDurationStep = 10;

	public boolean mIsShown;

	private boolean mIsCanShow;

	public static boolean mIsStopIsCanShow = false;

	private TypedArray mTypedArray;

	public ZrcListView(Context context) {
		this(context, null);
	}

	public ZrcListView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.listViewStyle);
	}

	public ZrcListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ListView, defStyle, 0);
		final Drawable d = a.getDrawable(R.styleable.ListView_android_divider);
		if (d != null) {
			setDivider(d);
		}
		final int dividerHeight = a.getDimensionPixelSize(R.styleable.ListView_android_dividerHeight, 0);
		if (dividerHeight != 0) {
			setDividerHeight(dividerHeight);
		}
		mHeaderDividersEnabled = a.getBoolean(R.styleable.ListView_android_headerDividersEnabled, true);
		mFooterDividersEnabled = a.getBoolean(R.styleable.ListView_android_footerDividersEnabled, true);
		mItemAnimForTopIn = 0;
		mItemAnimForBottomIn = 0;
		a.recycle();

		mTypedArray = context.obtainStyledAttributes(attrs,R.styleable.swipelistviewstyle);

		// 获取自定义属性和默认值
		mRightViewWidth = (int) mTypedArray.getDimension(R.styleable.swipelistviewstyle_right_width, 200);
		mTypedArray.recycle();
	}

	/**
	 * return true, deliver to listView. return false, deliver to child. if
	 * move, return true
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		float lastX = ev.getX();
		float lastY = ev.getY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsHorizontal = null;
			System.out.println("onInterceptTouchEvent----->ACTION_DOWN");
			mFirstX = lastX;
			mFirstY = lastY;
			itemPosition = pointToPosition((int) mFirstX, (int) mFirstY);
			if (itemPosition == 0 && mIsStopIsCanShow) {
				mIsCanShow = false;
			} else {
				mIsCanShow = true;
			}
			if (itemPosition >= 0) {
				View currentItemView = getChildAt(itemPosition
						- getFirstVisiblePosition());
				mPreItemView = mCurrentItemView;
				System.out.println("mPreItemView==" + mPreItemView);
				mCurrentItemView = currentItemView;
			} else if (itemPosition < 0 && mIsShown) {
			}
			System.out.println("itemPosition==" + itemPosition + "--mIsShown==" + mIsShown);
			break;

		case MotionEvent.ACTION_MOVE:
			float dx = lastX - mFirstX;
			float dy = lastY - mFirstY;

			if (Math.abs(dx) >= 5 && Math.abs(dy) >= 5) {
				return true;
			}
			break;

		case MotionEvent.ACTION_UP:
			System.out.println("onInterceptTouchEvent----->ACTION_UP");
		case MotionEvent.ACTION_CANCEL:
			System.out.println("onInterceptTouchEvent----->ACTION_CANCEL");
			if (mIsShown && (mPreItemView != mCurrentItemView || isHitCurItemLeft(lastX))) {
				System.out.println("1---> hiddenRight");
				/**
				 * 情况一：
				 * 一个Item的右边布局已经显示，
				 * 这时候点击任意一个item, 那么那个右边布局显示的item隐藏其右边布局
				 */
				hiddenRight(mPreItemView);
			} else if (itemPosition < 0) {
				// Hid
				hiddenRight(mCurrentItemView);
			}
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	private boolean isHitCurItemLeft(float x) {
		int mRightViewWidth=0;
		if(itemRightWidths.containsKey(itemPosition)){
			mRightViewWidth = itemRightWidths.get(itemPosition);
		}
		return x < getWidth() - mRightViewWidth;
	}

	/**
	 * @param dx
	 * @param dy
	 * @return judge if can judge scroll direction
	 */
	private boolean judgeScrollDirection(float dx, float dy) {
		boolean canJudge = true;

		if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
			mIsHorizontal = true;
			System.out.println("mIsHorizontal---->" + mIsHorizontal);
		} else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
			mIsHorizontal = false;
			System.out.println("mIsHorizontal---->" + mIsHorizontal);
		} else {
			canJudge = false;
		}

		return canJudge;
	}

	/**
	 * return false, can't move any direction. return true, cant't move
	 * vertical, can move horizontal. return super.onTouchEvent(ev), can move
	 * both.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		float lastX = ev.getX();
		float lastY = ev.getY();

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			System.out.println("onTouchEvent============ACTION_DOWN mIsShown==" + mIsShown);
			if (mIsShown) {
				return true;
			} else {
				break;
			}
		case MotionEvent.ACTION_MOVE:
			float dx = lastX - mFirstX;
			float dy = lastY - mFirstY;
			// confirm is scroll direction
			if (mIsHorizontal == null) {
				if (!judgeScrollDirection(dx, dy)) {
					break;
				}
			}
			if (mIsHorizontal) {
				if (mIsShown && mPreItemView != mCurrentItemView) {
					System.out.println("2---> hiddenRight");
					/**
					 * 情况二：
					 * 一个Item的右边布局已经显示，
					 * 这时候左右滑动另外一个item,那个右边布局显示的item隐藏其右边布局
					 * 向左滑动只触发该情况，向右滑动还会触发情况五
					 */
					hiddenRight(mPreItemView);
					hiddenRight(mCurrentItemView);
				}
				if (mIsShown && mPreItemView == mCurrentItemView) {
					if(itemRightWidths.containsKey(itemPosition)){
						dx = dx -itemRightWidths.get(itemPosition);
					}else{
						dx = dx -mRightViewWidth;
					}
					// System.out.println("======dx " + dx);
				}

				// can't move beyond boundary
				if(itemRightWidths.containsKey(itemPosition)){
					mRightViewWidth = itemRightWidths.get(itemPosition);
				}
				if (dx < 0 && dx > -mRightViewWidth && mCurrentItemView != null && itemPosition >= 0 && mIsCanShow) {
					mCurrentItemView.scrollTo((int) (-dx), 0);
					clearPressedState();
				}
				return true;
			} else {
				if (mIsShown) {
					System.out.println("3---> hiddenRight");
					/**
					 * 情况三：
					 * 一个Item的右边布局已经显示，这时候上下滚动ListView,那么那个右边布局显示的item隐藏其右边布局
					 */
					hiddenRight(mPreItemView);
					if (itemPosition < 0) {
						hiddenRight(mCurrentItemView);
					}
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			System.out.println("onTouchEvent============ACTION_UP");
			// hiddenRight(mCurrentItemView);
		case MotionEvent.ACTION_CANCEL:
			System.out.println("onTouchEvent============ACTION_CANCEL");
			clearPressedState();
			if (mIsShown) {
				if (itemPosition >= 0) {
					System.out.println("4---> hiddenRight");
					/**
					 * 情况四： 一个Item的右边布局已经显示， 这时候左右滑动当前一个item,那个右边布局显示的item隐藏其右边布局
					 */
					hiddenRight(mPreItemView);
				} else {
					System.out.println("4_1---> hiddenRight");
					/**
					 * 情况四：一个Item的右边布局已经显示，这时候左右滑动当前一个item,那个右边布局显示的item隐藏其右边布局
					 */
					hiddenRight(mPreItemView);
					hiddenRight(mCurrentItemView);
				}
				return true;
			}
			if (mIsHorizontal != null && mIsHorizontal) {
				if(itemRightWidths.containsKey(itemPosition)){
					mRightViewWidth = itemRightWidths.get(itemPosition);
				}
				if (mFirstX - lastX > mRightViewWidth / 2 && mIsCanShow) {
					showRight(mCurrentItemView,itemPosition);
				} else {
					System.out.println("5---> hiddenRight");
					/**
					 * 情况五：向右滑动一个item,且滑动的距离超过了右边View的宽度的一半，隐藏之。
					 */
					hiddenRight(mPreItemView);
					hiddenRight(mCurrentItemView);
				}
				return true;
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void clearPressedState() {
		// System.out.println("=========clearPressedState");
		if (mCurrentItemView != null) {
			mCurrentItemView.setPressed(false);
			mCurrentItemView.setSelected(false);
		}
		if (mPreItemView != null) {
			mPreItemView.setPressed(false);
			mPreItemView.setSelected(false);
		}
		setPressed(false);
		setSelected(false);
		refreshDrawableState();
		// invalidate();
	}

	private void showRight(View view,int postion) {
		if (view != null && itemPosition >= 0) {
			Message msg = new MoveHandler().obtainMessage();
			msg.obj = view;
			msg.arg1 = view.getScrollX();
			if(itemRightWidths.containsKey(postion)){
				msg.arg2 = itemRightWidths.get(postion);
			}else{
				msg.arg2 = mRightViewWidth;
			}
			msg.sendToTarget();
			mIsShown = true;
		}
	}

	public void hiddenRight(View view) {
		if (view != null) {
			Message msg = new MoveHandler().obtainMessage();//
			msg.obj = view;
			msg.arg1 = view.getScrollX();
			msg.arg2 = 0;
			msg.sendToTarget();
			mIsShown = false;
		}
	}

	/**
	 * show or hide right layout animation
	 */
	@SuppressLint("HandlerLeak")
	class MoveHandler extends Handler {
		int stepX = 0;
		int fromX;
		int toX;
		View view;
		private boolean mIsInAnimation = false;
		private void animatioOver() {
			mIsInAnimation = false;
			stepX = 0;
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (stepX == 0) {
				if (mIsInAnimation) {
					return;
				}
				mIsInAnimation = true;
				view = (View) msg.obj;
				fromX = msg.arg1;
				toX = msg.arg2;
				stepX = (int) ((toX - fromX) * mDurationStep * 1.0 / mDuration);
				if (stepX < 0 && stepX > -1) {
					stepX = -1;
				} else if (stepX > 0 && stepX < 1) {
					stepX = 1;
				}
				if (Math.abs(toX - fromX) < 10) {
					view.scrollTo(toX, 0);
					animatioOver();
					clearPressedState();
					return;
				}
			} else {
				clearPressedState();
			}

			fromX += stepX;
			boolean isLastStep = (stepX > 0 && fromX > toX) || (stepX < 0 && fromX < toX);
			if (isLastStep) {
				fromX = toX;
			}
			view.scrollTo(fromX, 0);
			invalidate();

			if (!isLastStep) {
				this.sendEmptyMessageDelayed(10, mDurationStep);
			} else {
				animatioOver();
			}
			clearPressedState();
		}
	}

//	public int getRightViewWidth() {
//		return mRightViewWidth;
//	}
//
//	public void setRightViewWidth(int mRightViewWidth) {
//		this.mRightViewWidth = mRightViewWidth;
//	}

	
	public int getMaxScrollAmount() {
		return (int) (MAX_SCROLL_FACTOR * (getBottom() - getTop()));
	}
	
	public Map<Integer, Integer> getItemRightWidths() {
		return itemRightWidths;
	}
	public int getRightViewWidth(int position){
		if(this.itemRightWidths.containsKey(position)){
			return this.itemRightWidths.get(position);
		}else{
			return mRightViewWidth;
		}
	}
	public void setItemRightWidths(int pistion,int width) {
		if(this.itemRightWidths==null){
			this.itemRightWidths = new HashMap<Integer, Integer>();
		}
		this.itemRightWidths.put(pistion,width);
	}
	public void removeItemRightWidth(){
		this.itemRightWidths.clear();
	}

	private void adjustViewsUp() {
		final int childCount = getChildCount();
		int delta;
		if (childCount > 0) {
			View child;
			child = getChildAt(0);
			delta = child.getTop() - mListPadding.top - mFirstTopOffset;
			if (mFirstPosition != 0) {
				delta -= mDividerHeight;
			}
			if (delta < 0) {
				delta = 0;
			}
			if (delta != 0) {
				offsetChildrenTopAndBottom(-delta);
			}
		}
	}

	public void addHeaderView(View v, Object data, boolean isSelectable) {
		final FixedViewInfo info = new FixedViewInfo();
		info.view = v;
		info.data = data;
		info.isSelectable = isSelectable;
		mHeaderViewInfos.add(info);
		if (mAdapter != null) {
			if (!(mAdapter instanceof HeaderViewListAdapter)) {
				mAdapter = new HeaderViewListAdapter(mHeaderViewInfos,mFooterViewInfos, mAdapter);
			}
			if (mDataSetObserver != null) {
				mDataSetObserver.onChanged();
			}
		}
	}

	public void addHeaderView(View v) {
		addHeaderView(v, null, true);
	}
	@Override
	public int getHeaderViewsCount() {
		return mHeaderViewInfos.size();
	}
	public boolean removeHeaderView(View v) {
		if (mHeaderViewInfos.size() > 0) {
			boolean result = false;
			if (mAdapter != null&& ((HeaderViewListAdapter) mAdapter).removeHeader(v)) {
				if (mDataSetObserver != null) {
					mDataSetObserver.onChanged();
				}
				result = true;
			}
			removeFixedViewInfo(v, mHeaderViewInfos);
			return result;
		}
		return false;
	}
	private void removeFixedViewInfo(View v, ArrayList<FixedViewInfo> where) {
		int len = where.size();
		for (int i = 0; i < len; ++i) {
			FixedViewInfo info = where.get(i);
			if (info.view == v) {
				where.remove(i);
				break;
			}
		}
	}

	public void addFooterView(View v, Object data, boolean isSelectable) {
		final FixedViewInfo info = new FixedViewInfo();
		info.view = v;
		info.data = data;
		info.isSelectable = isSelectable;
		mFooterViewInfos.add(info);

		// Wrap the adapter if it wasn't already wrapped.
		if (mAdapter != null) {
			if (!(mAdapter instanceof HeaderViewListAdapter)) {
				mAdapter = new HeaderViewListAdapter(mHeaderViewInfos,
						mFooterViewInfos, mAdapter);
			}
			if (mDataSetObserver != null) {
				mDataSetObserver.onChanged();
			}
		}
	}

	public void addFooterView(View v) {
		addFooterView(v, null, true);
	}
	@Override
	public int getFooterViewsCount() {
		return mFooterViewInfos.size();
	}
	public boolean removeFooterView(View v) {
		if (mFooterViewInfos.size() > 0) {
			boolean result = false;
			if (mAdapter != null && ((HeaderViewListAdapter) mAdapter).removeFooter(v)) {
				if (mDataSetObserver != null) {
					mDataSetObserver.onChanged();
				}
				result = true;
			}
			removeFixedViewInfo(v, mFooterViewInfos);
			return result;
		}
		return false;
	}

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (mAdapter != null && mDataSetObserver != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
		resetList();
		mRecycler.clear();

		if (mHeaderViewInfos.size() > 0 || mFooterViewInfos.size() > 0) {
			mAdapter = new HeaderViewListAdapter(mHeaderViewInfos,mFooterViewInfos, adapter);
		} else {
			mAdapter = adapter;
		}
		super.setAdapter(adapter);

		if (mAdapter != null) {
			mAreAllItemsSelectable = mAdapter.areAllItemsEnabled();
			mOldItemCount = mItemCount;
			mItemCount = mAdapter.getCount();
			checkFocus();

			mDataSetObserver = new AdapterDataSetObserver();
			mAdapter.registerDataSetObserver(mDataSetObserver);

			mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());
		} else {
			mAreAllItemsSelectable = true;
			checkFocus();
		}

		requestLayout();
	}

	@Override
	void resetList() {
		clearRecycledState(mHeaderViewInfos);
		clearRecycledState(mFooterViewInfos);
		super.resetList();
		mLayoutMode = LAYOUT_NORMAL;
	}

	private void clearRecycledState(ArrayList<FixedViewInfo> infos) {
		if (infos != null) {
			final int count = infos.size();

			for (int i = 0; i < count; i++) {
				final View child = infos.get(i).view;
				final LayoutParams p = (LayoutParams) child.getLayoutParams();
				if (p != null) {
					p.recycledHeaderFooter = false;
				}
			}
		}
	}

	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rect,boolean immediate) {
		rect.offset(child.getLeft(), child.getTop());
		rect.offset(-child.getScrollX(), -child.getScrollY());

		final int height = getHeight();
		int listUnfadedTop = getScrollY();
		int listUnfadedBottom = listUnfadedTop + height;

		int childCount = getChildCount();
		int bottomOfBottomChild = getChildAt(childCount - 1).getBottom();

		int scrollYDelta = 0;

		if (rect.bottom > listUnfadedBottom && rect.top > listUnfadedTop) {
			// need to MOVE DOWN to get it in view: move down just enough so
			// that the entire rectangle is in view (or at least the first
			// screen size chunk).

			if (rect.height() > height) {
				// just enough to get screen size chunk on
				scrollYDelta += (rect.top - listUnfadedTop);
			} else {
				// get entire rect at bottom of screen
				scrollYDelta += (rect.bottom - listUnfadedBottom);
			}

			// make sure we aren't scrolling beyond the end of our children
			int distanceToBottom = bottomOfBottomChild - listUnfadedBottom;
			scrollYDelta = Math.min(scrollYDelta, distanceToBottom);
		} else if (rect.top < listUnfadedTop && rect.bottom < listUnfadedBottom) {
			// need to MOVE UP to get it in view: move up just enough so that
			// entire rectangle is in view (or at least the first screen
			// size chunk of it).

			if (rect.height() > height) {
				// screen size chunk
				scrollYDelta -= (listUnfadedBottom - rect.bottom);
			} else {
				// entire rect at top
				scrollYDelta -= (listUnfadedTop - rect.top);
			}

			// make sure we aren't scrolling any further than the top our
			// children
			int top = getChildAt(0).getTop();
			int deltaToTop = top - listUnfadedTop;
			scrollYDelta = Math.max(scrollYDelta, deltaToTop);
		}

		final boolean scroll = scrollYDelta != 0;
		if (scroll) {
			scrollListItemsBy(-scrollYDelta);
			positionSelector(INVALID_POSITION, child);
			invalidate();
		}
		return scroll;
	}

	@Override
	void fillGap(boolean down) {
		final int count = getChildCount();
		if (down) {

			final int startOffset = count > 0 ? getChildAt(count - 1).getBottom() + mDividerHeight : mFirstTop + mListPadding.top + mFirstTopOffset;
			fillDown(mFirstPosition + count, startOffset, true);
			// correctTooHigh(getChildCount());
		} else {
			int paddingBottom = 0;

			final int startOffset = count > 0 ? getChildAt(0).getTop()- mDividerHeight : getHeight() - paddingBottom - mLastBottomOffset;
			fillUp(mFirstPosition - 1, startOffset, true);
			// correctTooLow(getChildCount());
		}
	}

	private void fillDown(int pos, int nextTop, boolean isAnim) {
		int end = (getBottom() - getTop());
		while (nextTop < end && pos < mItemCount) {
			View child = makeAndAddView(pos, nextTop, true, mListPadding.left,false);
			nextTop = child.getBottom() + mDividerHeight;
			if (isAnim && mItemAnimForBottomIn != 0 && child.getVisibility() == View.VISIBLE) {
				child.startAnimation(AnimationUtils.loadAnimation(getContext(),mItemAnimForBottomIn));
			}
			pos++;
		}
	}

	private void fillUp(int pos, int nextBottom, boolean isAnim) {
		int end = 0;

		while (nextBottom > end && pos >= 0) {
			View child = makeAndAddView(pos, nextBottom, false,mListPadding.left, false);
			nextBottom = child.getTop() - mDividerHeight;
			if (isAnim && mItemAnimForTopIn != 0 && child.getVisibility() == View.VISIBLE) {
				child.startAnimation(AnimationUtils.loadAnimation(getContext(),mItemAnimForTopIn));
			}
			pos--;
		}
		mFirstPosition = pos + 1;
	}

	private void fillFromTop(int nextTop) {
		mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
		if (mFirstPosition < 0) {
			mFirstPosition = 0;
		}
		fillDown(mFirstPosition, nextTop, false);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Sets up mListPadding
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int childWidth = 0;
		int childHeight = 0;
		int childState = 0;

		mItemCount = mAdapter == null ? 0 : mAdapter.getCount();
		if (mItemCount > 0
				&& (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED)) {
			final View child = obtainView(0, mIsScrap);

			measureScrapChild(child, 0, widthMeasureSpec);

			childWidth = child.getMeasuredWidth();
			childHeight = child.getMeasuredHeight();
			childState = combineMeasuredStates(childState,
					child.getMeasuredState());

			if (recycleOnMeasure() && mRecycler.shouldRecycleViewType(((LayoutParams) child .getLayoutParams()).viewType)) {
				mRecycler.addScrapView(child, -1);
			}
		}

		if (widthMode == MeasureSpec.UNSPECIFIED) {
			widthSize = mListPadding.left + mListPadding.right + childWidth + getVerticalScrollbarWidth();
		} else {
			widthSize |= (childState & MEASURED_STATE_MASK);
		}

		if (heightMode == MeasureSpec.UNSPECIFIED) {
			heightSize = mListPadding.top + mListPadding.bottom + childHeight + mFirstTopOffset + mLastBottomOffset;
		}

		if (heightMode == MeasureSpec.AT_MOST) {
			heightSize = measureHeightOfChildren(widthMeasureSpec, 0, NO_POSITION, heightSize, -1);
		}

		setMeasuredDimension(widthSize, heightSize);
		mWidthMeasureSpec = widthMeasureSpec;
	}

	private void measureScrapChild(View child, int position,int widthMeasureSpec) {
		LayoutParams p = (LayoutParams) child.getLayoutParams();
		if (p == null) {
			p = (ZrcAbsListView.LayoutParams) generateDefaultLayoutParams();
			child.setLayoutParams(p);
		}
		p.viewType = mAdapter.getItemViewType(position);
		p.forceAdd = true;

		int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,mListPadding.left + mListPadding.right, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	protected boolean recycleOnMeasure() {
		return true;
	}

	final int measureHeightOfChildren(int widthMeasureSpec, int startPosition,
			int endPosition, final int maxHeight,
			int disallowPartialChildPosition) {
		final ListAdapter adapter = mAdapter;
		if (adapter == null) {
			return mListPadding.top + mListPadding.bottom + mFirstTopOffset + mLastBottomOffset;
		}

		// Include the padding of the list
		int returnedHeight = mListPadding.top + mListPadding.bottom
				+ mFirstTopOffset + mLastBottomOffset;
		final int dividerHeight = ((mDividerHeight > 0) && mDivider != null) ? mDividerHeight: 0;
		// The previous height value that was less than maxHeight and contained
		// no partial children
		int prevHeightWithoutPartialChild = 0;
		int i;
		View child;

		// mItemCount - 1 since endPosition parameter is inclusive
		endPosition = (endPosition == NO_POSITION) ? adapter.getCount() - 1
				: endPosition;
		final ZrcAbsListView.RecycleBin recycleBin = mRecycler;
		final boolean recyle = recycleOnMeasure();
		final boolean[] isScrap = mIsScrap;

		for (i = startPosition; i <= endPosition; ++i) {
			child = obtainView(i, isScrap);

			measureScrapChild(child, i, widthMeasureSpec);

			if (i > 0) {
				// Count the divider for all but one child
				returnedHeight += dividerHeight;
			}

			// Recycle the view before we possibly return from the method
			if (recyle
					&& recycleBin.shouldRecycleViewType(((LayoutParams) child
							.getLayoutParams()).viewType)) {
				recycleBin.addScrapView(child, -1);
			}

			returnedHeight += child.getMeasuredHeight();

			if (returnedHeight >= maxHeight) {
				// We went over, figure out which height to return. If
				// returnedHeight > maxHeight,
				// then the i'th position did not fit completely.
				return (disallowPartialChildPosition >= 0) // Disallowing is
															// enabled (> -1)
						&& (i > disallowPartialChildPosition) // We've past the
																// min pos
						&& (prevHeightWithoutPartialChild > 0) // We have a prev
																// height
						&& (returnedHeight != maxHeight) // i'th child did not
															// fit completely
				? prevHeightWithoutPartialChild : maxHeight;
			}

			if ((disallowPartialChildPosition >= 0)
					&& (i >= disallowPartialChildPosition)) {
				prevHeightWithoutPartialChild = returnedHeight;
			}
		}

		// At this point, we went through the range of children, and they each
		// completely fit, so return the returnedHeight
		return returnedHeight;
	}

	@Override
	int findMotionRow(int y) {
		int childCount = getChildCount();
		if (childCount > 0) {
			for (int i = 0; i < childCount; i++) {
				View v = getChildAt(i);
				if (y <= v.getBottom()) {
					return mFirstPosition + i;
				}
			}
		}
		return INVALID_POSITION;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void layoutChildren() {
		final boolean blockLayoutRequests = mBlockLayoutRequests;
		if (blockLayoutRequests) {
			return;
		}
		mBlockLayoutRequests = true;
		try {
			super.layoutChildren();
			invalidate();
			final int childrenTop = mListPadding.top + mFirstTopOffset;
			final int childrenBottom = getBottom() - getTop()
					- mListPadding.bottom - mLastBottomOffset;
			final int childCount = getChildCount();

			boolean dataChanged = mDataChanged;
			if (dataChanged) {
				handleDataChanged();
			}

			// Handle the empty set by removing all views that are visible
			// and calling it a day
			if (mAdapter != null && mItemCount != mAdapter.getCount()) {
				throw new IllegalStateException(
						"The content of the adapter has changed but "
								+ "ListView did not receive a notification. Make sure the content of "
								+ "your adapter is not modified from a background thread, but only from "
								+ "the UI thread. Make sure your adapter calls notifyDataSetChanged() "
								+ "when its content changes. [in ListView("
								+ getId() + ", " + getClass()
								+ ") with Adapter(" + mAdapter.getClass()
								+ ")]");
			}

			// Ensure the child containing focus, if any, has transient state.
			// If the list data hasn't changed, or if the adapter has stable
			// IDs, this will maintain focus.
			final View focusedChild = getFocusedChild();
			if (focusedChild != null) {
				focusedChild.setHasTransientState(true);
			}

			// Pull all children into the RecycleBin.
			// These views will be reused if possible
			final int firstPosition = mFirstPosition;
			final int firstTop = mFirstTop;
			final RecycleBin recycleBin = mRecycler;
			if (dataChanged) {
				for (int i = 0; i < childCount; i++) {
					recycleBin.addScrapView(getChildAt(i), firstPosition + i);
				}
			} else {
				recycleBin.fillActiveViews(childCount, firstPosition);
			}

			// Clear out old views
			detachAllViewsFromParent();
			recycleBin.removeSkippedScrap();
			switch (mLayoutMode) {
			case LAYOUT_FORCE_BOTTOM:
				fillUp(mItemCount - 1, childrenBottom, false);
				adjustViewsUp();
				break;
			case LAYOUT_FORCE_TOP:
				mFirstPosition = 0;
				fillFromTop(childrenTop);
				break;
			default:
				if (mItemCount == 0) {
					if (mTouchMode != TOUCH_MODE_SCROLL) {
						scrollToAdjustViewsUpOrDown();
					}
				} else if (firstPosition >= mItemCount) {
					mFirstPosition = mItemCount - 1;
					View child = makeAndAddView(mFirstPosition, 1, false,
							mListPadding.left, false);
					if (mItemAnimForTopIn != 0
							&& child.getVisibility() == View.VISIBLE) {
						child.startAnimation(AnimationUtils.loadAnimation(
								getContext(), mItemAnimForTopIn));
					}
					scrollToAdjustViewsUpOrDown();
				} else {
					fillDown(firstPosition, firstTop, false);
					if (mTouchMode != TOUCH_MODE_SCROLL) {
						scrollToAdjustViewsUpOrDown();
					}
				}
				break;
			}

			// Flush any cached views that did not get reused above
			recycleBin.scrapActiveViews();
			// If the user's finger is down, select the motion position.
			// Otherwise, clear selection.
			if (mTouchMode == TOUCH_MODE_TAP
					|| mTouchMode == TOUCH_MODE_DONE_WAITING) {
				final View child = getChildAt(mMotionPosition - mFirstPosition);
				if (child != null) {
					positionSelector(mMotionPosition, child);
				}
			} else {
				mSelectorRect.setEmpty();
			}

			mLayoutMode = LAYOUT_NORMAL;
			mDataChanged = false;
			if (mPositionScrollAfterLayout != null) {
				post(mPositionScrollAfterLayout);
				mPositionScrollAfterLayout = null;
			}
			updateScrollIndicators();

			invokeOnItemScrollListener();
		} finally {
			if (!blockLayoutRequests) {
				mBlockLayoutRequests = false;
			}
		}
	}

	/**
	 * Obtain the view and add it to our list of children. The view can be made
	 * fresh, converted from an unused view, or used as is if it was in the
	 * recycle bin.
	 * 
	 * @param position
	 *            Logical position in the list
	 * @param y
	 *            Top or bottom edge of the view to add
	 * @param flow
	 *            If flow is true, align top edge to y. If false, align bottom
	 *            edge to y.
	 * @param childrenLeft
	 *            Left edge where children should be positioned
	 * @param selected
	 *            Is this position selected?
	 * @return View that was added
	 */
	private View makeAndAddView(int position, int y, boolean flow,
			int childrenLeft, boolean selected) {
		View child;

		if (!mDataChanged) {
			// Try to use an existing view for this position
			child = mRecycler.getActiveView(position);
			if (child != null) {
				// Found it -- we're using an existing child
				// This just needs to be positioned
				setupChild(child, position, y, flow, childrenLeft, selected,
						true);

				return child;
			}
		}

		// Make a new view for this position, or convert an unused view if
		// possible
		child = obtainView(position, mIsScrap);

		// This needs to be positioned and measured
		setupChild(child, position, y, flow, childrenLeft, selected,
				mIsScrap[0]);

		return child;
	}

	/**
	 * Add a view as a child and make sure it is measured (if necessary) and
	 * positioned properly.
	 * 
	 * @param child
	 *            The view to add
	 * @param position
	 *            The position of this child
	 * @param y
	 *            The y position relative to which this view will be positioned
	 * @param flowDown
	 *            If true, align top edge to y. If false, align bottom edge to
	 *            y.
	 * @param childrenLeft
	 *            Left edge where children should be positioned
	 * @param selected
	 *            Is this position selected?
	 * @param recycled
	 *            Has this view been pulled from the recycle bin? If so it does
	 *            not need to be remeasured.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupChild(View child, int position, int y, boolean flowDown,
			int childrenLeft, boolean selected, boolean recycled) {
		final boolean isSelected = selected && shouldShowSelector();
		final boolean updateChildSelected = isSelected != child.isSelected();
		final int mode = mTouchMode;
		final boolean isPressed = mode > TOUCH_MODE_DOWN
				&& mode < TOUCH_MODE_SCROLL && mMotionPosition == position;
		final boolean updateChildPressed = isPressed != child.isPressed();
		final boolean needToMeasure = !recycled || updateChildSelected
				|| child.isLayoutRequested();

		// Respect layout params that are already in the view. Otherwise make
		// some up...
		// noinspection unchecked
		ZrcAbsListView.LayoutParams p = (ZrcAbsListView.LayoutParams) child
				.getLayoutParams();
		if (p == null) {
			p = (ZrcAbsListView.LayoutParams) generateDefaultLayoutParams();
		}
		p.viewType = mAdapter.getItemViewType(position);

		if ((recycled && !p.forceAdd)
				|| (p.recycledHeaderFooter && p.viewType == ZrcAdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER)) {
			attachViewToParent(child, flowDown ? -1 : 0, p);
		} else {
			p.forceAdd = false;
			if (p.viewType == ZrcAdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
				p.recycledHeaderFooter = true;
			}
			addViewInLayout(child, flowDown ? -1 : 0, p, true);
		}

		if (updateChildSelected) {
			child.setSelected(isSelected);
		}

		if (updateChildPressed) {
			child.setPressed(isPressed);
		}

		if (needToMeasure) {
			int childWidthSpec = ViewGroup.getChildMeasureSpec(
					mWidthMeasureSpec, mListPadding.left + mListPadding.right,
					p.width);
			int lpHeight = p.height;
			int childHeightSpec;
			if (lpHeight > 0) {
				childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
						MeasureSpec.EXACTLY);
			} else {
				childHeightSpec = MeasureSpec.makeMeasureSpec(0,
						MeasureSpec.UNSPECIFIED);
			}
			child.measure(childWidthSpec, childHeightSpec);
		} else {
			cleanupLayoutState(child);
		}

		final int w = child.getMeasuredWidth();
		final int h = child.getMeasuredHeight();
		final int childTop = flowDown ? y : y - h;

		if (needToMeasure) {
			final int childRight = childrenLeft + w;
			final int childBottom = childTop + h;
			child.layout(childrenLeft, childTop, childRight, childBottom);
		} else {
			child.offsetLeftAndRight(childrenLeft - child.getLeft());
			child.offsetTopAndBottom(childTop - child.getTop());
		}

		if (mCachingStarted && !child.isDrawingCacheEnabled()) {
			child.setDrawingCacheEnabled(true);
		}

		if (recycled
				&& (((ZrcAbsListView.LayoutParams) child.getLayoutParams()).scrappedFromPosition) != position) {
			if (APIUtil.isSupport(11)) {
				child.jumpDrawablesToCurrentState();
			}
		}
	}

	@Override
	protected boolean canAnimate() {
		return super.canAnimate() && mItemCount > 0;
	}

	/**
	 * Scroll the children by amount, adding a view at the end and removing
	 * views that fall off as necessary.
	 * 
	 * @param amount
	 *            The amount (positive or negative) to scroll.
	 */
	private void scrollListItemsBy(int amount) {
		offsetChildrenTopAndBottom(amount);

		final int listBottom = getHeight() - mListPadding.bottom
				- mLastBottomOffset;
		final int listTop = mListPadding.top + mFirstTopOffset;
		final ZrcAbsListView.RecycleBin recycleBin = mRecycler;

		if (amount < 0) {
			// shifted items up

			// may need to pan views into the bottom space
			int numChildren = getChildCount();
			View last = getChildAt(numChildren - 1);
			while (last.getBottom() < listBottom) {
				final int lastVisiblePosition = mFirstPosition + numChildren
						- 1;
				if (lastVisiblePosition < mItemCount - 1) {
					last = addViewBelow(last, lastVisiblePosition);
					numChildren++;
				} else {
					break;
				}
			}

			// may have brought in the last child of the list that is skinnier
			// than the fading edge, thereby leaving space at the end. need
			// to shift back
			if (last.getBottom() < listBottom) {
				// offsetChildrenTopAndBottom(listBottom - last.getBottom());
			}

			// top views may be panned off screen
			View first = getChildAt(0);
			while (first.getBottom() < listTop) {
				ZrcAbsListView.LayoutParams layoutParams = (LayoutParams) first
						.getLayoutParams();
				if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
					recycleBin.addScrapView(first, mFirstPosition);
				}
				detachViewFromParent(first);
				first = getChildAt(0);
				mFirstPosition++;
			}
		} else {
			// shifted items down
			View first = getChildAt(0);

			// may need to pan views into top
			while ((first.getTop() > listTop) && (mFirstPosition > 0)) {
				first = addViewAbove(first, mFirstPosition);
				mFirstPosition--;
			}

			// may have brought the very first child of the list in too far and
			// need to shift it back
			if (first.getTop() > listTop) {
				// offsetChildrenTopAndBottom(listTop - first.getTop());
			}

			int lastIndex = getChildCount() - 1;
			View last = getChildAt(lastIndex);

			// bottom view may be panned off screen
			while (last.getTop() > listBottom) {
				ZrcAbsListView.LayoutParams layoutParams = (LayoutParams) last
						.getLayoutParams();
				if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
					recycleBin.addScrapView(last, mFirstPosition + lastIndex);
				}
				detachViewFromParent(last);
				last = getChildAt(--lastIndex);
			}
		}
	}

	private View addViewAbove(View theView, int position) {
		int abovePosition = position - 1;
		View view = obtainView(abovePosition, mIsScrap);
		int edgeOfNewChild = theView.getTop() - mDividerHeight;
		setupChild(view, abovePosition, edgeOfNewChild, false,
				mListPadding.left, false, mIsScrap[0]);
		return view;
	}

	private View addViewBelow(View theView, int position) {
		int belowPosition = position + 1;
		View view = obtainView(belowPosition, mIsScrap);
		int edgeOfNewChild = theView.getBottom() + mDividerHeight;
		setupChild(view, belowPosition, edgeOfNewChild, true,
				mListPadding.left, false, mIsScrap[0]);
		return view;
	}

	/**
	 * Indicates that the views created by the ListAdapter can contain focusable
	 * items.
	 * 
	 * @param itemsCanFocus
	 *            true if items can get focus, false otherwise
	 */
	public void setItemsCanFocus(boolean itemsCanFocus) {
		mItemsCanFocus = itemsCanFocus;
		if (!itemsCanFocus) {
			setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		}
	}

	/**
	 * @return Whether the views created by the ListAdapter can contain
	 *         focusable items.
	 */
	public boolean getItemsCanFocus() {
		return mItemsCanFocus;
	}

	@Override
	public boolean isOpaque() {
		boolean retValue = (mCachingActive && mIsCacheColorOpaque && mDividerIsOpaque)
				|| super.isOpaque();
		if (retValue) {
			// only return true if the list items cover the entire area of the
			// view
			final int listTop = mListPadding != null ? mListPadding.top
					+ mFirstTopOffset : getPaddingTop();
			View first = getChildAt(0);
			if (first == null || first.getTop() > listTop) {
				return false;
			}
			final int listBottom = getHeight()
					- (mListPadding != null ? mListPadding.bottom
							+ mLastBottomOffset : getPaddingBottom());
			View last = getChildAt(getChildCount() - 1);
			if (last == null || last.getBottom() < listBottom) {
				return false;
			}
		}
		return retValue;
	}

	@Override
	public void setCacheColorHint(int color) {
		final boolean opaque = (color >>> 24) == 0xFF;
		mIsCacheColorOpaque = opaque;
		if (opaque) {
			if (mDividerPaint == null) {
				mDividerPaint = new Paint();
			}
			mDividerPaint.setColor(color);
		}
		super.setCacheColorHint(color);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (mCachingStarted) {
			mCachingActive = true;
		}
		// Draw the dividers
		final int dividerHeight = mDividerHeight;
		final boolean drawDividers = dividerHeight > 0 && mDivider != null;

		if (drawDividers) {
			// Only modify the top and bottom in the loop, we set the left and
			// right here
			final Rect bounds = mTempRect;
			bounds.left = getPaddingLeft();
			bounds.right = getRight() - getLeft() - getPaddingRight();

			final int mBottom = getBottom();
			final int mTop = getTop();
			final int mScrollY = getScrollY();
			final int count = getChildCount();
			final int headerCount = mHeaderViewInfos.size();
			final int itemCount = mItemCount;
			final int footerLimit = (itemCount - mFooterViewInfos.size());
			final boolean headerDividers = mHeaderDividersEnabled;
			final boolean footerDividers = mFooterDividersEnabled;
			final int first = mFirstPosition;
			final boolean areAllItemsSelectable = mAreAllItemsSelectable;
			final ListAdapter adapter = mAdapter;
			// If the list is opaque *and* the background is not, we want to
			// fill a rect where the dividers would be for non-selectable items
			// If the list is opaque and the background is also opaque, we don't
			// need to draw anything since the background will do it for us
			final boolean fillForMissingDividers = isOpaque()
					&& !super.isOpaque();

			if (fillForMissingDividers && mDividerPaint == null
					&& mIsCacheColorOpaque) {
				mDividerPaint = new Paint();
				mDividerPaint.setColor(getCacheColorHint());
			}
			final Paint paint = mDividerPaint;

			int effectivePaddingBottom = 0;

			final int listBottom = mBottom - mTop - effectivePaddingBottom
					+ mScrollY;
			int bottom = 0;

			if (getChildCount() > 0) {
				final int firstTop = getChildAt(0).getTop();
				if (firstTop > 0) {
					bounds.top = firstTop - dividerHeight;
					bounds.bottom = firstTop;
					drawDivider(canvas, bounds, 0);
				}
			}
			for (int i = 0; i < count; i++) {
				final int itemIndex = (first + i);
				final boolean isHeader = (itemIndex < headerCount);
				final boolean isFooter = (itemIndex >= footerLimit);
				if ((headerDividers || !isHeader)
						&& (footerDividers || !isFooter)) {
					final View child = getChildAt(i);
					bottom = child.getBottom();
					final boolean isLastItem = (i == (count - 1));

					if (drawDividers && (bottom < listBottom)) {
						final int nextIndex = (itemIndex + 1);
						// Draw dividers between enabled items, headers and/or
						// footers when enabled, and the end of the list.
						if (areAllItemsSelectable
								|| ((adapter.isEnabled(itemIndex)
										|| (headerDividers && isHeader) || (footerDividers && isFooter)) && (isLastItem
										|| adapter.isEnabled(nextIndex)
										|| (headerDividers && (nextIndex < headerCount)) || (footerDividers && (nextIndex >= footerLimit))))) {
							bounds.top = bottom;
							bounds.bottom = bottom + dividerHeight;
							drawDivider(canvas, bounds, i);
						} else if (fillForMissingDividers) {
							bounds.top = bottom;
							bounds.bottom = bottom + dividerHeight;
							canvas.drawRect(bounds, paint);
						}
					}
				}
			}
		}
		super.dispatchDraw(canvas);
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		boolean more = super.drawChild(canvas, child, drawingTime);
		if (mCachingActive) {
			mCachingActive = false;
		}
		return more;
	}

	void drawDivider(Canvas canvas, Rect bounds, int childIndex) {
		final Drawable divider = mDivider;
		divider.setBounds(bounds);
		divider.draw(canvas);
	}

	public Drawable getDivider() {
		return mDivider;
	}

	public void setDivider(Drawable divider) {
		if (divider != null) {
			mDividerHeight = divider.getIntrinsicHeight();
		} else {
			mDividerHeight = 0;
		}
		mDivider = divider;
		mDividerIsOpaque = divider == null || divider.getOpacity() == PixelFormat.OPAQUE;
		requestLayout();
		invalidate();
	}

	public int getDividerHeight() {
		return mDividerHeight;
	}

	public void setDividerHeight(int height) {
		mDividerHeight = height;
		requestLayout();
		invalidate();
	}

	public void setHeaderDividersEnabled(boolean headerDividersEnabled) {
		mHeaderDividersEnabled = headerDividersEnabled;
		invalidate();
	}

	public boolean areHeaderDividersEnabled() {
		return mHeaderDividersEnabled;
	}

	public void setFooterDividersEnabled(boolean footerDividersEnabled) {
		mFooterDividersEnabled = footerDividersEnabled;
		invalidate();
	}

	public boolean areFooterDividersEnabled() {
		return mFooterDividersEnabled;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

		final ListAdapter adapter = mAdapter;
		if (adapter != null && gainFocus && previouslyFocusedRect != null) {
			previouslyFocusedRect.offset(getScrollX(), getScrollY());

			// Don't cache the result of getChildCount or mFirstPosition here,
			// it could change in layoutChildren.
			if (adapter.getCount() < getChildCount() + mFirstPosition) {
				mLayoutMode = LAYOUT_NORMAL;
				layoutChildren();
			}

			// figure out which item should be selected based on previously
			// focused rect
			Rect otherRect = mTempRect;
			int minDistance = Integer.MAX_VALUE;
			final int childCount = getChildCount();
			final int firstPosition = mFirstPosition;

			for (int i = 0; i < childCount; i++) {
				// only consider selectable views
				if (!adapter.isEnabled(firstPosition + i)) {
					continue;
				}

				View other = getChildAt(i);
				other.getDrawingRect(otherRect);
				offsetDescendantRectToMyCoords(other, otherRect);
				int distance = getDistance(previouslyFocusedRect, otherRect,
						direction);

				if (distance < minDistance) {
					minDistance = distance;
				}
			}
		}

		requestLayout();
	}

	/**
	 * convert all views to header view
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		int count = getChildCount();
		if (count > 0) {
			for (int i = 0; i < count; ++i) {
				addHeaderView(getChildAt(i));
			}
			removeAllViews();
		}
	}

	/**
	 * 设置由顶部进入视图的列表项动画，当animId = 0时，为取消动画。
	 * 
	 * @param animId
	 */
	public void setItemAnimForTopIn(int animId) {
		mItemAnimForTopIn = animId;
	}

	/**
	 * 设置由底部进入视图的列表项动画，当animId = 0时，为取消动画。
	 * 
	 * @param animId
	 */
	public void setItemAnimForBottomIn(int animId) {
		mItemAnimForBottomIn = animId;
	}

	public void setSelection(int i) {
		mFirstPosition = i;
		mFirstTop = 0;
		layoutChildren();
	}

	public static interface OnStartListener {
		void onStart();
	}

	public static interface OnScrollStateListener {
		public static final int EDGE = 0;
		public static final int DOWN = 1;
		public static final int UP = 2;

		void onChange(int state);
	}

	public static interface OnScrollListener {
		public static int SCROLL_STATE_IDLE = 0;
		public static int SCROLL_STATE_TOUCH_SCROLL = 1;
		public static int SCROLL_STATE_FLING = 2;

		public void onScrollStateChanged(ZrcAbsListView view, int scrollState);

		public void onScroll(ZrcAbsListView view, int firstVisibleItem,
							 int visibleItemCount, int totalItemCount);
	}

	public interface OnItemClickListener {
		void onItemClick(ZrcListView parent, View view, int position, long id);
	}

	public interface OnItemLongClickListener {
		boolean onItemLongClick(ZrcListView parent, View view, int position,
								long id);
	}
}