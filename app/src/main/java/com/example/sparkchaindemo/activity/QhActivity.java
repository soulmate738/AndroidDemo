package com.example.sparkchaindemo.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.base.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QhActivity extends BaseActivity {
    private EditText searchEditText;
    private ListView areaCodeListView;
    private Button backButton;
    private ArrayAdapter<String> adapter;
    private List<String> originalDataList;
    private List<String> filteredDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_qh);
        // 初始化视图组件
        searchEditText = findViewById(R.id.searchEditText);
        areaCodeListView = findViewById(R.id.areaCodeListView);
        backButton = findViewById(R.id.backButton);

    }

    @Override
    protected void initData() {
        // 初始化数据
        initializeData();
        // 设置适配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredDataList);
        areaCodeListView.setAdapter(adapter);

        // 设置搜索功能
        setupSearchFunctionality();

    }

    @Override
    protected void initListener() {
        // 设置返回按钮点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 不需要实现
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当搜索框文本变化时执行过滤
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 不需要实现
            }
        });
    }

    private void filterData(String searchText) {
        filteredDataList.clear();
        if (searchText.isEmpty()) {
            // 如果搜索框为空，显示所有数据
            filteredDataList.addAll(originalDataList);
        } else {
            // 否则，根据搜索文本过滤数据
            searchText = searchText.toLowerCase();
            for (String item : originalDataList) {
                if (item.toLowerCase().contains(searchText)) {
                    filteredDataList.add(item);
                }
            }
        }
        // 通知适配器数据已更改
        adapter.notifyDataSetChanged();
    }
    private void initializeData() {
        // 创建城市和区号数据
        Map<String, String> areaCodeMap = new HashMap<>();
        areaCodeMap.put("北京", "010");
        areaCodeMap.put("鄂州", "0711");
        areaCodeMap.put("恩施", "0718");
        areaCodeMap.put("黄冈", "0713");
        areaCodeMap.put("黄石", "0714");
        areaCodeMap.put("荆门", "0724");
        areaCodeMap.put("荆州", "0716");
        areaCodeMap.put("十堰", "0719");
        areaCodeMap.put("随州", "0722");
        areaCodeMap.put("武汉", "027");
        areaCodeMap.put("咸宁", "0715");
        areaCodeMap.put("襄樊", "0710");
        areaCodeMap.put("孝感", "0712");
        areaCodeMap.put("宜昌", "0717");
        areaCodeMap.put("潮州", "0768");
        areaCodeMap.put("东莞", "0769");
        areaCodeMap.put("佛山", "0757");
        areaCodeMap.put("广州", "021");
        areaCodeMap.put("河源", "0762");
        areaCodeMap.put("惠州", "0752");
        areaCodeMap.put("江门", "0750");
        areaCodeMap.put("揭阳", "0663");
        areaCodeMap.put("茂名", "0668");
        areaCodeMap.put("梅州", "0753");
        areaCodeMap.put("清远", "0763");
        areaCodeMap.put("汕头", "0754");
        areaCodeMap.put("汕尾", "0660");
        areaCodeMap.put("韶关", "0751");
        areaCodeMap.put("阳江", "0662");
        areaCodeMap.put("云浮", "0766");
        areaCodeMap.put("湛江", "0759");
        areaCodeMap.put("肇庆", "0758");
        areaCodeMap.put("中山", "0760");
        areaCodeMap.put("珠海", "0756");
        areaCodeMap.put("上海", "021");
        areaCodeMap.put("巴中", "0827");
        areaCodeMap.put("成都", "028");
        areaCodeMap.put("成都郊县", "");
        areaCodeMap.put("达州", "0818");
        areaCodeMap.put("德阳", "0838");
        areaCodeMap.put("甘孜", "0836");
        areaCodeMap.put("广安", "0826");
        areaCodeMap.put("广元", "0839");
        areaCodeMap.put("乐山", "0833");
        areaCodeMap.put("凉山", "0834");
        areaCodeMap.put("泸州", "0830");
        areaCodeMap.put("眉山", "0833");
        areaCodeMap.put("绵阳", "0816");
        areaCodeMap.put("内江", "0832");
        areaCodeMap.put("南充", "0817");
        areaCodeMap.put("攀枝花", "0812");
        areaCodeMap.put("遂宁", "0825");
        areaCodeMap.put("雅安", "0835");
        areaCodeMap.put("宜宾", "0831");
        areaCodeMap.put("资阳", "0832");
        areaCodeMap.put("自贡", "0813");
        areaCodeMap.put("鞍山", "0412");
        areaCodeMap.put("本溪", "0414");
        areaCodeMap.put("朝阳", "0421");
        areaCodeMap.put("丹东", "0415");
        areaCodeMap.put("抚顺", "0413");
        areaCodeMap.put("阜新", "0418");
        areaCodeMap.put("葫芦岛", "0429");
        areaCodeMap.put("锦州", "0416");
        areaCodeMap.put("辽阳", "0419");
        areaCodeMap.put("盘锦", "0427");
        areaCodeMap.put("沈阳", "024");
        areaCodeMap.put("铁岭", "0410");
        areaCodeMap.put("营口", "0417");
        areaCodeMap.put("安康", "0915");
        areaCodeMap.put("宝鸡", "0917");
        areaCodeMap.put("汉中", "0916");
        areaCodeMap.put("商洛", "0914");
        areaCodeMap.put("铜川", "0929");
        areaCodeMap.put("渭南", "0913");
        areaCodeMap.put("西安", "029");
        areaCodeMap.put("咸阳", "0910");
        areaCodeMap.put("延安", "0911");
        areaCodeMap.put("榆林", "0912");
        areaCodeMap.put("海南", "0898");
        areaCodeMap.put("常州", "0519");
        areaCodeMap.put("淮安", "0517");
        areaCodeMap.put("连云港", "0518");
        areaCodeMap.put("南京", "025");
        areaCodeMap.put("南通", "0513");
        areaCodeMap.put("苏州", "0512");
        areaCodeMap.put("宿迁", "0527");
        areaCodeMap.put("泰州", "0523");
        areaCodeMap.put("无锡", "0510");
        areaCodeMap.put("徐州", "0516");
        areaCodeMap.put("盐城", "0515");
        areaCodeMap.put("扬州", "0514");
        areaCodeMap.put("镇江", "0511");
        areaCodeMap.put("杭州", "0571");
        areaCodeMap.put("湖州", "0572");
        areaCodeMap.put("嘉兴", "0573");
        areaCodeMap.put("金华", "0579");
        areaCodeMap.put("丽水", "0578");
        areaCodeMap.put("衢州", "0570");
        areaCodeMap.put("绍兴", "0575");
        areaCodeMap.put("台州", "0576");
        areaCodeMap.put("温州", "0577");
        areaCodeMap.put("舟山", "0580");
        areaCodeMap.put("滨州", "0543");
        areaCodeMap.put("德州", "0534");
        areaCodeMap.put("东营", "0546");
        areaCodeMap.put("菏泽", "0530");
        areaCodeMap.put("济南", "0531");
        areaCodeMap.put("济宁", "0537");
        areaCodeMap.put("莱芜", "0634");
        areaCodeMap.put("聊城", "0635");
        areaCodeMap.put("临沂", "0539");
        areaCodeMap.put("日照", "0633");
        areaCodeMap.put("泰安", "0538");
        areaCodeMap.put("威海", "0631");
        areaCodeMap.put("潍坊", "0536");
        areaCodeMap.put("烟台", "0535");
        areaCodeMap.put("枣庄", "0632");
        areaCodeMap.put("淄博", "0533");
        areaCodeMap.put("安阳", "0372");
        areaCodeMap.put("鹤壁", "0392");
        areaCodeMap.put("济源", "0391");
        areaCodeMap.put("焦作", "0391");
        areaCodeMap.put("开封", "0378");
        areaCodeMap.put("洛阳", "0379");
        areaCodeMap.put("漯河", "0395");
        areaCodeMap.put("南阳", "0377");
        areaCodeMap.put("平顶山", "0375");
        areaCodeMap.put("濮阳", "0393");
        areaCodeMap.put("三门峡", "0398");
        areaCodeMap.put("商丘", "0370");
        areaCodeMap.put("新乡", "0373");
        areaCodeMap.put("信阳", "0376");
        areaCodeMap.put("许昌", "0374");
        areaCodeMap.put("郑州", "0371");
        areaCodeMap.put("周口", "0394");
        areaCodeMap.put("驻马店", "0396");
        areaCodeMap.put("天津", "022");
        areaCodeMap.put("重庆", "023");
        areaCodeMap.put("福州", "0591");
        areaCodeMap.put("龙岩", "0597");
        areaCodeMap.put("南平", "0599");
        areaCodeMap.put("宁德", "0593");
        areaCodeMap.put("莆田", "0594");
        areaCodeMap.put("泉州", "0595");
        areaCodeMap.put("三明", "0598");
        areaCodeMap.put("厦门", "0592");
        areaCodeMap.put("漳州", "0596");
        areaCodeMap.put("长沙", "0731");
        areaCodeMap.put("常德", "0736");
        areaCodeMap.put("郴州", "0735");
        areaCodeMap.put("衡阳", "0734");
        areaCodeMap.put("怀化", "0745");
        areaCodeMap.put("吉首", "0743");
        areaCodeMap.put("娄底", "0738");
        areaCodeMap.put("邵阳", "0739");
        areaCodeMap.put("湘潭", "0732");
        areaCodeMap.put("益阳", "0737");
        areaCodeMap.put("永州", "0746");
        areaCodeMap.put("岳阳", "0730");
        areaCodeMap.put("张家界", "0744");
        areaCodeMap.put("株洲", "0733");
        areaCodeMap.put("深圳", "0755");
        areaCodeMap.put("安庆", "0556");
        areaCodeMap.put("蚌埠", "0552");
        areaCodeMap.put("巢湖", "0565");
        areaCodeMap.put("池州", "0566");
        areaCodeMap.put("滁州", "0550");
        areaCodeMap.put("阜阳", "0558");
        areaCodeMap.put("毫州", "0558");
        areaCodeMap.put("合肥", "0551");
        areaCodeMap.put("淮北", "0561");
        areaCodeMap.put("淮南", "0554");
        areaCodeMap.put("黄山", "0559");
        areaCodeMap.put("六安", "0564");
        areaCodeMap.put("马鞍山", "0555");
        areaCodeMap.put("宿州", "0557");
        areaCodeMap.put("铜陵", "0562");
        areaCodeMap.put("芜湖", "0553");
        areaCodeMap.put("宣城", "0563");
        areaCodeMap.put("大连", "0411");
        areaCodeMap.put("青岛", "0532");
        areaCodeMap.put("宁波", "0574");
        areaCodeMap.put("保定", "0312");
        areaCodeMap.put("沧州", "0317");
        areaCodeMap.put("承德", "0314");
        areaCodeMap.put("邯郸", "0310");
        areaCodeMap.put("衡水", "0318");
        areaCodeMap.put("廊坊", "0316");
        areaCodeMap.put("秦皇岛", "0335");
        areaCodeMap.put("石家庄", "0311");
        areaCodeMap.put("唐山", "0315");
        areaCodeMap.put("邢台", "0319");
        areaCodeMap.put("张家口", "0313");
        areaCodeMap.put("大庆", "0459");
        areaCodeMap.put("大兴安岭", "0457");
        areaCodeMap.put("哈尔滨", "0451");
        areaCodeMap.put("鹤岗", "0468");
        areaCodeMap.put("黑河", "0456");
        areaCodeMap.put("鸡西", "0467");
        areaCodeMap.put("佳木斯", "0454");
        areaCodeMap.put("牡丹江", "0453");
        areaCodeMap.put("齐齐哈尔", "0452");
        areaCodeMap.put("绥化", "0455");
        areaCodeMap.put("伊春", "0458");
        areaCodeMap.put("保山", "0875");
        areaCodeMap.put("楚雄", "0878");
        areaCodeMap.put("大理", "0872");
        areaCodeMap.put("德宏", "0692");
        areaCodeMap.put("红河", "0873");
        areaCodeMap.put("景洪", "0691");
        areaCodeMap.put("昆明", "0871");
        areaCodeMap.put("丽江", "0888");
        areaCodeMap.put("临沧", "0883");
        areaCodeMap.put("曲靖", "0874");
        areaCodeMap.put("思茅", "0879");
        areaCodeMap.put("文山", "0876");
        areaCodeMap.put("玉溪", "0877");
        areaCodeMap.put("昭通", "0870");
        areaCodeMap.put("白城", "0436");
        areaCodeMap.put("长春", "0431");
        areaCodeMap.put("吉林", "0432");
        areaCodeMap.put("辽源", "0437");
        areaCodeMap.put("四平", "0434");
        areaCodeMap.put("松原", "0438");
        areaCodeMap.put("通化", "0435");
        areaCodeMap.put("延吉", "0433");
        areaCodeMap.put("长治", "0355");
        areaCodeMap.put("大同", "0352");
        areaCodeMap.put("晋城", "0356");
        areaCodeMap.put("晋中", "0354");
        areaCodeMap.put("临汾", "0357");
        areaCodeMap.put("吕梁", "0359");
        areaCodeMap.put("朔州", "0349");
        areaCodeMap.put("太原", "0351");
        areaCodeMap.put("忻州", "0350");
        areaCodeMap.put("阳泉", "0353");
        areaCodeMap.put("运城", "0359");
        areaCodeMap.put("阿克苏", "0997");
        areaCodeMap.put("博州", "0909");
        areaCodeMap.put("昌吉", "0994");
        areaCodeMap.put("哈密", "0902");
        areaCodeMap.put("喀什", "0998");
        areaCodeMap.put("克拉玛依", "0990");
        areaCodeMap.put("库尔勒", "0996");
        areaCodeMap.put("奎屯", "0992");
        areaCodeMap.put("石河子", "0993");
        areaCodeMap.put("塔城", "0901");
        areaCodeMap.put("吐鲁番", "0995");
        areaCodeMap.put("乌鲁木齐", "0991");
        areaCodeMap.put("伊犁", "0999");
        areaCodeMap.put("抚州", "0794");
        areaCodeMap.put("赣州", "0797");
        areaCodeMap.put("吉安", "0796");
        areaCodeMap.put("景德镇", "0798");
        areaCodeMap.put("九江", "0792");
        areaCodeMap.put("南昌", "0791");
        areaCodeMap.put("萍乡", "0799");
        areaCodeMap.put("上饶", "0793");
        areaCodeMap.put("新余", "0790");
        areaCodeMap.put("宜春", "0795");
        areaCodeMap.put("鹰潭", "0701");
        areaCodeMap.put("百色", "0776");
        areaCodeMap.put("北海", "0779");
        areaCodeMap.put("桂林", "0773");
        areaCodeMap.put("河池", "0778");
        areaCodeMap.put("贺州", "0774");
        areaCodeMap.put("来宾", "0772");
        areaCodeMap.put("柳州", "0772");
        areaCodeMap.put("南宁", "0771");
        areaCodeMap.put("钦州", "0777");
        areaCodeMap.put("梧州", "0774");
        areaCodeMap.put("玉林", "0775");
        areaCodeMap.put("巴彦淖尔", "0478");
        areaCodeMap.put("包头", "0472");
        areaCodeMap.put("赤峰", "0476");
        areaCodeMap.put("鄂尔多斯", "0477");
        areaCodeMap.put("呼和浩特", "0471");
        areaCodeMap.put("呼伦贝尔", "0470");
        areaCodeMap.put("通辽", "0475");
        areaCodeMap.put("乌海", "0473");
        areaCodeMap.put("乌兰察布", "0474");
        areaCodeMap.put("锡林郭勒", "0479");
        areaCodeMap.put("白银", "0943");
        areaCodeMap.put("酒泉", "0937");
        areaCodeMap.put("兰州", "0931");
        areaCodeMap.put("天水", "0938");
        areaCodeMap.put("武威", "0935");
        areaCodeMap.put("张掖", "0936");
        areaCodeMap.put("宁夏", "0951");
        areaCodeMap.put("安顺", "0853");
        areaCodeMap.put("毕节", "0857");
        areaCodeMap.put("贵阳", "0851");
        areaCodeMap.put("六盘水", "0858");
        areaCodeMap.put("黔东南", "0855");
        areaCodeMap.put("黔南", "0854");
        areaCodeMap.put("黔西南", "0859");
        areaCodeMap.put("铜仁", "0856");
        areaCodeMap.put("遵义", "0852");
        areaCodeMap.put("青海", "0971");
        // 转换为列表并排序
        originalDataList = new ArrayList<>();
        for (Map.Entry<String, String> entry : areaCodeMap.entrySet()) {
            originalDataList.add(entry.getKey() + " - " + entry.getValue());
        }
        Collections.sort(originalDataList);

        // 初始化过滤后的数据列表
        filteredDataList = new ArrayList<>(originalDataList);
    }

}