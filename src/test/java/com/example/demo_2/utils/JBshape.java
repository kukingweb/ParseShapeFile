package com.example.demo_2.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo_2.entity.Coord;
import com.example.demo_2.jsonEntity.Shp;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JBshape {
    public static void main(String[] args) throws IOException {

        ArrayList<Coord> coords = new ArrayList<>();
        long start = System.currentTimeMillis();

        String SHAPE_FILE = "C:\\Users\\www28\\Desktop\\shape\\4326-沉积相.shp"; // ShapeFile全路径

        // 使用GeoTools读取ShapeFile文件
        File shapeFile = new File(SHAPE_FILE);
        ShapefileDataStore store = new ShapefileDataStore(shapeFile.toURI().toURL());
        //设置编码
        Charset charset = StandardCharsets.UTF_8;
        store.setCharset(charset);
        SimpleFeatureSource sfSource = store.getFeatureSource();
        SimpleFeatureIterator sfIter = sfSource.getFeatures().features();
        // 从ShapeFile文件中遍历每一个Feature，然后将Feature转为GeoJSON字符串
        while (sfIter.hasNext()) {
            SimpleFeature feature = sfIter.next();
            // Feature转GeoJSON
            FeatureJSON fjson = new FeatureJSON();
            StringWriter writer = new StringWriter();
            fjson.writeFeature(feature, writer);
            String sjson = writer.toString();

            Shp shp = JSONObject.parseObject(sjson, Shp.class);
            List<List<List<List<Double>>>> coordinates = shp.getGeometry().getCoordinates();
            List<List<Double>> lists = coordinates.get(0).get(0);
            for (List<Double> list : lists) {
                Coord coord = new Coord();
                coord.setX(list.get(0));
                coord.setY(list.get(1));
                coords.add(coord);
            }
//            System.out.println("doubles.size() = " + doubles.size());
//            System.out.println(coordinates);
        }
        System.out.println("coords = " + coords);
        System.out.println("数据导入完成，共耗时"+(System.currentTimeMillis() - start)+"ms");
    }
}
