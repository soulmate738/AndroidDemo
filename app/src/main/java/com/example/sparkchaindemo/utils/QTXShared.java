package com.example.sparkchaindemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author anjia
 */
public class QTXShared {

        private static final String SP_NAME = "CHECKBOX_STATES"; // SP 文件名
        private static final String[] CHECKBOX_KEYS = { // 8 个 Checkbox 的键
                "checkBox1_state", "checkBox2_state",
                "checkBox3_state", "checkBox4_state",
                "checkBox5_state", "checkBox6_state",
                "checkBox7_state", "checkBox8_state",
                "checkBox9_state", "checkBox10_state"

        };

        private final SharedPreferences sp;
        private final SharedPreferences.Editor editor;

        public QTXShared(Context context) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            editor = sp.edit();
            // 首次初始化：若不存在任何数据，则创建默认状态（全未选中）
            if (sp.getAll().isEmpty()) {
                for (String key : CHECKBOX_KEYS) {
                    editor.putBoolean(key, false).apply(); // 默认未选中
                }
            }
        }

        // 保存单个 Checkbox 状态
        public void saveState(int position, boolean isChecked) {
            if (position >= 0 && position < CHECKBOX_KEYS.length) {
                String key = CHECKBOX_KEYS[position];
                editor.putBoolean(key, isChecked).apply();
            }
        }

        // 获取单个 Checkbox 状态
        public boolean getState(int position) {
            if (position >= 0 && position < CHECKBOX_KEYS.length) {
                String key = CHECKBOX_KEYS[position];
                return sp.getBoolean(key, false); // 第二个参数为默认值（防止异常）
            }
            return false;
        }

        // 清空所有状态（可选功能）
        public void clearAll() {
            editor.clear().apply();
        }

}
