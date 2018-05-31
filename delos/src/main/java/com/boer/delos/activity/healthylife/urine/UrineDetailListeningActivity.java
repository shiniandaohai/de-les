package com.boer.delos.activity.healthylife.urine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.SerializableMap;
import com.boer.delos.commen.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Description: 尿检详情页
 */
public class UrineDetailListeningActivity extends BaseActivity {

    @Bind(R.id.viewpager)
    ViewPager mViewpager;
    @Bind(R.id.pagertab)
    PagerTabStrip mPagertab;

    //尿检状态的Map
    private Map<String, String> statesMap = new HashMap<String, String>();
    //尿检列表的图片Map
    private Map<String, int[]> imagesMap = new HashMap<String, int[]>();
    //尿检点击列表，跳转到详细说明，需要显示的说明项
    private Map<String, String> judgeIndexMap = new HashMap<String, String>();
    //尿检仪获取数据对应的名称
    private ArrayList<String> dataList = new ArrayList<String>();

    private ArrayList<View> viewList = new ArrayList<View>();// 将要分页显示的View装入数组中

    private String result = "";
    private static final String LEU = "白细胞（LEU）";
    private static final String NIT = "亚硝酸盐（NIT）";
    private static final String UBG = "尿胆原（UBG）";
    private static final String PRO = "尿蛋白（PRO）";
    private static final String PH = "PH值（PH）";
    private static final String BLD = "潜血（BLD）";
    private static final String SG = "比重（SG）";
    private static final String KET = "酮体（KET）";
    private static final String BIL = "胆红素（BIL）";
    private static final String GLU = "葡萄糖（GLU）";
    private static final String VC = "维生素C（VC）";
    //名词解释
    private Map<String, String> ExplainMap = new HashMap<String, String>();
    //描述
    private Map<String, String> DescribeMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urine_detail);
        ButterKnife.bind(this);

        mPagertab.setTabIndicatorColor(Color.BLUE);
        mPagertab.setBackgroundColor(getResources().getColor(R.color.pager));
        mPagertab.setDrawFullUnderline(true);
        setDefaultData();
        Intent intent = getIntent();
        dataList = intent.getStringArrayListExtra("dataList");


        Bundle bundle = intent.getExtras();

        SerializableMap serializableMap1 = (SerializableMap) bundle
                .get("statesMap");
        statesMap = serializableMap1.getStringMap();

        SerializableMap serializableMap2 = (SerializableMap) bundle
                .get("judgeIndexMap");
        judgeIndexMap = serializableMap2.getStringMap();

        SerializableMap serializableMap3 = (SerializableMap) bundle
                .get("imagesMap");
        imagesMap = serializableMap3.getIntsMap();
        LayoutInflater lf = getLayoutInflater().from(this);
        for (int i = 0; i < dataList.size(); i++) {
            viewList.add(lf.inflate(R.layout.viewpager_container, null));
        }

        mViewpager.setAdapter(mPagerAdapter);
        mViewpager.setCurrentItem(bundle.getInt("position"));
        initTopBar(dataList.get(bundle.getInt("position")).split("（")[0] + "分析", null, true, true);
        ivRight.setImageResource(R.drawable.ic_health_live_more);
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UrineDetailListeningActivity.this, UrinalysisHistoryActivity.class));
            }
        });
    }

    PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView(viewList.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            tvTitle.setText(dataList.get(position).split("（")[0] + "分析");
            return dataList.get(position).split("（")[0];

        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(viewList.get(position));
            TextView textViewUrineState = (TextView) viewList.get(position).findViewById(R.id.id_txtUrineState);
            textViewUrineState.setText(Html.fromHtml(statesMap.get(dataList.get(position))));

            LinearLayout linearLayout = (LinearLayout) viewList.get(position).findViewById(R.id.id_linearLayoutAddView);
            linearLayout.removeAllViews();
            for (int i : imagesMap.get(dataList.get(position))) {
                linearLayout.addView(getBaseImageView(i));
            }

            TextView textViewExplain = (TextView) viewList.get(position).findViewById(R.id.id_textViewExplain);
            textViewExplain.setText(Html.fromHtml(ExplainMap.get(dataList.get(position))));
            TextView textViewDescribe = (TextView) viewList.get(position).findViewById(R.id.id_textViewDescribe);
            textViewDescribe.setText(Html.fromHtml(DescribeMap.get(dataList.get(position))));
            ImageView imageView = (ImageView) viewList.get(position).findViewById(R.id.id_imageViewQuestion);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UrineDialog(UrineDetailListeningActivity.this, UrineResultListeningActivity.JudgeDataArrayList, dataList.get(position), judgeIndexMap.get(dataList.get(position)));
                }
            });
            return viewList.get(position);
        }
    };

    //列表中根据值加入ImageView，创建基础的ImageView
    private ImageView getBaseImageView(int sourceID) {
        ImageView imageView = new ImageView(UrineDetailListeningActivity.this);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setMargins(15, 0, 15, 0);
        imageView.setLayoutParams(layout);
        imageView.setBackgroundResource(sourceID);
        return imageView;
    }


    private void setDefaultData() {
        ExplainMap.put(LEU, "名词解释：<br/>白细胞酯酶是人体白细胞内含有的一种特异性酶类，临床常用这种酶类来检测标本中有无白细胞存在。这个结果阳性说明所检测的标本中有白细胞存在，说明泌尿系统有感染的可能。");
        DescribeMap.put(LEU, "<font color='#00a0e9'>阳性：</font><br/>通常提示尿路感染。可见于：肾盂肾炎、膀胱炎、尿道炎、前列腺炎等。");

        ExplainMap.put(NIT, "名词解释：<br/>亚硝酸盐是泌尿系统细菌性感染的筛选指标，某些泌尿系统存在革兰氏阴性杆菌以将尿中蛋白质代谢产物硝酸盐还原为亚硝酸盐，因此测定尿液中是否存在亚硝酸盐就可以快速间接地知道泌尿系统细菌感染的情况。");
        DescribeMap.put(NIT, "<font color='#00a0e9'>阳性：</font><br/>可能有尿道感染，也可能与饮食因素，如进食过多咸菜和腌制品等亚硝酸盐含量高的食物有关。");

        ExplainMap.put(UBG, "名词解释：<br/>结核性胆红素排入肠腔转化为尿胆原，从粪便排出为粪胆原。大部分尿胆原从肠道重吸收经肝转化为结合胆红素再排入肠腔，小部分尿胆原从肾小球滤过或肾小管排除后即为尿胆原。");
        DescribeMap.put(UBG, "<font color='#00a0e9'>阳性：</font><br/>可能有黄疸，与肝功问题、溶血等有关。尿胆原、尿胆红素、尿胆素称为“尿三胆”，它并不提示肾脏有疾病，而是用来鉴别不同原因的黄疸，尿胆原检测室鉴别阻塞性黄疸与溶血性黄疸和肝细胞黄疸的重要指标之一。此外，高热、心功能不全、便秘等也可使尿胆原轻度升高。");

        ExplainMap.put(PRO, "名词解释：<br/>健康成人24小时尿中排出蛋白质总量为(80±24) mg，如24小时尿液中蛋白质含量超过150mg，称为尿蛋白，分为生理性尿蛋白和病例性尿蛋白。生理性尿蛋白指泌尿系统无器质性鬓边，尿内暂时或一过性出现尿蛋白。病例性尿蛋白是指泌尿系统发生气质性病变，尿中蛋白质持续超过150mg/24小时。病例性尿蛋白是肾脏疾病的可靠依据。");
        DescribeMap.put(PRO, "<font color='#00a0e9'>阳性：</font><br/>1、原发性肾小球疾病有急慢性肾炎，肾病综合症。<br/>2、任何对肾脏产生损害的疾病都会引起尿蛋白，如高血压肾病、糖尿病肾病、狼疮性肾病、骨髓瘤肾病等。<br/>3、尿路感染也会伴随尿蛋白，但当感染好转后消失。<br/>尿蛋白持续阳性，往往代表肾脏发生了病变，故临床可依据尿蛋白阳性的多少来判定肾病损伤的程度以及肾病治疗的效果。");

        ExplainMap.put(PH, "名词解释：<br/>尿pH值（酸碱度）一般情况下在6.5左右。正常尿为弱酸性，也可为中性或弱碱性，尿的酸碱度在很大程度上取决于饮食种类、服用的药物及疾病类型。");
        DescribeMap.put(PH, "<font color='#00a0e9'>偏高：</font><br/>进食过多的蔬菜、水果的尿液可呈碱性；可能有尿路感染、应用利尿剂或肾功能不良等情形，以及频繁呕吐、呼吸性碱中毒等。<br/><font color='#00a0e9'>偏低：</font><br/>进食肉类、海鲜、剧烈运动、饥饿、大量出汗是尿液常呈酸性；可见于酸中毒、糖尿病、慢性肾小球肾炎、痛风、正值饥饿状态、酮酸症等。");

        ExplainMap.put(BLD, "名词解释：<br/>尿液分析仪检测的尿潜血是指尿液中红细胞和红细胞变形裂解后溢出的血红蛋白，因此尿潜血报告的阳性程度往往高于显微镜检查的红细胞数。正常人尿液中可有极少量陈旧红细胞，偶尔引起尿潜血的微弱阳性，成年女性由于白带污染常引起尿潜血的弱阳性。");
        DescribeMap.put(BLD, "<font color='#00a0e9'>阳性：</font><br/>可能提示有急慢性肾炎、尿路结石、肾损伤、肿瘤、血液系统疾病等。需要排除月经、药物、剧烈运动等假性血尿的影响。");

        ExplainMap.put(SG, "名词解释：<br/>尿比重受年龄、饮水量和出汗的影响，一般婴幼儿的尿比重偏低；尿比重的高低，主要取决于肾脏的浓缩功能，故测定尿比重用于肾脏疾病的辅助诊断和病情监测。");
        DescribeMap.put(SG, "<font color='#00a0e9'>偏高：</font><br/>短暂性升高可能是食物因素，持续性增高可见于急性肾炎、糖尿病、高热、大汗等。<br/><font color='#00a0e9'>偏低：</font><br/>尿比重偏低常出现在多饮水之后，若提示疾病则表示肾浓缩功能减退，可见于尿崩症、慢性肾炎、高血压动脉硬化、精神性多饮多尿症等。");

        ExplainMap.put(KET, "名词解释：<br/>酮体是脂肪酸的分解产物，包括丙酮、乙酰乙酸、ß-羟基丁酸。正常人尿中酮体含量极少，一般定性试验为阴性。");
        DescribeMap.put(KET, "<font color='#00a0e9'>阳性：</font><br/>见于糖尿病、腹泻、酸中毒、肺炎、急性风湿热；此外，剧烈运动、腹泻、饮食缺乏糖类、饥饿、分娩后摄入过多的脂肪和蛋白质等也可出现阳性。");

        ExplainMap.put(BIL, "名词解释：<br/>胆红素为橙黄色化合物，血浆中有3中：未结合胆红素、结合胆红素和δ-胆红素。结合胆红素相对分支质量小，溶解度高，可通过肾小球滤膜由尿排出。正常人血中结合胆红素含量很低，滤过量极低。");
        DescribeMap.put(BIL, "<font color='#00a0e9'>阳性：</font><br/>可能提示有胆石症、胆道肿瘤、胆道蛔虫、胰头癌等引起的梗阻性黄疸；肝癌、肝硬化、急慢性肝炎、肝细胞坏死等导致的肝细胞性黄疸。在检查前有其它原因，如感冒、过敏、服用感冒药或抗生素和大量进食蛋白等，多食肉类和动物肝脏可引起尿胆红素增高。");

        ExplainMap.put(GLU, "名词解释：<br/>生理情况下，正常人血糖浓度维持在相对稳定水平，尿中可有微量葡萄糖，浓度为0.3~0.8mmol/L，由于肾小管近曲小管对葡萄糖分子的重吸收，晨尿或空腹尿定性试验阴性，尿糖定性阳性称为糖尿，是诊断糖尿病的重要线索。");
        DescribeMap.put(GLU, "<font color='#00a0e9'>阳性：</font><br/>可能提示有糖尿病、甲亢、慢性肾小球肾炎、肾病综合征等；较快摄入大量糖类、应激状态、妊娠后期及哺乳期妇女，糖尿也会出现阳性。");

        ExplainMap.put(VC, "名词解释：<br/>维C检测受饮食因素影响，食物中如果维C含量高，尿中就有可能高于正常值。部分检验项目如葡萄糖、潜血、胆红素、亚硝酸盐和尿pH值等通过氧化还原反应及pH改变的原理进行检测。维生素C作为强还原剂，可干扰上述各种尿液成分与试剂条中化学物质的氧化还原反应，从而常常造成假阴性的检测结果，干扰临床的诊断和治疗。");
        DescribeMap.put(VC, "<font color='#00a0e9'>阳性：</font><br/>提示尿液红细胞、胆红素、亚硝酸盐，检测可能出现假阳性。");
    }

}
