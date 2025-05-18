package com.example.sparkchaindemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.adapter.ClassAdapter;
import com.example.sparkchaindemo.base.BaseActivity;
import com.example.sparkchaindemo.entity.ClassItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShouYuActivity extends BaseActivity {
    private ImageView mainclassBack;
    private RecyclerView mainclassRV;
    List<ClassItem> itemList=new ArrayList<>();
    private SharedPreferences loginUser;
    private  boolean isFirst=true;
    ClassAdapter classAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_shou_yu);
        mainclassBack = findViewById(R.id.iv_back);
        mainclassRV = findViewById(R.id.rv_class);

    }

    @Override
    protected void initData() {
        itemList.add(new ClassItem( "第1课 | 从零认识手语", 1, R.raw.video1, 0, "   本节课将带领学习者全面了解手语的基本概念和发展历程，深入探讨手语作为一种独立语言的特点和语法规则。学习者将掌握手语的基本表达逻辑，包括如何通过手势、面部表情和身体动作来传达完整的意思。课程还会介绍手语在不同文化和地区的差异，以及手语与口语、书面语之间的关系。通过本节课的学习，学习者将建立起对手语的基本认知框架，为后续的学习打下坚实的基础。"));
        itemList.add(new ClassItem("第2课 | 基础手势入门", 2, R.raw.video2, 0, "    本节课专注于教授手语中最基础的手势动作，这些动作是构成复杂手语表达的基石。学习者将系统学习手部的不同位置、方向、形状和运动轨迹，以及如何将这些元素组合成简单的手势单元。课程会通过大量的实例演示和练习，帮助学习者掌握正确的手势发音和动作要领，纠正常见的错误动作。同时，学习者还将了解手势的力度、速度和节奏感对手语表达的影响，开始培养手语表达的流畅性和自然度。"));
        itemList.add(new ClassItem("第3课 | 常用字母与数字", 3, R.raw.video3, 1, "   本节课将深入教授手语中的字母表和数字表示方法，这是手语交流中的重要基础技能。学习者将逐一学习每个手指字母的准确打法和发音规则，通过反复练习达到熟练掌握的程度。课程还会教授如何运用手指字母拼写简单的单词、名字和短语，以及在实际交流中如何快速识别和理解手语字母。此外，学习者将学习数字的手语表达方法，包括基数、序数、分数、小数等不同形式的数字表示，为日常交流和数字信息传递做好准备。"));
        itemList.add(new ClassItem( "第4课 | 日常问候用语",  4, R.raw.video4, 2,"   本节课聚焦于日常生活中最常用的问候语和简单对话表达，帮助学习者快速进入实际应用场景。学习者将系统学习各种打招呼的方式、自我介绍的常用表达、询问姓名和联系方式的标准手语句型，以及相应的回应方式。课程会模拟不同的社交场景，如初次见面、重逢、告别等，让学习者在实践中运用所学的问候用语，培养真实场景下的交流能力。同时，学习者还将学习在交流中如何运用适当的面部表情和身体语言来增强表达的感染力和亲和力。"));
        itemList.add(new ClassItem( "第5课 | 家庭与亲属称呼",5, R.raw.video5, 2, "   本节课将带领学习者进入家庭和亲属关系的手语表达领域，深入学习家庭成员和亲属关系的各种手语表达方法。学习者将掌握从父母、兄弟姐妹到祖父母、外祖父母等直系亲属，以及姑姑、叔叔、舅舅、阿姨等旁系亲属的准确手语表示。课程还会教授如何用手语介绍自己的家庭结构、描述家庭成员的特征和职业，以及表达亲情和家庭关系的常用词汇和句子。通过本节课的学习，学习者将能够自如地用手语谈论自己和他人的家庭，增进与听障人士在家庭话题上的交流。"));
        itemList.add(new ClassItem("第6课 | 身体部位表达", 6, R.raw.video1, 2,  "   本节课专注于人体各个部位的手语表达学习，这是日常生活和健康交流中必不可少的技能。学习者将系统学习从头部到脚部的各个身体部位的准确手语表示方法，包括五官、四肢、躯干等各个部分的详细表达。课程还会教授如何用手语描述身体的动作和姿势，如站立、坐下、行走等，以及如何表达身体的感觉和状况，如疼痛、疲劳、舒适等。此外，学习者将学习在医疗场景中如何用手语准确描述症状和健康问题，提高与医护人员沟通的效率和准确性。"));
        itemList.add(new ClassItem( "第7课 | 颜色与形状",  7, R.raw.video1, 2,"    本节课将深入探讨颜色和形状的手语表达方法，帮助学习者丰富手语表达的视觉描述能力。学习者将系统学习各种常见颜色的准确手语表示，包括基本颜色和一些混合颜色的表达技巧。课程还会教授如何用手语描述物体的形状，如圆形、方形、三角形等各种几何形状，以及如何表达物体的大小、长短、宽窄等维度特征。通过本节课的学习，学习者将能够用手语更加生动、准确地描述物体的外观特征，为艺术、设计、购物等场景下的交流提供有力支持。"));
        itemList.add(new ClassItem("第8课 | 时间与日期表达", 8, R.raw.video1, 2, "   本节课专注于时间和日期的手语表达学习，这是日常生活和工作中不可或缺的交流内容。学习者将系统学习年、月、日、星期、小时、分钟、秒等时间单位的准确手语表示方法，以及如何用手语表达具体的时间点和时间段。课程会教授不同时间表达方式在不同场景下的应用，如约会、会议、行程安排等。学习者还将学习如何用手语询问和回答关于时间的问题，以及如何表达时间的先后顺序和持续时间。通过本节课的学习，学习者将能够在时间相关的交流中做到准确、清晰。"));
        itemList.add(new ClassItem("第9课 | 情感与状态描述", 9, R.raw.video1, 2, "   本节课将深入探讨情感和状态的手语表达方法，帮助学习者更好地表达自己的内心世界和理解他人的情感状态。学习者将系统学习各种基本情感的手语表达，如高兴、悲伤、愤怒、惊讶、恐惧等，以及如何用手语描述更复杂的情感和情绪状态。课程还会教授如何用手语表达身体和心理的状态，如健康、疲劳、饥饿、快乐、焦虑等。通过本节课的学习，学习者将能够更加细腻地表达自己的情感和状态，增强与听障人士之间的情感沟通和理解。"));
        itemList.add(new ClassItem("第10课 | 综合应用与对话", 10, R.raw.video1, 2, "    本节课是对手语课程内容的综合应用和提升，通过模拟各种真实场景的对话练习，帮助学习者将前面所学的知识融会贯通，提高手语的流利度和实际应用能力。课程会设置多种不同主题的对话场景，如购物、餐厅点餐、交通出行、医疗咨询等，让学习者在实践中运用所学的手语知识进行交流。学习者将学习如何组织句子、运用合适的词汇和语法结构，以及如何根据对话对象和场景调整自己的表达风格。通过本节课的学习，学习者将能够自信、流畅地用手语进行日常交流，为与听障人士的无障碍沟通打下坚实的基础。"));
        // 初始化绑定MainItemAdapter
        classAdapter=new ClassAdapter(itemList);
        mainclassRV.setAdapter(classAdapter);
        // 设置LinearLayoutManager
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        mainclassRV.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void initListener() {
        mainclassBack.setOnClickListener(v -> finish());
        classAdapter.setOnItemClickListener(new ClassAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (itemList.get(position).getStatus()==2){
                    Toast.makeText(ShouYuActivity.this, "该课暂未开放", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(ShouYuActivity.this, ClassActivity.class);
                    intent.putExtra("resource", itemList.get(position).getResource());
                    intent.putExtra("title", itemList.get(position).getTitle());
                    intent.putExtra("content", itemList.get(position).getContent());
                    startActivity(intent);
                }
            }
        });
    }
}