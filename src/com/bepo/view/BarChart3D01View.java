package com.bepo.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.xclcharts.chart.BarChart3D;
import org.xclcharts.chart.BarData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterDoubleCallBack;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.BarPosition;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bepo.core.ApplicationController;
import com.bepo.core.DemoView;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;

public class BarChart3D01View extends DemoView {

	private String TAG = "Bar3DChart01View";
	private BarChart3D chart = new BarChart3D();

	// 标签轴
	private List<String> chartLabels = new LinkedList<String>();
	// 数据轴
	private List<BarData> BarDataset = new LinkedList<BarData>();

	Paint mPaintToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);

	public BarChart3D01View(Context context) {
		super(context);
		initView();
	}

	public BarChart3D01View(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public BarChart3D01View(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		chartLabels();
		chartDataSet();
		chartRender();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 图所占范围大小
		chart.setChartRange(w, h);
	}

	private void chartRender() {
		try {

			// 设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			int[] ltrb = getBarLnDefaultSpadding();
			chart.setPadding(DensityUtil.dip2px(getContext(), 50), ltrb[1], ltrb[2], ltrb[3]);

			// 显示边框
			// chart.showRoundBorder();

			// 数据源
			chart.setDataSource(BarDataset);
			chart.setCategories(chartLabels);

			// 坐标系

			chart.getDataAxis().setAxisMin(0);
			chart.getDataAxis().setAxisSteps(250);
			// chart.getCategoryAxis().setAxisTickLabelsRotateAngle(-45f);

			// 隐藏轴线和tick
			chart.getDataAxis().hideAxisLine();
			chart.getDataAxis().hideTickMarks();// 隐藏标注线
			// chart.getDataAxis().setTickMarksVisible(false);

			// 标题
			// chart.setTitle("人口统计");
			// chart.addSubtitle("(XCL-Charts Demo)");
			chart.setTitleVerticalAlign(XEnum.VerticalAlign.MIDDLE);

			// 背景网格
			// chart.getPlotGrid().showHorizontalLines();
			// chart.getPlotGrid().showVerticalLines();
			chart.getPlotGrid().showEvenRowBgColor();
			chart.getPlotGrid().showOddRowBgColor();

			// 定义数据轴标签显示格式
			// chart.getDataAxis().setTickLabelRotateAngle(-45);
			chart.getDataAxis().getTickMarksPaint().setColor(Color.rgb(155, 144, 206));
			chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack() {

				@Override
				public String textFormatter(String value) {
					Double tmp = Double.parseDouble(value);
					DecimalFormat df = new DecimalFormat("#0");
					String label = df.format(tmp).toString();
					return (label + "人");
				}

			});

			// 定义标签轴标签显示格式
			chart.getCategoryAxis().setLabelFormatter(new IFormatterTextCallBack() {

				@Override
				public String textFormatter(String value) {
					return value;
				}

			});
			// 定义柱形上标签显示格式
			chart.getBar().setItemLabelVisible(true);
			chart.setItemLabelFormatter(new IFormatterDoubleCallBack() {
				@Override
				public String doubleFormatter(Double value) {
					DecimalFormat df = new DecimalFormat("#0");
					String label = df.format(value).toString();
					return label;
				}
			});

			// 激活点击监听
			// chart.ActiveListenItemClick();

			// 仅能横向移动
			chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

			// 扩展横向显示范围
			// chart.getPlotArea().extWidth(200f);

			// 标签文字与轴间距
			chart.getCategoryAxis().setTickLabelMargin(5);

			// 不使用精确计算，忽略Java计算误差
			chart.disableHighPrecision();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void chartDataSet() {
		// 标签对应的柱形数据集
		List<Double> dataSeriesA = new LinkedList<Double>();
		Double max = 0.0;
		for (String s : PathConfig.ydata) {
			dataSeriesA.add(Double.valueOf(s));
			if (Double.valueOf(s) > max) {
				max = Double.valueOf(s);
			}

		}
		max = max + 250;
		chart.getDataAxis().setAxisMax(max);

		// List<Double> dataSeriesB = new LinkedList<Double>();
		// dataSeriesB.add(300d);
		// dataSeriesB.add(150d);
		// dataSeriesB.add(450d);
		// dataSeriesB.add(480d);
		// dataSeriesB.add(200d);

		BarDataset.add(new BarData("人口统计", dataSeriesA, Color.rgb(155, 144, 206)));
		// BarDataset.add(new BarData("右边店", dataSeriesB, Color.rgb(55, 144,
		// 206)));

		// List<Double> dataSeriesC = new LinkedList<Double>();
		// dataSeriesC.add(270d);
		// dataSeriesC.add(180d);
		// dataSeriesC.add(450d);
		// dataSeriesC.add(380d);
		// dataSeriesC.add(230d);
		// BarDataset.add(new BarData("右边店2", dataSeriesC, Color.rgb(155, 144,
		// 206)));
	}

	private void chartLabels() {

		chartLabels = PathConfig.xdata;

		// chartLabels.add("桃子(Peach)");
		// chartLabels.add("梨子(Pear)");
		// chartLabels.add("香蕉 (Banana)");
		// chartLabels.add("苹果");
		// chartLabels.add("桔子");
	}

	@Override
	public void render(Canvas canvas) {
		try {
			chart.render(canvas);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public List<XChart> bindChart() {
		List<XChart> lst = new ArrayList<XChart>();
		lst.add(chart);
		return lst;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			triggerClick(event.getX(), event.getY());
		}
		return true;
	}

	// 触发监听
	private void triggerClick(float x, float y) {
		if (!chart.getListenItemClickStatus())
			return;

		BarPosition record = chart.getPositionRecord(x, y);
		if (null == record)
			return;

		BarData bData = BarDataset.get(record.getDataID());
		Double bValue = bData.getDataSet().get(record.getDataChildID());

		// 在点击处显示tooltip
		mPaintToolTip.setColor(Color.WHITE);
		chart.getToolTip().getBackgroundPaint().setColor(Color.rgb(75, 202, 255));
		chart.getToolTip().getBorderPaint().setColor(Color.RED);
		chart.getToolTip().setCurrentXY(x, y);
		chart.getToolTip().addToolTip(" Current Value:" + Double.toString(bValue), mPaintToolTip);
		chart.getToolTip().getBackgroundPaint().setAlpha(100);
		this.invalidate();
	}

}
