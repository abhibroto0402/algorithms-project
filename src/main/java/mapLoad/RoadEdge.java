package mapLoad;

/**
 * Created by amukherjee on 4/24/17.

 */

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
