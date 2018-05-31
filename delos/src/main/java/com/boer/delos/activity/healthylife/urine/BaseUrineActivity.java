package com.boer.delos.activity.healthylife.urine;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.BaseHealthyLifeActivity;
import com.boer.delos.model.UrineResult;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 对尿检结果的计算
 * @CreateDate: 2017/3/10 0010 08:51
 * @Modify:
 * @ModifyDate:
 */


public class BaseUrineActivity extends BaseHealthyLifeActivity {
    protected static final String LEU = "白细胞（LEU）";
    protected static final String NIT = "亚硝酸盐（NIT）";
    protected static final String UBG = "尿胆原（UBG）";
    protected static final String PRO = "尿蛋白（PRO）";
    protected static final String PH = "PH值（PH）";
    protected static final String BLD = "潜血（BLD）";
    protected static final String SG = "比重（SG）";
    protected static final String KET = "酮体（KET）";
    protected static final String BIL = "胆红素（BIL）";
    protected static final String GLU = "葡萄糖（GLU）";
    protected static final String VC = "维生素C（VC）";
    public static Integer SCORE_STANDARD = 85;

    protected int urineNumber = 0;//尿检得分

    //尿检状态的Map
    protected Map<String, String> statesMap;
    //尿检列表的图片Map
    protected Map<String, int[]> imagesMap;
    //尿检点击列表，跳转到详细说明，需要显示的说明项
    protected Map<String, String> judgeIndexMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        statesMap = new HashMap<>();
        imagesMap = new HashMap<>();
        judgeIndexMap = new HashMap<>();

    }

    //计算不同项目的值
    protected void calculateData(Map<String, String> resultMap) {
        for (String key : resultMap.keySet()) {
            String date = resultMap.get(key);
            if (key.equals(LEU)) {
                handleUrineDataWithLEU(date);
            }

            if (key.equals(NIT)) {
                handleUrineDataWithNIT(date);
            }

            if (key.equals(UBG)) {
                handleUrineDataWithUBG(date);
            }

            if (key.equals(PRO)) {
                handleUrineDataWithPRO(date);
            }

            if (key.equals(PH)) {
                handleUrineDataWithPH(date);
            }

            if (key.equals(BLD)) {
                handleUrineDataWithBLD(date);
            }

            if (key.equals(SG)) {
                handleUrineDataWithSG(date);
            }

            if (key.equals(KET)) {
                handleUrineDataWithKET(date);
            }

            if (key.equals(BIL)) {
                handleUrineDataWithBIL(date);
            }

            if (key.equals(GLU)) {
                handleUrineDataWithGLU(date);
            }

            if (key.equals(VC)) {
                handleUrineDataWithVC(date);
            }
        }
    }

    /**
     * 先计算再拿分数
     *
     * @return
     */
    protected int getUrineNumber() {
        return urineNumber;
    }


    protected Map<String, String> data2Map(UrineResult.UrineBean urineBean) {
        urineNumber = 0;
        Map<String, String> resultMap = new HashMap<>();

        UrineResult.UrineData urineData = GsonUtil.getObject(urineBean.getDetail(), UrineResult.UrineData.class);
        resultMap.put(LEU, urineData.getUrineLeu());
        resultMap.put(NIT, urineData.getUrineNit());
        resultMap.put(UBG, urineData.getUrineUbg());
        resultMap.put(PRO, urineData.getUrinePro());
        resultMap.put(PH, urineData.getUrinePh());
        resultMap.put(BLD, urineData.getUrineBld());
        resultMap.put(SG, urineData.getUrineSg());
        resultMap.put(KET, urineData.getUrineKet());
        resultMap.put(BIL, urineData.getUrineBil());
        resultMap.put(GLU, urineData.getUrineGlu());
        resultMap.put(VC, urineData.getUrineVC());

        if (StringUtil.isEmpty(urineData.getUrineTureTime())) {
            resultMap.put("time", urineBean.getMeasuretime() + "");
        } else
            resultMap.put("time", urineData.getUrineTureTime());


        return resultMap;
    }


    //白细胞数据处理
    //date数据
    private void handleUrineDataWithLEU(String date) {
        String judgeIndex = "0";
        float leu = Float.parseFloat(date);
        String state = "";// 状态
        String result = "";
        int firstImage;//第一张图片资源
        int secondImage;//第二张图片资源
        int threeImage;//第三张图片资源
        int number = 0;
        if (leu == 0) {
            state = "正常";
            result = "-";
            firstImage = R.mipmap.state2;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            number = 8;
            judgeIndex = "1";
        } else if (leu == 1) {
            state = "弱阳性";
            result = "+-";
            firstImage = R.mipmap.state1;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            number = 7;
            judgeIndex = "2";
        } else if (leu == 2) {
            state = "阳性";
            result = "+";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            number = 5;
            judgeIndex = "3";
        } else if (leu == 3) {
            state = "阳性";
            result = "++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.state2;
            number = 2;
            judgeIndex = "4";
        } else {
            state = "阳性";
            result = "+++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.normal;
            number = 1;
            judgeIndex = "5";
        }

        urineNumber = urineNumber + number;
        statesMap.put(LEU, "<font color='#71bd40'>" + state + "  </font><font color='#00a0e9'>" + result + "</font>");
        imagesMap.put(LEU, new int[]{firstImage, secondImage, threeImage});
        judgeIndexMap.put(LEU, judgeIndex);
    }

    //亚硝酸盐数据处理
    //date数据
    private void handleUrineDataWithNIT(String date) {
        String judgeIndex = "0";
        float nit = Float.parseFloat(date);
        String state = "";// 状态
        String result = "";
        int firstImage;//第一张图片资源
        int number = 0;
        if (nit == 0) {
            state = "正常";
            result = "-";
            firstImage = R.mipmap.state2;
            number = 6;
            judgeIndex = "1";
        } else {
            state = "阳性";
            result = "+";
            firstImage = R.mipmap.normal;
            number = 0;
            judgeIndex = "2";
        }
        urineNumber = urineNumber + number;
        statesMap.put(NIT, "<font color='#71bd40'>" + state + "  </font><font color='#00a0e9'>" + result + "</font>");
        imagesMap.put(NIT, new int[]{firstImage});
        judgeIndexMap.put(NIT, judgeIndex);

    }

    //尿胆原数据处理
    //date数据
    private void handleUrineDataWithUBG(String date) {
        String judgeIndex = "0";
        float ubg = Float.parseFloat(date);
        String state = "";// 状态
        String result = "";
        int firstImage;//第一张图片资源
        int secondImage;//第二张图片资源
        int threeImage;//第三张图片资源
        int number = 0;
        if (ubg == 0) {
            state = "正常";
            result = "-";
            firstImage = R.mipmap.state2;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            judgeIndex = "1";
            number = 8;
        } else if (ubg == 1) {
            state = "阳性";
            result = "+";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            judgeIndex = "2";
            number = 6;
        } else if (ubg == 2) {
            state = "阳性";
            result = "++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.state2;
            judgeIndex = "3";
            number = 4;
        } else {
            state = "阳性";
            result = "+++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.normal;
            judgeIndex = "4";
            number = 2;
        }

        urineNumber = urineNumber + number;
        statesMap.put(UBG, "<font color='#71bd40'>" + state + "  </font><font color='#00a0e9'>" + result + "</font>");
        imagesMap.put(UBG, new int[]{firstImage, secondImage, threeImage});
        judgeIndexMap.put(UBG, judgeIndex);

    }


    //尿胆白数据处理
    //date数据
    private void handleUrineDataWithPRO(String date) {
        String judgeIndex = "0";
        float pro = Float.parseFloat(date);
        String state = "";// 状态
        String result = "";
        int firstImage;//第一张图片资源
        int secondImage;//第二张图片资源
        int threeImage;//第三张图片资源
        int fourImage;//第四张图片资源
        int number = 0;
        if (pro == 0) {
            state = "正常";
            result = "-";
            firstImage = R.mipmap.state2;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            fourImage = R.mipmap.state2;
            judgeIndex = "1";
            number = 14;
        } else if (pro == 1) {
            state = "弱阳性";
            result = "+-";
            firstImage = R.mipmap.state1;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            fourImage = R.mipmap.state2;
            judgeIndex = "2";
            number = 13;
        } else if (pro == 2) {
            state = "阳性";
            result = "+";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            fourImage = R.mipmap.state2;
            judgeIndex = "3";
            number = 10;
        } else if (pro == 3) {
            state = "阳性";
            result = "++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.state2;
            fourImage = R.mipmap.state2;
            judgeIndex = "4";
            number = 8;
        } else if (pro == 4) {
            state = "阳性";
            result = "+++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.normal;
            fourImage = R.mipmap.state2;
            judgeIndex = "5";
            number = 6;
        } else {
            state = "阳性";
            result = "++++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.normal;
            fourImage = R.mipmap.normal;
            judgeIndex = "6";
            number = 4;
        }

        urineNumber = urineNumber + number;
        statesMap.put(PRO, "<font color='#71bd40'>" + state + "  </font><font color='#00a0e9'>" + result + "</font>");
        imagesMap.put(PRO, new int[]{firstImage, secondImage, threeImage, fourImage});
        judgeIndexMap.put(PRO, judgeIndex);

    }


    //PH值数据处理
    //date数据
    private void handleUrineDataWithPH(String date) {
        String judgeIndex = "0";
        float ph = Float.parseFloat(date);
        String state = "";// 状态
        String result = "";
        int firstImage;//第一张图片资源
        double newPH = 0;
        if (ph == 0) {
            newPH = 5.0;
        }
        if (ph == 1) {
            newPH = 6.0;
        }
        if (ph == 2) {
            newPH = 6.5;
        }
        if (ph == 3) {
            newPH = 7.0;
        }
        if (ph == 4) {
            newPH = 7.5;
        }
        if (ph == 5) {
            newPH = 8.0;
        }
        if (ph == 6) {
            newPH = 8.5;
        }
        int number = 0;
        if (newPH <= 5.4) {
            state = "偏低";
            judgeIndex = "1";
            number = 4;
        } else if (newPH < 7.5) {
            state = "正常";
            judgeIndex = "2";
            number = 6;
        } else {
            state = "偏高";
            judgeIndex = "3";
            number = 4;
        }

        urineNumber = urineNumber + number;
        firstImage = R.mipmap.ph;
        BigDecimal bg = new BigDecimal(newPH);
        result = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + "";

        statesMap.put(PH, "<font color='#71bd40'>" + state + "  </font><font color='#00a0e9'>" + result + "</font>");
        imagesMap.put(PH, new int[]{firstImage});
        judgeIndexMap.put(PH, judgeIndex);


    }


    //潜血数据处理
    //date数据
    private void handleUrineDataWithBLD(String date) {
        String judgeIndex = "0";
        float bld = Float.parseFloat(date);
        String state = "";// 状态
        String result = "";
        int firstImage;//第一张图片资源
        int secondImage;//第二张图片资源
        int threeImage;//第三张图片资源
        int number = 0;
        if (bld == 0) {
            state = "正常";
            result = "-";
            firstImage = R.mipmap.state2;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            judgeIndex = "1";
            number = 8;
        } else if (bld == 1) {
            state = "弱阳性";
            result = "+-";
            firstImage = R.mipmap.state1;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            judgeIndex = "2";
            number = 7;
        } else if (bld == 2) {
            state = "阳性";
            result = "+";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            judgeIndex = "3";
            number = 5;
        } else if (bld == 3) {
            state = "阳性";
            result = "++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.state2;
            judgeIndex = "4";
            number = 4;
        } else {
            state = "阳性";
            result = "+++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.normal;
            judgeIndex = "5";
            number = 2;
        }

        urineNumber = urineNumber + number;
        statesMap.put(BLD, "<font color='#71bd40'>" + state + "  </font><font color='#00a0e9'>" + result + "</font>");
        imagesMap.put(BLD, new int[]{firstImage, secondImage, threeImage});
        judgeIndexMap.put(BLD, judgeIndex);
    }

    //比重数据处理
    //date数据
    private void handleUrineDataWithSG(String date) {
        String judgeIndex = "0";
        double sg = Double.parseDouble(date);
        String state = "";// 状态
        String result = "";
        int firstImage;//第一张图片资源
        int number = 0;
        sg = sg * 0.005 + 1;
        if (sg <= 1.014) {
            state = "偏低";
            judgeIndex = "1";
            number = 5;
        } else if (sg < 1.026) {
            state = "正常";
            judgeIndex = "2";
            number = 6;
        } else {
            state = "偏高";
            judgeIndex = "3";
            number = 4;
        }

        urineNumber = urineNumber + number;
        //小数点后三位
        BigDecimal bg = new BigDecimal(sg);
        result = bg.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "";

        firstImage = R.mipmap.weight;
        statesMap.put(SG, "<font color='#71bd40'>" + state + "  </font><font color='#00a0e9'>" + result + "</font>");
        imagesMap.put(SG, new int[]{firstImage});
        judgeIndexMap.put(SG, judgeIndex);

    }

    //酮体数据处理
    //date数据
    private void handleUrineDataWithKET(String date) {
        String judgeIndex = "0";
        float ket = Float.parseFloat(date);
        String state = "";// 状态
        String result = "";
        int firstImage;//第一张图片资源
        int secondImage;//第二张图片资源
        int threeImage;//第三张图片资源
        int fourImage;//第四张图片资源
        int number = 0;
        if (ket == 0) {
            state = "正常";
            result = "-";
            firstImage = R.mipmap.state2;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            fourImage = R.mipmap.state2;
            judgeIndex = "1";
            number = 14;
        } else if (ket == 1) {
            state = "弱阳性";
            result = "+-";
            firstImage = R.mipmap.state1;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            fourImage = R.mipmap.state2;
            judgeIndex = "2";
            number = 13;
        } else if (ket == 2) {
            state = "阳性";
            result = "+";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            fourImage = R.mipmap.state2;
            judgeIndex = "3";
            number = 11;
        } else if (ket == 3) {
            state = "阳性";
            result = "++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.state2;
            fourImage = R.mipmap.state2;
            judgeIndex = "4";
            number = 9;
        } else if (ket == 4) {
            state = "阳性";
            result = "+++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.normal;
            fourImage = R.mipmap.state2;
            judgeIndex = "5";
            number = 7;
        } else {
            state = "阳性";
            result = "++++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.normal;
            fourImage = R.mipmap.normal;
            judgeIndex = "6";
            number = 4;
        }

        urineNumber = urineNumber + number;
        statesMap.put(KET, "<font color='#71bd40'>" + state + "  </font><font color='#00a0e9'>" + result + "</font>");
        imagesMap.put(KET, new int[]{firstImage, secondImage, threeImage, fourImage});
        judgeIndexMap.put(KET, judgeIndex);

    }


    //胆红素数据处理
    //date数据
    private void handleUrineDataWithBIL(String date) {
        String judgeIndex = "0";
        float bil = Float.parseFloat(date);
        String state = "";// 状态
        String result = "";
        int firstImage;//第一张图片资源
        int secondImage;//第二张图片资源
        int threeImage;//第三张图片资源
        int fourImage;//第四张图片资源
        int number = 0;
        if (bil == 0) {
            state = "正常";
            result = "-";
            firstImage = R.mipmap.state2;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            judgeIndex = "1";
            number = 8;

        } else if (bil == 1) {
            state = "阳性";
            result = "+";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            judgeIndex = "2";
            number = 6;
        } else if (bil == 2) {
            state = "阳性";
            result = "++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.state2;
            judgeIndex = "3";
            number = 4;
        } else {
            state = "阳性";
            result = "+++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.normal;
            judgeIndex = "4";
            number = 3;
        }

        urineNumber = urineNumber + number;
        statesMap.put(BIL, "<font color='#71bd40'>" + state + "  </font><font color='#00a0e9'>" + result + "</font>");
        imagesMap.put(BIL, new int[]{firstImage, secondImage, threeImage});
        judgeIndexMap.put(BIL, judgeIndex);

    }

    //葡萄糖数据处理
    //date数据
    private void handleUrineDataWithGLU(String date) {
        String judgeIndex = "0";
        float glu = Float.parseFloat(date);
        String state = "";// 状态
        String result = "";
        int firstImage;//第一张图片资源
        int secondImage;//第二张图片资源
        int threeImage;//第三张图片资源
        int fourImage;//第四张图片资源
        int number = 0;
        if (glu == 0) {
            state = "正常";
            result = "-";
            firstImage = R.mipmap.state2;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            fourImage = R.mipmap.state2;
            judgeIndex = "1";
            number = 14;
        } else if (glu == 1) {
            state = "弱阳性";
            result = "+-";
            firstImage = R.mipmap.state1;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            fourImage = R.mipmap.state2;
            judgeIndex = "2";
            number = 13;
        } else if (glu == 2) {
            state = "阳性";
            result = "+";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            fourImage = R.mipmap.state2;
            judgeIndex = "3";
            number = 11;
        } else if (glu == 3) {
            state = "阳性";
            result = "++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.state2;
            fourImage = R.mipmap.state2;
            judgeIndex = "4";
            number = 9;
        } else if (glu == 4) {
            state = "阳性";
            result = "+++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.normal;
            fourImage = R.mipmap.state2;
            judgeIndex = "5";
            number = 7;
        } else {
            state = "阳性";
            result = "++++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.normal;
            fourImage = R.mipmap.normal;
            judgeIndex = "6";
            number = 5;
        }

        urineNumber = urineNumber + number;
        statesMap.put(GLU, "<font color='#71bd40'>" + state + "  </font><font color='#00a0e9'>" + result + "</font>");
        imagesMap.put(GLU, new int[]{firstImage, secondImage, threeImage, fourImage});
        judgeIndexMap.put(GLU, judgeIndex);

    }

    //维生素C数据处理
    //date数据
    private void handleUrineDataWithVC(String date) {
        String judgeIndex = "0";
        float vc = Float.parseFloat(date);
        String state = "";// 状态
        String result = "";
        int firstImage;//第一张图片资源
        int secondImage;//第二张图片资源
        int threeImage;//第三张图片资源
        int number = 0;
        if (vc == 0) {
            state = "正常";
            result = "-";
            firstImage = R.mipmap.state2;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            judgeIndex = "1";
            number = 8;
        } else if (vc == 1) {
            state = "弱阳性";
            result = "+-";
            firstImage = R.mipmap.state1;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            judgeIndex = "2";
            number = 7;
        } else if (vc == 2) {
            state = "阳性";
            result = "+";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.state2;
            threeImage = R.mipmap.state2;
            judgeIndex = "3";
            number = 5;
        } else if (vc == 3) {
            state = "阳性";
            result = "++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.state2;
            judgeIndex = "4";
            number = 3;
        } else {
            state = "阳性";
            result = "+++";
            firstImage = R.mipmap.normal;
            secondImage = R.mipmap.normal;
            threeImage = R.mipmap.normal;
            judgeIndex = "5";
            number = 2;
        }

        urineNumber = urineNumber + number;
        statesMap.put(VC, "<font color='#71bd40'>" + state + "  </font><font color='#00a0e9'>" + result + "</font>");
        imagesMap.put(VC, new int[]{firstImage, secondImage, threeImage});
        judgeIndexMap.put(VC, judgeIndex);

    }


    /**
     * 将尿检仪所得到的数据进行判断，将判断的数据封装
     */

    private Map<String, String> handleJudgeData(String resultStr, String stateStr, String judgeStr) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("result", resultStr);
        map.put("state", stateStr);
        map.put("judge", judgeStr);
        return map;
    }

    protected Map<String, ArrayList> handleUrineDataWithJudgeData() {
        Map<String, ArrayList> mapArrayList = new HashMap<String, ArrayList>();
        mapArrayList.put(LEU, handleJudgeDataWithLEU());
        mapArrayList.put(NIT, handleJudgeDataWithNIT());
        mapArrayList.put(UBG, handleJudgeDataWithUBG());
        mapArrayList.put(PRO, handleJudgeDataWithPRO());
        mapArrayList.put(PH, handleJudgeDataWithPH());
        mapArrayList.put(BLD, handleJudgeDataWithBLD());
        mapArrayList.put(SG, handleJudgeDataWithSG());
        mapArrayList.put(KET, handleJudgeDataWithKET());
        mapArrayList.put(BIL, handleJudgeDataWithBIL());
        mapArrayList.put(GLU, handleJudgeDataWithGLU());
        mapArrayList.put(VC, handleJudgeDataWithVC());

        return mapArrayList;

    }

    //白细胞判断的依据
    private ArrayList handleJudgeDataWithLEU() {
        ArrayList<Map> arrayList = new ArrayList<Map>();
        arrayList.add(handleJudgeData("结果", "状态", "判断依据"));
        arrayList.add(handleJudgeData("-", "正常", "≤14 Cell/μl"));
        arrayList.add(handleJudgeData("+-", "弱阳性", "15-69 Cell/μl"));
        arrayList.add(handleJudgeData("+", "阳性", "70-124 Cell/μl"));
        arrayList.add(handleJudgeData("++", "阳性", "125-499 Cell/μl"));
        arrayList.add(handleJudgeData("+++", "阳性", "≥500 Cell/μl"));
        return arrayList;

    }

    //亚硝酸盐判断的依据
    private ArrayList handleJudgeDataWithNIT() {
        ArrayList<Map> arrayList = new ArrayList<Map>();
        arrayList.add(handleJudgeData("结果", "状态", "判断依据"));
        arrayList.add(handleJudgeData("-", "正常", "<1"));
        arrayList.add(handleJudgeData("+", "阳性", "≥1"));
        return arrayList;

    }

    //尿胆原判断的依据
    private ArrayList handleJudgeDataWithUBG() {
        ArrayList<Map> arrayList = new ArrayList<Map>();
        arrayList.add(handleJudgeData("结果", "状态", "判断依据"));
        arrayList.add(handleJudgeData("-", "正常", "0.2-1 mg/dl"));
        arrayList.add(handleJudgeData("+", "阳性", "1.1-2.5 mg/dl"));
        arrayList.add(handleJudgeData("++", "阳性", "2.6-7.9 mg/dl"));
        arrayList.add(handleJudgeData("+++", "阳性", "≥8 mg/dl"));
        return arrayList;

    }

    //尿蛋白判断的依据
    private ArrayList handleJudgeDataWithPRO() {
        ArrayList<Map> arrayList = new ArrayList<Map>();
        arrayList.add(handleJudgeData("结果", "状态", "判断依据"));
        arrayList.add(handleJudgeData("-", "正常", "≤20 mg/dl"));
        arrayList.add(handleJudgeData("+-", "弱阳性", "20-30 mg/dl"));
        arrayList.add(handleJudgeData("+", "阳性", "30-99 mg/dl"));
        arrayList.add(handleJudgeData("++", "阳性", "100-299 mg/dl"));
        arrayList.add(handleJudgeData("+++", "阳性", "300-1999 mg/dl"));
        arrayList.add(handleJudgeData("++++", "阳性", "≥2000 mg/dl"));
        return arrayList;

    }

    //PH判断的依据
    private ArrayList handleJudgeDataWithPH() {
        ArrayList<Map> arrayList = new ArrayList<Map>();
        arrayList.add(handleJudgeData("结果", "无", "判断依据"));
        arrayList.add(handleJudgeData("偏低", "无", "≤5.4"));
        arrayList.add(handleJudgeData("正常", "无", "5.5-7.4"));
        arrayList.add(handleJudgeData("偏高", "无", "≥7.5"));

        return arrayList;
    }

    //潜血判断的依据
    private ArrayList handleJudgeDataWithBLD() {
        ArrayList<Map> arrayList = new ArrayList<Map>();
        arrayList.add(handleJudgeData("结果", "状态", "判断依据"));
        arrayList.add(handleJudgeData("-", "正常", "≤9 Cell/μl"));
        arrayList.add(handleJudgeData("+-", "弱阳性", "10-24 Cell/μl"));
        arrayList.add(handleJudgeData("+", "阳性", "25-79 Cell/μl"));
        arrayList.add(handleJudgeData("++", "阳性", "80-199 Cell/μl"));
        arrayList.add(handleJudgeData("+++", "阳性", "≥200 Cell/μl"));
        return arrayList;

    }

    //比重判断的依据
    private ArrayList handleJudgeDataWithSG() {
        ArrayList<Map> arrayList = new ArrayList<Map>();
        arrayList.add(handleJudgeData("结果", "无", "判断依据"));
        arrayList.add(handleJudgeData("偏低", "无", "≤1.014"));
        arrayList.add(handleJudgeData("正常", "无", "1.015-1.025"));
        arrayList.add(handleJudgeData("偏高", "无", "≥1.026"));
        return arrayList;

    }

    // 酮体判断的依据
    private ArrayList handleJudgeDataWithKET() {
        ArrayList<Map> arrayList = new ArrayList<Map>();
        arrayList.add(handleJudgeData("结果", "状态", "判断依据"));
        arrayList.add(handleJudgeData("-", "正常", "≤4 mg/dl"));
        arrayList.add(handleJudgeData("+-", "弱阳性", "5-14 mg/dl"));
        arrayList.add(handleJudgeData("+", "阳性", "15-39 mg/dl"));
        arrayList.add(handleJudgeData("++", "阳性", "40-79 mg/dl"));
        arrayList.add(handleJudgeData("+++", "阳性", "80-159 mg/dl"));
        arrayList.add(handleJudgeData("++++", "阳性", "≥160 mg/dl"));
        return arrayList;

    }

    //胆红素判断的依据
    private ArrayList handleJudgeDataWithBIL() {
        ArrayList<Map> arrayList = new ArrayList<Map>();
        arrayList.add(handleJudgeData("结果", "状态", "判断依据"));
        arrayList.add(handleJudgeData("-", "正常", "<1 mg/dl"));
        arrayList.add(handleJudgeData("+", "阳性", "1-2 mg/dl"));
        arrayList.add(handleJudgeData("++", "阳性", "3-5 mg/dl"));
        arrayList.add(handleJudgeData("+++", "阳性", "≥6 mg/dl"));
        return arrayList;

    }

    //葡萄糖判断的依据
    private ArrayList handleJudgeDataWithGLU() {
        ArrayList<Map> arrayList = new ArrayList<Map>();
        arrayList.add(handleJudgeData("结果", "状态", "判断依据"));
        arrayList.add(handleJudgeData("-", "正常", "≤99 mg/dl"));
        arrayList.add(handleJudgeData("+-", "弱阳性", "100-249 mg/dl"));
        arrayList.add(handleJudgeData("+", "阳性", "250-499 mg/dl"));
        arrayList.add(handleJudgeData("++", "阳性", "500-999 mg/dl"));
        arrayList.add(handleJudgeData("+++", "阳性", "1000-2000 mg/dl"));
        arrayList.add(handleJudgeData("++++", "阳性", ">2000 mg/dl"));
        return arrayList;

    }

    //VC判断的依据
    private ArrayList handleJudgeDataWithVC() {
        ArrayList<Map> arrayList = new ArrayList<Map>();
        arrayList.add(handleJudgeData("结果", "状态", "判断依据"));
        arrayList.add(handleJudgeData("-", "正常", "≤0.5 mmol/l"));
        arrayList.add(handleJudgeData("+-", "弱阳性", "0.6-1.3 mmol/l"));
        arrayList.add(handleJudgeData("+", "阳性", "1.4-2.7 mmol/l"));
        arrayList.add(handleJudgeData("++", "阳性", "2.8-5.5 mmol/l"));
        arrayList.add(handleJudgeData("+++", "阳性", "≥5.6 mmol/l"));
        return arrayList;

    }
}
