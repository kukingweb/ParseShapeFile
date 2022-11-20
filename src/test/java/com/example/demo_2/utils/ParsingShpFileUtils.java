package com.example.demo_2.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ParsingShpFileUtils {
    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\www28\\Desktop\\shape\\4326-沉积相.shp");
        ArrayList<Object> list = readShapeFile(file);
        for (Object o : list) {
            System.out.println("o = " + o);
        }
//
//        Map x = ParsingShpFile("C:\\Users\\www28\\Desktop\\shape\\4326-沉积相.shp");
//        ArrayList data = (ArrayList) x.get("data");
////        for (Object datum : data) {
////            System.out.println(datum);
////        }
//        String s = JSON.toJSONString(data);
//        System.out.println(s);
//
//        System.out.println(ParsingShpFile("C:\\Users\\www28\\Desktop\\shape\\4326-沉积相.shx"));\
//        readDBF("C:\\Users\\www28\\Desktop\\shape\\4326-沉积相.dbf");
//        readSHP("C:\\Users\\www28\\Desktop\\shape\\4326-沉积相.shp");
    }

    public static void readSHP(String path) {

        ArrayList<Property> properties = new ArrayList<>();
        ShapefileDataStore shpDataStore;

        try{

            shpDataStore = new ShapefileDataStore(new File(path).toURI().toURL());

            shpDataStore.setCharset(Charset.forName("GBK"));

            String typeName = shpDataStore.getTypeNames()[0];

            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource;

            featureSource = shpDataStore.getFeatureSource(typeName);

            FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures();

            System.out.println("我是你爹"+result.size());

            FeatureIterator<SimpleFeature> itertor = result.features();

            while(itertor.hasNext()){

                SimpleFeature feature = itertor.next();

                Collection<Property> p = feature.getProperties();

                for (Property pro : p) {

                    if (pro.getValue() instanceof Point) {

                        System.out.println("PointX = " + ((Point) (pro.getValue())).getX());

                        System.out.println("PointY = " + ((Point) (pro.getValue())).getY());

                    } else {
                        if ("the_geom".equals((pro.getName().toString()))){

//                            System.out.println( pro.getValue());
                        }
                    }

                }

            }

            itertor.close();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }


    public static void readDBF(String path) {

        try {

            FileChannel in = new FileInputStream(path).getChannel();

            DbaseFileReader dbfReader =  new DbaseFileReader(in, false,  Charset.forName("GBK"));

            DbaseFileHeader header = dbfReader.getHeader();

            int fields = header.getNumFields();



            while ( dbfReader.hasNext() ){

                DbaseFileReader.Row row =  dbfReader.readRow();

//              System.out.println(row.toString());

                for (int i=0; i<fields; i++) {

                    System.out.println(header.getFieldName(i) + " : " + row.read(i));

                }

            }

            dbfReader.close();

            in.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    /**
     *
     * @param shpFile  传递的是shape文件中的.shp文件
     */
    private static ArrayList<Object> readShapeFile(File shpFile) {
        ArrayList<Object> list = new ArrayList<>();
        /*
          直接使用shapefileDatastore,如果不知道，也可以使用工厂模式(见下个方法)
          建议，如果确定是shape文件，就直使用shapefileDatastore
         */
        try {
            ShapefileDataStore shapefileDataStore = new ShapefileDataStore(shpFile.toURI().toURL());
            //这个typeNamae不传递，默认是文件名称
            FeatureSource<SimpleFeatureType, SimpleFeature> featuresource = shapefileDataStore.getFeatureSource(shapefileDataStore.getTypeNames()[0]);
            //读取bbox
            ReferencedEnvelope bbox  =featuresource.getBounds();
            //读取投影
            CoordinateReferenceSystem crs = featuresource.getSchema().getCoordinateReferenceSystem();
            //特征总数
            int count = featuresource.getCount(Query.ALL);
            //获取当前数据的geometry类型（点、线、面）
            GeometryType geometryType = featuresource.getSchema().getGeometryDescriptor().getType();
            //读取要素
            SimpleFeatureCollection simpleFeatureCollection = (SimpleFeatureCollection) featuresource.getFeatures();
            //获取当前矢量数据有哪些属性字段值
            List<AttributeDescriptor> attributes = simpleFeatureCollection.getSchema().getAttributeDescriptors();
            //
            SimpleFeatureIterator simpleFeatureIterator = simpleFeatureCollection.features();
            //
            while(simpleFeatureIterator.hasNext()) {
                SimpleFeature simpleFeature = simpleFeatureIterator.next();
                attributes.forEach((a) -> {
                    list.add(simpleFeature.getAttribute(a.getLocalName()));
                    //依次读取这个shape中每一个属性值，当然这个属性值，可以处理其它业务
//                    System.out.println(simpleFeature.getAttribute(a.getLocalName()));
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;

    }

    /**
     * 解析shp文件
     * @param filePath
     * @return
     * @throws Exception
     */
    public static Map ParsingShpFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()){
            throw new Exception("文件不存在!");
        }
        if (!filePath.endsWith("shp")){
            throw new Exception("只能指定后缀为shp的文件");
        }
        Map map = new HashMap();
        List<Map> list = new ArrayList();
        //读取shp
        SimpleFeatureCollection colls1 = readShp(filePath);
        SimpleFeatureType schema = colls1.getSchema();
        Name name = schema.getGeometryDescriptor().getType().getName();
        ReferencedEnvelope bounds = colls1.getBounds();
        //所有features
        SimpleFeatureIterator iters = colls1.features();
        String s = name.toString();
        if ("Point".equals(s)) {
            list = parsingPoint(iters);
        } else if ("MultiLineString".equals(s) || "MultiPolygon".equals(s)) {
            list = parsingLineOrPoly(iters);
        }
        map.put("data",list);
        map.put("maxX",bounds.getMaxX());
        map.put("minX",bounds.getMinX());
        map.put("maxY",bounds.getMaxY());
        map.put("minY",bounds.getMinY());
        map.put("shapeFile",name.toString());
        return map;
    }
    /**
     * 解析点数据
     *
     * @param iters
     * @return
     */
    public static List<Map> parsingPoint(SimpleFeatureIterator iters) {
        List<Map> list = new ArrayList();
        while (iters.hasNext()) {
            SimpleFeature sf = iters.next();
            Map map = new HashMap();
            Iterator<? extends Property> iterator = sf.getValue().iterator();
            while (iterator.hasNext()) {
                Property property = iterator.next();
                if (property.getValue() instanceof Point) {
                    map.put("PointX", ((Point)(property.getValue())).getX());
                    map.put("PointY", ((Point)(property.getValue())).getY());
                }else{
                    Name name = property.getName();//属性名称
                    Object value = property.getValue();//属性值
                    map.put(name,value);
                }
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 解析线和面
     *
     * @param iters
     * @return
     */
    public static List<Map> parsingLineOrPoly(SimpleFeatureIterator iters) {
        List<Map> list = new ArrayList();
        while (iters.hasNext()) {
            SimpleFeature sf = iters.next();
            Map map = new HashMap();
            Iterator<? extends Property> iterator = sf.getValue().iterator();
            while (iterator.hasNext()) {
                Property property = iterator.next();
                if (property.getValue() instanceof Geometry) {
                    Geometry geometry = (Geometry) property.getValue();
                    Coordinate[] coordinates = geometry.getCoordinates();
                    List<Map> paths = new ArrayList<Map>();
                    for (Coordinate coordinate : coordinates) {
                        Map path = new HashMap();
                        path.put("x",coordinate.x);
                        path.put("y",coordinate.y);
                        path.put("z",coordinate.z);
                        paths.add(path);
                    }
                    map.put("path",paths);
                }else{
                    Name name = property.getName();//属性名称
                    Object value = property.getValue();//属性值
                    map.put(name,value);
                }
            }
            list.add(map);
        }
        return list;
    }

    public static SimpleFeatureCollection readShp(String path) {
        return readShp(path, null);

    }

    public static SimpleFeatureCollection readShp(String path, Filter filter) {
        SimpleFeatureSource featureSource = readStoreByShp(path);
        if (featureSource == null) return null;
        try {
            return filter != null ? featureSource.getFeatures(filter) : featureSource.getFeatures();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SimpleFeatureSource readStoreByShp(String path) {
        File file = new File(path);
        FileDataStore store;
        SimpleFeatureSource featureSource = null;
        try {
            store = FileDataStoreFinder.getDataStore(file);
            ((ShapefileDataStore) store).setCharset(StandardCharsets.UTF_8);
            featureSource = store.getFeatureSource();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return featureSource;
    }
}
