package com.huidf.slimming.dynamic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.input.InputManager;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huidf.slimming.R;
import com.huidf.slimming.context.PreferenceEntity;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.LayoutUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：ZhuTao
 * 创建时间：2019/1/21 : 15:55
 * 描述：动态输入框
 */
public class InputView extends LinearLayout implements AdapterView.OnItemClickListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Context context;
    /**
     * 当前表情页
     */
    private int current = 0;
    /**
     * 表情页的监听事件
     */
    private OnCorpusSelectedListener mListener;

    /**
     * 游标点集合
     */
    private ArrayList<ImageView> pointViews;
    /**
     * 表情集合
     */
    private List<List<ChatEmoji>> emojis;
    /**
     * 输入框
     */
    private EditText ET_InputView;
    private TextView tv_cdh_words;
    /**
     * 表情区域
     */
    private View view_face;
    /**
     * 显示表情页的viewpager
     */
    private ViewPager viewPager_face;
    /**
     * 游标显示布局
     */
    private LinearLayout layout_point;
    private RadioGroup rg_cdv_title_sel;
    private LinearLayout lin_select_picture_main;    //照片框

    LayoutUtil mLayoutUtil;
    InputMethodManager imm;    //软键盘的处理
    private ArrayList<View> pageViewLists;
    private List<FaceAdapter> faceAdapters;
    private int textSize;

    public InputView(Context context) {
        super(context);
        this.context = context;
    }

    public InputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public InputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void setOnCorpusSelectedListener(OnCorpusSelectedListener listener) {
        mListener = listener;
    }

    /**
     * 表情选择监听
     *
     * @author naibo-liao
     * @时间： 2013-1-15下午04:32:54
     */
    public interface OnCorpusSelectedListener {

        void onCorpusSelected(ChatEmoji emoji);

        void onCorpusDeleted();
    }

    InputMethodManager inputMethodManager;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        Init_View();
    }


    private int num = 2000;// 限制的最大字数
    private int selectionStart;
    private int selectionEnd;
    private CharSequence temp;

    /**
     * 为输入框添加输入监听，监听字数变化
     */
    private void addTextChangeLis() {
        ET_InputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_cdh_words.setText(s.length() + "/2000");
                selectionStart = ET_InputView.getSelectionStart();
                selectionEnd = ET_InputView.getSelectionEnd();
                if (temp.length() > num) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    ET_InputView.setText(s);
                    ET_InputView.setSelection(tempSelection);// 设置光标在最后
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_picture:
                // 隐藏表情选择框
                imm.hideSoftInputFromWindow(ET_InputView.getWindowToken(), 0); //强制隐藏键盘
                view_face.setVisibility(View.GONE);
                lin_select_picture_main.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_face:
                imm.hideSoftInputFromWindow(ET_InputView.getWindowToken(), 0); //强制隐藏键盘
                view_face.setVisibility(View.VISIBLE);
                lin_select_picture_main.setVisibility(View.GONE);

                if (emojis == null || !PreferenceEntity.isInitIcons) {
//                   Observable.create(new ObservableOnSubscribe<Integer>() {
//                        @Override
//                        public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                            emitter.onNext(1);
//                            emitter.onNext(2);
//                            emitter.onNext(3);
//                        }
//                    })
////                           .map(new Function<Integer, String>() {
////                                @Override
////                                public String apply(Integer integer) throws Exception {
////
////                                    return "map:" + integer;
////                                }
////                            })
//                           .flatMap(new Function<Integer, ObservableSource<String>>() {
//                               @Override
//                               public ObservableSource<String> apply(Integer integer) throws Exception {
//
////                                   return Observab.from;
//                                   return Observable.fromFuture(new Future<String>() {
//                                       @Override
//                                       public boolean cancel(boolean mayInterruptIfRunning) {
//                                           return false;
//                                       }
//
//                                       @Override
//                                       public boolean isCancelled() {
//                                           return false;
//                                       }
//
//                                       @Override
//                                       public boolean isDone() {
//                                           return false;
//                                       }
//
//                                       @Override
//                                       public String get() throws InterruptedException, ExecutionException {
//                                           return null;
//                                       }
//
//                                       @Override
//                                       public String get(long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
//                                           return null;
//                                       }
//                                   });
//                               }
//                           })
//                           .subscribe(new Consumer<String>() {
//                                @Override
//                                public void accept(String string) throws Exception {
//                                    LOGUtils.LOG("accept + map方法变化后，传过来的值" + string);
//                                }
//                            });
                    Observable observable = Observable.create(mSubscriber)
                            .subscribeOn(Schedulers.io())    //执行在io线程
                            .observeOn(AndroidSchedulers.mainThread());    //回调在主线程
                    observable.subscribe(observer);
                }
                break;

        }
    }

    ObservableOnSubscribe<String> mSubscriber = new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
            //执行异步操作
            if (!PreferenceEntity.isInitIcons) FaceConversionUtil.getInstace().getFileText(context);

            //回调给观察者
            emitter.onNext("init");
            emitter.onComplete();
            PreferenceEntity.isInitIcons = true;
        }
    };

    private Disposable mDisposable;
    Observer observer = new Observer() {
        @Override
        public void onSubscribe(Disposable d) {
            mDisposable = d;
        }

        @Override
        public void onNext(Object o) {
            emojis = FaceConversionUtil.getInstace().emojiLists;
        }

        @Override
        public void onError(Throwable e) {
            PreferenceEntity.isInitIcons = false;
        }

        @Override
        public void onComplete() {

            InitViewPagerLists();
            initViewPagerData();
            initPoint();
            if (!mDisposable.isDisposed()) mDisposable.dispose();  //取消订阅
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_cd_input:    //消息输入框
                if (view_face.getVisibility() == VISIBLE || lin_select_picture_main.getVisibility() == VISIBLE) {
                    rg_cdv_title_sel.clearCheck();
                    view_face.setVisibility(View.GONE);
                    lin_select_picture_main.setVisibility(View.GONE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);    //显示软键盘
                            }
                        }
                    }).start();
                }
                break;

        }
    }

    /**
     * 隐藏/显示 表情选择框
     */
    public boolean isHideFaceView() {
        // 隐藏表情选择框
        if (view_face.getVisibility() == View.VISIBLE) {
            view_face.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    /**
     * 初始化控件
     */
    private void Init_View() {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);//初始化软键盘处理的方法
        ET_InputView = findViewById(R.id.et_cd_input);    //输入框
        tv_cdh_words = findViewById(R.id.tv_cdh_words);
        layout_point = findViewById(R.id.iv_image);
        rg_cdv_title_sel = findViewById(R.id.rg_cdv_title_sel);
        lin_select_picture_main = findViewById(R.id.lin_select_picture_main);
        view_face = findViewById(R.id.ll_facechoose);
        viewPager_face = findViewById(R.id.vp_contains);    //显示表情的viewPager

        ET_InputView.setOnClickListener(this);
        rg_cdv_title_sel.setOnCheckedChangeListener(this);
        addTextChangeLis();

        mLayoutUtil = LayoutUtil.getInstance();
        rg_cdv_title_sel.setMinimumHeight(mLayoutUtil.getWidgetHeight(100));

        textSize = (int) ET_InputView.getTextSize();
        LOGUtils.LOG("字体大小：" + textSize);
    }

    /**
     * 初始化显示表情的viewpager
     */
    private void InitViewPagerLists() {
        pageViewLists = new ArrayList<>();

        // 左侧添加空页
        View nullView1 = new View(context);
        // 设置透明背景
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViewLists.add(nullView1);

        // 中间添加表情页
        faceAdapters = new ArrayList<>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(context);
            FaceAdapter adapter = new FaceAdapter(context, emojis.get(i));
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            pageViewLists.add(view);
        }
        // 右侧添加空页面
        View nullView2 = new View(context);
        // 设置透明背景
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViewLists.add(nullView2);
    }

    /**
     * 初始化游标
     */
    private void initPoint() {

        pointViews = new ArrayList<>();
        ImageView imageView;
        for (int i = 0; i < pageViewLists.size(); i++) {
            imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.d1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            layout_point.addView(imageView, layoutParams);
            if (i == 0 || i == pageViewLists.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.d2);
            }
            pointViews.add(imageView);

        }
    }

    /**
     * 填充数据
     */
    private void initViewPagerData() {
        viewPager_face.setAdapter(new ViewPagerAdapter(pageViewLists));
        viewPager_face.setCurrentItem(1);
        current = 0;
        viewPager_face.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                current = arg0 - 1;
                // 描绘分页点
                draw_Point(arg0);
                // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        viewPager_face.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
                        pointViews.get(1).setBackgroundResource(R.drawable.d2);
                    } else {
                        viewPager_face.setCurrentItem(arg0 - 1);// 倒数第二屏
                        pointViews.get(arg0 - 1).setBackgroundResource(R.drawable.d2);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    /**
     * 绘制游标背景
     */
    public void draw_Point(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setBackgroundResource(R.drawable.d2);
            } else {
                pointViews.get(i).setBackgroundResource(R.drawable.d1);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ChatEmoji emoji = (ChatEmoji) faceAdapters.get(current).getItem(arg2);
        if (emoji.getId() == R.drawable.face_del_icon) {
            int selection = ET_InputView.getSelectionStart();
            String text = ET_InputView.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    ET_InputView.getText().delete(start, end);
                    return;
                }
                ET_InputView.getText().delete(selection - 1, selection);
            }
        }
        if (!TextUtils.isEmpty(emoji.getCharacter())) {    //不为空
            if (mListener != null)
                mListener.onCorpusSelected(emoji);
            int selection = ET_InputView.getSelectionStart();
            SpannableString spannableString = FaceConversionUtil.getInstace().addFace(getContext(), emoji.getId(), emoji.getCharacter(), textSize);
//			et_cd_input.append(spannableString);	//在文本框末尾插入字符表情
            ET_InputView.getText().insert(selection, spannableString);    //在指定光标处插入表情

        }

    }
}
