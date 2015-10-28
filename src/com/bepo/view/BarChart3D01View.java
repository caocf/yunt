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

	// ��ǩ��
	private List<String> chartLabels = new LinkedList<String>();
	// ������
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
		// ͼ��ռ��Χ��С
		chart.setChartRange(w, h);
	}

	private void chartRender() {
		try {

			// ���û�ͼ��Ĭ������pxֵ,���ÿռ���ʾAxis,Axistitle....
			int[] ltrb = getBarLnDefaultSpadding();
			chart.setPadding(DensityUtil.dip2px(getContext(), 50), ltrb[1], ltrb[2], ltrb[3]);

			// ��ʾ�߿�
			// chart.showRoundBorder();

			// ����Դ
			chart.setDataSource(BarDataset);
			chart.setCategories(chartLabels);

			// ����ϵ

			chart.getDataAxis().setAxisMin(0);
			chart.getDataAxis().setAxisSteps(250);
			// chart.getCategoryAxis().setAxisTickLabelsRotateAngle(-45f);

			// �������ߺ�tick
			chart.getDataAxis().hideAxisLine();
			chart.getDataAxis().hideTickMarks();// ���ر�ע��
			// chart.getDataAxis().setTickMarksVisible(false);

			// ����
			// chart.setTitle("�˿�ͳ��");
			// chart.addSubtitle("(XCL-Charts Demo)");
			chart.setTitleVerticalAlign(XEnum.VerticalAlign.MIDDLE);

			// ��������
			// chart.getPlotGrid().showHorizontalLines();
			// chart.getPlotGrid().showVerticalLines();
			chart.getPlotGrid().showEvenRowBgColor();
			chart.getPlotGrid().showOddRowBgColor();

			// �����������ǩ��ʾ��ʽ
			// chart.getDataAxis().setTickLabelRotateAngle(-45);
			chart.getDataAxis().getTickMarksPaint().setColor(Color.rgb(155, 144, 206));
			chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack() {

				@Override
				public String textFormatter(String value) {
					Double tmp = Double.parseDouble(value);
					DecimalFormat df = new DecimalFormat("#0");
					String label = df.format(tmp).toString();
					return (label + "��");
				}

			});

			// �����ǩ���ǩ��ʾ��ʽ
			chart.getCategoryAxis().setLabelFormatter(new IFormatterTextCallBack() {

				@Override
				public String textFormatter(String value) {
					return value;
				}

			});
			// ���������ϱ�ǩ��ʾ��ʽ
			chart.getBar().setItemLabelVisible(true);
			chart.setItemLabelFormatter(new IFormatterDoubleCallBack() {
				@Override
				public String doubleFormatter(Double value) {
					DecimalFormat df = new DecimalFormat("#0");
					String label = df.format(value).toString();
					return label;
				}
			});

			// ����������
			// chart.ActiveListenItemClick();

			// ���ܺ����ƶ�
			chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

			// ��չ������ʾ��Χ
			// chart.getPlotArea().extWidth(200f);

			// ��ǩ����������
			chart.getCategoryAxis().setTickLabelMargin(5);

			// ��ʹ�þ�ȷ���㣬����Java�������
			chart.disableHighPrecision();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void chartDataSet() {
		// ��ǩ��Ӧ���������ݼ�
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

		BarDataset.add(new BarData("�˿�ͳ��", dataSeriesA, Color.rgb(155, 144, 206)));
		// BarDataset.add(new BarData("�ұߵ�", dataSeriesB, Color.rgb(55, 144,
		// 206)));

		// List<Double> dataSeriesC = new LinkedList<Double>();
		// dataSeriesC.add(270d);
		// dataSeriesC.add(180d);
		// dataSeriesC.add(450d);
		// dataSeriesC.add(380d);
		// dataSeriesC.add(230d);
		// BarDataset.add(new BarData("�ұߵ�2", dataSeriesC, Color.rgb(155, 144,
		// 206)));
	}

	private void chartLabels() {

		chartLabels = PathConfig.xdata;

		// chartLabels.add("����(Peach)");
		// chartLabels.add("����(Pear)");
		// chartLabels.add("�㽶 (Banana)");
		// chartLabels.add("ƻ��");
		// chartLabels.add("����");
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

	// ��������
	private void triggerClick(float x, float y) {
		if (!chart.getListenItemClickStatus())
			return;

		BarPosition record = chart.getPositionRecord(x, y);
		if (null == record)
			return;

		BarData bData = BarDataset.get(record.getDataID());
		Double bValue = bData.getDataSet().get(record.getDataChildID());

		// �ڵ������ʾtooltip
		mPaintToolTip.setColor(Color.WHITE);
		chart.getToolTip().getBackgroundPaint().setColor(Color.rgb(75, 202, 255));
		chart.getToolTip().getBorderPaint().setColor(Color.RED);
		chart.getToolTip().setCurrentXY(x, y);
		chart.getToolTip().addToolTip(" Current Value:" + Double.toString(bValue), mPaintToolTip);
		chart.getToolTip().getBackgroundPaint().setAlpha(100);
		this.invalidate();
	}

}
