package com.bepo.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.info.AnchorDataPoint;
import org.xclcharts.renderer.plot.PlotGrid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.bepo.core.DemoView;

public class SplineChart01View extends DemoView {

	private String TAG = "SplineChart01View";
	private SplineChart chart = new SplineChart();
	// 分类轴标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<SplineData> chartData = new LinkedList<SplineData>();
	Paint pToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);

	public SplineChart01View(Context context) {
		super(context);
		initView();
	}

	public SplineChart01View(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public SplineChart01View(Context context, AttributeSet attrs, int defStyle) {
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
		chart.setChartRange((float) (w), h);
	}

	private void chartRender() {
		try {

			chart.getPlotLegend().hide();
			chart.disableScale();// 禁止缩放
			chart.disablePanMode();// 禁止滑动
			// 设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			int[] ltrb = getBarLnDefaultSpadding();
			chart.setPadding(ltrb[0], 50, ltrb[2], 60);

			// 平移时收缩下
			// float margin = DensityUtil.dip2px(getContext(), 20);
			// chart.setXTickMarksOffsetMargin(margin);

			// 显示边框
			// chart.showRoundBorder();

			// 数据源
			chart.setCategories(labels);
			chart.setDataSource(chartData);

			// 轴标题
			// chart.getAxisTitle().setLeftTitle("提交个数");
			// chart.getAxisTitle().setLowerTitle("时间");

			// 坐标系
			chart.getDataAxis().setAxisMax(5);// 数据轴最大值
			chart.getDataAxis().setAxisMin(0);// 数据轴最小值
			chart.getDataAxis().setAxisSteps(1);// 数据轴刻度间隔
			chart.getDataAxis().hideAxisLine();// 隐藏线
			chart.getDataAxis().hideTickMarks();// 隐藏标注线
			// chart.getDataAxis().hideAxisLabels();// 隐藏标签

			chart.setCategoryAxisMax(7);// 标签轴最大值
			chart.setCategoryAxisMin(1);// 标签轴最小值
			chart.getCategoryAxis().setAxisSteps(1);
			// chart.getCategoryAxis().hideAxisLine();
			chart.getCategoryAxis().hideTickMarks();
			// chart.getCategoryAxis().hideAxisLabels();

			// 设置图的背景色
			// chart.setBackgroupColor(true,Color.BLACK);
			// 设置绘图区的背景色
			// chart.getPlotArea().setBackgroupColor(true, Color.WHITE);

			// 背景网格
			PlotGrid plot = chart.getPlotGrid();
			plot.showHorizontalLines();
			// plot.showVerticalLines();
			plot.getHorizontalLinePaint().setStrokeWidth(1);
			plot.getHorizontalLinePaint().setColor(Color.rgb(127, 204, 204));
			plot.setHorizontalLineStyle(XEnum.LineStyle.SOLID);

			// 把轴线设成和横向网络线一样和大小和颜色,演示下定制性，这块问得人较多
			// chart.getDataAxis().getAxisPaint().setStrokeWidth(plot.getHorizontalLinePaint().getStrokeWidth());
			chart.getCategoryAxis().getAxisPaint().setStrokeWidth(plot.getHorizontalLinePaint().getStrokeWidth());

			// chart.getDataAxis().getAxisPaint().setColor(Color.rgb(127, 204,
			// 204));
			chart.getCategoryAxis().getAxisPaint().setColor(Color.rgb(127, 204, 204));

			// chart.getDataAxis().getTickMarksPaint().setColor(Color.rgb(127,
			// 204, 204));
			chart.getCategoryAxis().getTickMarksPaint().setColor(Color.rgb(127, 204, 204));

			chart.getDataAxis().setHorizontalTickAlign(Align.LEFT);
			chart.getDataAxis().getTickLabelPaint().setTextAlign(Align.LEFT);
			// chart.getDataAxis().getAxisPaint().setTextSize(350);
			chart.getDataAxis().setTickLabelMargin(50);// 标签和y轴的距离
			chart.getCategoryAxis().setTickLabelMargin(30);// 标签和x轴的距离

			// 居中显示轴
			// plot.hideHorizontalLines();
			// plot.hideVerticalLines();
			// chart.setDataAxisLocation(XEnum.AxisLocation.VERTICAL_CENTER);
			// chart.setCategoryAxisLocation(XEnum.AxisLocation.HORIZONTAL_CENTER);

			// 定义交叉点标签显示格式,特别备注,因曲线图的特殊性，所以返回格式为: x值,y值
			// 请自行分析定制
			chart.setDotLabelFormatter(new IFormatterTextCallBack() {

				@Override
				public String textFormatter(String value) {
					String label = "(" + value + ")";
					return (label);
				}

			});
			// 标题
			// chart.setTitle("近7日统计量(日期/个数)");
			// chart.addSubtitle("(XCL-Charts Demo)");

			// 激活点击监听
			// chart.ActiveListenItemClick();
			// 为了让触发更灵敏，可以扩大5px的点击监听范围
			chart.extPointClickRange(5);
			chart.showClikedFocus();

			// 显示十字交叉线
			// chart.showDyLine();
			chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Vertical);
			// 扩大实际绘制宽度
			// chart.getPlotArea().extWidth(500.f);

			// 封闭轴
			// chart.setAxesClosed(true);

			// 将线显示为直线，而不是平滑的
			chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEELINE);

			// 不使用精确计算，忽略Java计算误差,提高性能
			chart.disableHighPrecision();

			// 批注
			List<AnchorDataPoint> mAnchorSet = new ArrayList<AnchorDataPoint>();

			AnchorDataPoint an1 = new AnchorDataPoint(2, 0, XEnum.AnchorStyle.CIRCLE);
			an1.setAlpha(200);
			an1.setBgColor(Color.RED);
			an1.setAreaStyle(XEnum.DataAreaStyle.FILL);

			AnchorDataPoint an2 = new AnchorDataPoint(1, 1, XEnum.AnchorStyle.CIRCLE);
			an2.setBgColor(Color.GRAY);

			AnchorDataPoint an3 = new AnchorDataPoint(0, 2, XEnum.AnchorStyle.RECT);
			an3.setBgColor(Color.BLUE);

			mAnchorSet.add(an1);
			mAnchorSet.add(an2);
			mAnchorSet.add(an3);
			// chart.setAnchorDataPoint(mAnchorSet);

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
	}

	private void chartDataSet() {
		/*
		 * //线1的数据集 List<PointD> linePoint1 = new ArrayList<PointD>();
		 * linePoint1.add(new PointD(5d, 8d));
		 * 
		 * linePoint1.add(new PointD(12d, 12d)); linePoint1.add(new PointD(25d,
		 * 15d)); linePoint1.add(new PointD(30d, 30d)); linePoint1.add(new
		 * PointD(45d, 25d));
		 * 
		 * linePoint1.add(new PointD(55d, 33d)); linePoint1.add(new PointD(62d,
		 * 45d)); SplineData dataSeries1 = new SplineData("青菜萝卜够吃",linePoint1,
		 * Color.rgb(54, 141, 238) ); //把线弄细点
		 * dataSeries1.getLinePaint().setStrokeWidth(2);
		 * 
		 * //线2的数据集 List<PointD> linePoint2 = new ArrayList<PointD>();
		 * linePoint2.add(new PointD(40d, 50d)); linePoint2.add(new PointD(55d,
		 * 55d)); linePoint2.add(new PointD(60d, 65d)); linePoint2.add(new
		 * PointD(65d, 85d));
		 * 
		 * linePoint2.add(new PointD(72d, 70d)); linePoint2.add(new PointD(85d,
		 * 68d)); SplineData dataSeries2 = new SplineData("饭管够",linePoint2,
		 * Color.rgb(255, 165, 132) );
		 * 
		 * 
		 * dataSeries2.setLabelVisible(true);
		 * dataSeries2.setDotStyle(XEnum.DotStyle.RECT);
		 * dataSeries2.getDotLabelPaint().setColor(Color.RED);
		 * 
		 * //设定数据源 chartData.add(dataSeries1); chartData.add(dataSeries2);
		 */

		// 线1的数据集
		List<PointD> linePoint1 = new ArrayList<PointD>();

		linePoint1.add(new PointD(1, 0));
		linePoint1.add(new PointD(2, 1));
		linePoint1.add(new PointD(3, 2));
		linePoint1.add(new PointD(4, 1));
		linePoint1.add(new PointD(5, 5));
		linePoint1.add(new PointD(6, 3));
		linePoint1.add(new PointD(7, 4));

		SplineData dataSeries1 = new SplineData("近7日统计量(日期/个数)", linePoint1, Color.rgb(255, 165, 132));

		dataSeries1.getLinePaint().setStrokeWidth(5); // 把线弄细点
		dataSeries1.setDotStyle(XEnum.DotStyle.RING);

		// 线2的数据集
		// List<PointD> linePoint2 = new ArrayList<PointD>();
		// linePoint2.add(new PointD(1d, 50d));
		// linePoint2.add(new PointD(2d, 52d));
		// linePoint2.add(new PointD(3d, 53d));
		// linePoint2.add(new PointD(8d, 55d));
		// SplineData dataSeries2 = new SplineData("饭管够", linePoint2,
		// Color.rgb(255, 165, 132));
		//
		// dataSeries2.setLabelVisible(true);
		// dataSeries2.setDotStyle(XEnum.DotStyle.RECT);
		// dataSeries2.getDotLabelPaint().setColor(Color.RED);
		//
		// // 设置round风格的标签
		// // dataSeries2.getLabelOptions().showBackground();
		// dataSeries2.getLabelOptions().getBox().getBackgroundPaint().setColor(Color.GREEN);
		// dataSeries2.getLabelOptions().getBox().setRoundRadius(8);
		// dataSeries2.getLabelOptions().setLabelBoxStyle(XEnum.LabelBoxStyle.RECT);

		chartData.add(dataSeries1);
		// chartData.add(dataSeries2);
	}

	private void chartLabels() {
		labels.add("");
		labels.add("2");
		labels.add("3");
		labels.add("4");
		labels.add("5");
		labels.add("6");
		labels.add("7");

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
		// 交叉线
		if (chart.getDyLineVisible())
			chart.getDyLine().setCurrentXY(x, y);
		if (!chart.getListenItemClickStatus()) {
			if (chart.getDyLineVisible() && chart.getDyLine().isInvalidate())
				this.invalidate();
		} else {
			PointPosition record = chart.getPositionRecord(x, y);
			if (null == record)
				return;

			if (record.getDataID() >= chartData.size())
				return;
			SplineData lData = chartData.get(record.getDataID());
			List<PointD> linePoint = lData.getLineDataSet();
			int pos = record.getDataChildID();
			int i = 0;
			Iterator it = linePoint.iterator();
			while (it.hasNext()) {
				PointD entry = (PointD) it.next();

				if (pos == i) {
					Double xValue = entry.x;
					Double yValue = entry.y;

					float r = record.getRadius();
					chart.showFocusPointF(record.getPosition(), r * 2);
					chart.getFocusPaint().setStyle(Style.STROKE);
					chart.getFocusPaint().setStrokeWidth(3);
					if (record.getDataID() >= 2) {
						chart.getFocusPaint().setColor(Color.BLUE);
					} else {
						chart.getFocusPaint().setColor(Color.RED);
					}

					// 在点击处显示tooltip
					pToolTip.setColor(Color.RED);
					chart.getToolTip().setCurrentXY(x, y);
					chart.getToolTip().addToolTip(" Key:" + lData.getLineKey(), pToolTip);
					chart.getToolTip().addToolTip(" Label:" + lData.getLabel(), pToolTip);
					chart.getToolTip().addToolTip(
							" Current Value:" + Double.toString(xValue) + "," + Double.toString(yValue), pToolTip);
					chart.getToolTip().getBackgroundPaint().setAlpha(100);
					this.invalidate();

					break;
				}
				i++;
			}// end while
		}
	}

}
