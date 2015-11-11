package mymapapplication.miguel.labrador.com.deadlifthelper;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.GraphicalView;


public class ChartMaker {

    /*****************************/
    /*****Global Variables******/
    /***************************/

    /*****************************/
    /*****Private Variables******/
    /***************************/

    //Step 1: Dataset & type of view
    // XYSeries indicates that it is a line chart.
    private XYSeries dataSet;

    //Step 1.1: XYSeries -> XYMultipleSeriesDataset.
    private XYMultipleSeriesDataset finalDataSet;

    //Step 2.1 : renderer to control how the data is drawn.
    //This contains one series worth of data (one line).
    private XYSeriesRenderer dataRender;

    //Ste 2.2: renderer to control how the view is drawn.
    //This adds multiple (or one) series worth of data into one graph.
    private XYMultipleSeriesRenderer chartRender;

    //Step 3: use a chart factory to combine the data and the renderer.
    private GraphicalView actualChartView;

    //Used to smooth the chart.
    private float smoothingFactor;


    /*****************************/
    /*Helper & Private Functions*/
    /***************************/

    /*****************************/
    /******Public Functions******/
    /***************************/

    //Function to repaint the chart.
    public void clearChart()
    {
        actualChartView.repaint();
    }


    //Function to return whether or not the chart is null at the moment.
    public boolean isNullChart()
    {
        if(actualChartView == null)
            return true;
        else
            return false;
    }


    //Function to add one record of data to the dataset.
    public void addSinglePoint(float x_value, float y_value)
    {
        dataSet.add(x_value, y_value);
    }

    //Function to add multiple records of data to the dataset at once.
    //Each pair of points is contained within its own float array.
    //If these two array do not have the same number of elements, return false.
    //Otherwise, return true.
    public boolean addMultiplePoints(float[] x_values, float[] y_values)
    {
        //Make sure that they are same length, otherwise something is screwey.
        if(x_values.length != y_values.length)
            return false;

        //Add each pair to dataset, one at a time.
        for(int c = 0; c < x_values.length; c++)
        {
            dataSet.add(x_values[c], y_values[c]);
        }

        return true;
    }


    //Function to get the chartView of the chart created in this class.
    //dataSet cannot be null, so we check that the user has added some data to this
    //chart before we attempt to create it. If no data, print a message and return.
    public GraphicalView getChartView(Context context)
    {
        if(dataSet == null || finalDataSet == null){
            //Error message.
            Log.i("chart_error", "Data set cannot be null. Use addSinglePoint or addMultiplePoints " +
                    "to add data to the dataSet before calling this function.");
            return null;
        }

        actualChartView = ChartFactory.getCubeLineChartView(context, finalDataSet, chartRender, smoothingFactor);
        return actualChartView;
    }


    //Function to remove all data from our dataSet.
    //Thus, we can create a fresh, new chart.
    public void removeAllData()
    {
        dataSet.clear();
    }

    /*****************************/
    /*****Lifecycle Functions****/
    /***************************/

    //Default constructor initializes the dataSet, but does not add data to it.
    //Also initialize renderer's appearances but still do not add data.
    ChartMaker(String chartName)
    {
        /****Initialize Datasets****/
        dataSet = new XYSeries(chartName);
        finalDataSet = new XYMultipleSeriesDataset();
        finalDataSet.addSeries(dataSet);        //Associate dataSet with finalDataSet.

        /****Initialize data renderer and format how data is drawn****/
        dataRender = new XYSeriesRenderer();
        //Formatting
        dataRender.setLineWidth(2);
        dataRender.setColor(Color.RED);
        dataRender.setDisplayBoundingPoints(true); //Display low and max values.

        /****Initialize chart renderer and format how chart is drawn****/
        chartRender = new XYMultipleSeriesRenderer();
        chartRender.addSeriesRenderer(dataRender);
        //Formatting.
        chartRender.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));    //Transparent margins.
        chartRender.setXAxisMax(20);    //Length of Xaxis.
        chartRender.setXAxisMax(20);    //Length of Yaxis.
    }


    ChartMaker(String chartName, float smoothness)
    {
        /****Initialize Datasets****/
        dataSet = new XYSeries(chartName);
        finalDataSet = new XYMultipleSeriesDataset();
        finalDataSet.addSeries(dataSet);        //Associate dataSet with finalDataSet.

        /****Initialize data renderer and format how data is drawn****/
        dataRender = new XYSeriesRenderer();
        //Formatting
        dataRender.setLineWidth(2);
        dataRender.setColor(Color.RED);
        dataRender.setDisplayBoundingPoints(true); //Display low and max values.

        /****Initialize chart renderer and format how chart is drawn****/
        chartRender = new XYMultipleSeriesRenderer();
        chartRender.addSeriesRenderer(dataRender);
        //Formatting.
        chartRender.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));    //Transparent margins.
        chartRender.setXAxisMax(20);    //Length of Xaxis.
        chartRender.setXAxisMax(20);    //Length of Yaxis.

        smoothingFactor = smoothness;
    }
}
