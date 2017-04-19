package mapLoad;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiLineString;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

/**
 * Prompts the user for a shapefile and displays the contents on the screen in a map frame.
 * <p>
 * This is the GeoTools Quickstart application used in documentationa and tutorials. *
 */
public class MapLoad {

    /**
     * GeoTools Quickstart demo application. Prompts the user for a shapefile and displays its
     * contents on the screen in a map frame
     */
    public static void main(String[] args) throws Exception {
    	System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
    	HashMap coordMap = new HashMap(); 
        // display a data store file chooser dialog for shapefiles
        File file = new File("/Users/amukherjee/Downloads/Street Segment/geo_export_0efab099-a752-486c-abd3-46487f495781.shp");
		if (file == null) {
			return;
		}

		List<RoadEdge> edgeList = new ArrayList();

		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();

		// Create a map content and add our shapefile to it
		MapContent map = new MapContent();
		map.setTitle("Quickstart");

		Style style = SLD.createSimpleStyle(featureSource.getSchema());
		Layer layer = new FeatureLayer(featureSource, style);
		map.addLayer(layer);

		SimpleFeatureCollection streetCollection = featureSource.getFeatures();

		SimpleFeatureType streetTypes = streetCollection.getSchema();

		//System.out.println(streetTypes.toString());

		SimpleFeatureIterator streetIterator = streetCollection.features();

		int pointID = 0;
		int pointCount = 0;

		//approxmate location of capitol building
		Coordinate centerCoord = new Coordinate(-97.7392, 30.2747 );


		while (streetIterator.hasNext() ){

			SimpleFeature streetSegment = streetIterator.next();
			System.out.println(streetSegment.getAttribute("street_typ"));
			MultiLineString defGeom = (MultiLineString) streetSegment.getDefaultGeometry();
			float road_class = Float.parseFloat(streetSegment.getAttribute("road_class").toString());
			if (road_class<=8.0 ){
				com.vividsolutions.jts.geom.Geometry geom;

				Coordinate[] coordList = defGeom.getCoordinates();

				Coordinate lastCoord = null;

				for (Coordinate temp: coordList){
					//System.out.println(temp.toString());
					//System.out.println("Distance: " +temp.distance(centerCoord));

					if(temp.distance(centerCoord) > 0.009 && lastCoord == null){
						break;
					}
					pointCount++;
					if(!coordMap.containsKey(temp)){
						pointID++;
						coordMap.put(temp, pointID);
						if (pointID < 10){
							System.out.println("Point: "+temp.toString() );
						}
					}
					if (lastCoord == null){

						lastCoord = temp;
					}else {
						double distance = temp.distance(lastCoord);
						//System.out.println("Distance: "+temp.distance(lastCoord));
						int startID = (Integer) coordMap.get(lastCoord);
						int stopID = (Integer) coordMap.get(temp);
						edgeList.add(new RoadEdge(startID, stopID, distance,road_class));

					}

				}
			}

		}

		map.dispose();

		System.out.println("Distinct Coordinates: "+coordMap.size() );
		System.out.println("Total Coordinates: "+pointCount );
		System.out.println("Edges: "+edgeList.size());

		Graph roadGraph = new MultiGraph("AustinRoads");
		String styleSheet = "graph {} edge.roadClass1{fill-color: Red;} edge.roadClass2{fill-color: Orange;} "
				+ "edge.roadClass3{fill-color: Yellow;} edge.roadClass4{fill-color: Blue;}";
		roadGraph.addAttribute("ui.stylesheet", styleSheet);
		for(int i = 1; i <= coordMap.size(); i++){
			roadGraph.addNode( Integer.toString(i) );
		}
		int edgeID = 0;
		for(RoadEdge temp: edgeList){
			edgeID++;
			roadGraph.addEdge(Integer.toString(edgeID) , Integer.toString(temp.start), Integer.toString(temp.stop));
			String roadClass =  "roadClass1";
			if( temp.road_class == 1.0){
				roadClass = "roadClass1";
				System.out.println("set Red");
			} else if( temp.road_class == 2.0){
				roadClass = "roadClass2";
				System.out.println("set Orange");
			} else if( temp.road_class == 3.0){
				roadClass = "roadClass3";
				System.out.println("set Yellow");
			} else if( temp.road_class == 4.0){
				roadClass = "roadClass4";
				System.out.println("set blue");
			} else if( temp.road_class == 5.0){
				roadClass = "roadClass5";
				System.out.println("set Green");
			} else if( temp.road_class == 6.0){
				roadClass = "roadClass6";
				System.out.println("set Cyan");
			} else if( temp.road_class == 7.0){
				roadClass = "roadClass7";
				System.out.println("set Magenta");
			}
			roadGraph.getEdge(Integer.toString(edgeID)).addAttribute("ui.class", roadClass);
		}

		roadGraph.display();

		// Now display the map
		//JMapFrame.showMap(map);



	}

}

class RoadEdge{
	int start;
	int stop;
	double distance;
	float road_class; 
	
	RoadEdge(int strt, int stop, double dist, float road_class){
		this.start = strt;
		this.stop = stop;
		this.distance = dist;
		this.road_class = road_class;
	}
}