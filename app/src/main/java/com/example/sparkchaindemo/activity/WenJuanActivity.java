package com.example.sparkchaindemo.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WenJuanActivity extends BaseActivity {
    private ScrollView scrollView;
    private LinearLayout surveyContainer;
    private Button submitButton;
    private Button backButton; // 添加返回按钮
    private List<Question> questions = new ArrayList<>();
    private Map<Integer, Object> answers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_wen_juan);
        scrollView = findViewById(R.id.scrollView);
        surveyContainer = findViewById(R.id.surveyContainer);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton); // 初始化返回按钮
        initQuestions();
        setupSurveyUI();
    }
    private void initQuestions() {
        // 添加问题
        questions.add(new Question(1, "您的年龄范围是？", Question.TYPE_RADIO,
                new String[]{"18岁以下", "18-30岁", "31-45岁", "46-60岁", "60岁以上"}));

        questions.add(new Question(2, "您使用哪种方式进行日常沟通？（可多选）", Question.TYPE_CHECKBOX,
                new String[]{"手语", "文字交流", "唇语", "语音转文字工具", "其他"}));

        questions.add(new Question(3, "您是否使用助听器或人工耳蜗？", Question.TYPE_RADIO,
                new String[]{"是", "否"}));

        questions.add(new Question(4, "您希望本APP提供哪些功能？（可多选）", Question.TYPE_CHECKBOX,
                new String[]{"实时语音转文字", "震动提醒", "手势识别", "字幕视频", "紧急求助", "其他"}));

        questions.add(new Question(5, "您在日常生活中遇到的最大障碍是什么？", Question.TYPE_EDITTEXT));

        questions.add(new Question(6, "您对本APP的界面设计有什么建议？", Question.TYPE_EDITTEXT));
    }
    private void setupSurveyUI() {
        for (Question question : questions) {
            View questionView = createQuestionView(question);
            surveyContainer.addView(questionView);

            // 添加分隔线
            View divider = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 2);
            params.setMargins(0, 16, 0, 16);
            divider.setLayoutParams(params);
            divider.setBackgroundColor(getResources().getColor(R.color.font_grey));
            surveyContainer.addView(divider);
        }
    }
    private View createQuestionView(Question question) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, 8, 0, 8);

        // 添加问题标题
        TextView questionTitle = new TextView(this);
        questionTitle.setText(question.getTitle());
        questionTitle.setTextSize(18);
        questionTitle.setPadding(0, 0, 0, 12);
        container.addView(questionTitle);

        // 根据问题类型添加选项
        switch (question.getType()) {
            case Question.TYPE_RADIO:
                RadioGroup radioGroup = new RadioGroup(this);
                for (String option : question.getOptions()) {
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(option);
                    radioButton.setId(View.generateViewId());
                    radioGroup.addView(radioButton);

                    // 设置选项点击事件
                    radioButton.setOnClickListener(v -> {
                        answers.put(question.getId(), ((RadioButton) v).getText());
                    });
                }
                container.addView(radioGroup);
                break;

            case Question.TYPE_CHECKBOX:
                for (String option : question.getOptions()) {
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(option);
                    checkBox.setId(View.generateViewId());
                    container.addView(checkBox);

                    // 设置选项点击事件
                    checkBox.setOnClickListener(v -> {
                        List<String> selectedOptions = (List<String>) answers.get(question.getId());
                        if (selectedOptions == null) {
                            selectedOptions = new ArrayList<>();
                            answers.put(question.getId(), selectedOptions);
                        }

                        CheckBox cb = (CheckBox) v;
                        if (cb.isChecked()) {
                            selectedOptions.add(cb.getText().toString());
                        } else {
                            selectedOptions.remove(cb.getText().toString());
                        }
                    });
                }
                break;

            case Question.TYPE_EDITTEXT:
                EditText editText = new EditText(this);
                editText.setHint("请输入您的回答...");
                editText.setMinLines(3);
                editText.setPadding(12, 12, 12, 12);
                editText.setBackgroundResource(R.drawable.edittext_border);
                container.addView(editText);

                // 设置文本变化监听器
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        answers.put(question.getId(), s.toString());
                    }
                });
                break;
        }

        return container;
    }
    private boolean validateAnswers() {
        // 检查是否回答了所有问题
        for (Question question : questions) {
            if (!answers.containsKey(question.getId())) {
                // 滚动到未回答的问题
                scrollToQuestion(question.getId());
                return false;
            }

            // 检查多选和填空是否有内容
            Object answer = answers.get(question.getId());
            if (answer == null) {
                scrollToQuestion(question.getId());
                return false;
            }

            if (question.getType() == Question.TYPE_CHECKBOX) {
                List<String> selectedOptions = (List<String>) answer;
                if (selectedOptions.isEmpty()) {
                    scrollToQuestion(question.getId());
                    return false;
                }
            } else if (question.getType() == Question.TYPE_EDITTEXT) {
                String text = (String) answer;
                if (text.trim().isEmpty()) {
                    scrollToQuestion(question.getId());
                    return false;
                }
            }
        }
        return true;
    }

    private void scrollToQuestion(int questionId) {
        // 找到问题视图并滚动到它
        for (int i = 0; i < surveyContainer.getChildCount(); i++) {
            View view = surveyContainer.getChildAt(i);
            if (view instanceof LinearLayout) {
                TextView titleView = (TextView) ((LinearLayout) view).getChildAt(0);
                Question question = questions.get(questionId - 1);
                if (titleView.getText().equals(question.getTitle())) {
                    scrollView.smoothScrollTo(0, view.getTop() - 50);
                    break;
                }
            }
        }
    }

    private void submitSurvey() {
        // 这里处理提交逻辑，例如发送到服务器
        StringBuilder surveyResult = new StringBuilder();
        for (Question question : questions) {
            surveyResult.append(question.getTitle()).append("\n");
            Object answer = answers.get(question.getId());

            if (answer instanceof List) {
                List<String> options = (List<String>) answer;
                surveyResult.append("回答: ");
                for (String option : options) {
                    surveyResult.append(option).append("、");
                }
                surveyResult.deleteCharAt(surveyResult.length() - 1); // 删除最后一个顿号
            } else {
                surveyResult.append("回答: ").append(answer);
            }
            surveyResult.append("\n\n");
        }

        // 模拟提交成功
        Toast.makeText(this, "问卷提交成功，感谢您的反馈！", Toast.LENGTH_SHORT).show();

        // 返回结果（实际应用中可发送到服务器）
        Log.d("SurveyResult", surveyResult.toString());

        // 关闭页面
        // finish();
    }
    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
// 设置提交按钮
        submitButton.setOnClickListener(v -> {
            if (validateAnswers()) {
                submitSurvey();
            } else {
                Toast.makeText(this, "请回答所有必填问题", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置返回按钮
        backButton.setOnClickListener(v -> {
            onBackPressed(); // 执行默认的返回逻辑
        });
    }
    // 问题模型类
    static class Question {
        public static final int TYPE_RADIO = 1;
        public static final int TYPE_CHECKBOX = 2;
        public static final int TYPE_EDITTEXT = 3;

        private int id;
        private String title;
        private int type;
        private String[] options;

        public Question(int id, String title, int type, String[] options) {
            this.id = id;
            this.title = title;
            this.type = type;
            this.options = options;
        }

        public Question(int id, String title, int type) {
            this.id = id;
            this.title = title;
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public int getType() {
            return type;
        }

        public String[] getOptions() {
            return options;
        }
    }
}